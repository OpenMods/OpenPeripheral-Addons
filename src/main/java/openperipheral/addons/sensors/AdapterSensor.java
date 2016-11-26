package openperipheral.addons.sensors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openmods.utils.WorldUtils;
import openperipheral.addons.OpcAccess;
import openperipheral.api.adapter.IPeripheralAdapter;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.architecture.FeatureGroup;
import openperipheral.api.meta.IMetaProviderProxy;

@FeatureGroup("openperipheral-sensor")
public class AdapterSensor implements IPeripheralAdapter {

	private static final String DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING = "Entity not found";

	public static enum SupportedEntityTypes {
		MOB(EntityLiving.class),
		MINECART(EntityMinecart.class),
		ITEM(EntityItem.class),
		ITEM_FRAME(EntityItemFrame.class),
		PAINTING(EntityPainting.class);

		public final Class<? extends Entity> cls;

		private SupportedEntityTypes(Class<? extends Entity> cls) {
			this.cls = cls;
		}
	}

	private static enum ComputerColors {
		WHITE     (0xF0F0F0, "0"),
		ORANGE    (0xF2B233, "1"),
		MAGENTA   (0xE57FD8, "2"),
		LIGHT_BLUE(0x99B2F2, "3"),
		YELLOW    (0xDEDE6C, "4"),
		LIME      (0x7FCC19, "5"),
		PINK      (0xF2B2CC, "6"),
		GRAY      (0x4C4C4C, "7"),
		LIGHT_GRAY(0x999999, "8"),
		CYAN      (0x4C99B2, "9"),
		PURPLE    (0xB266E5, "a"),
		BLUE      (0x3366CC, "b"),
		BROWN     (0x7F664C, "c"),
		GREEN     (0x57A64E, "d"),
		RED       (0xCC4C4C, "e"),
		BLACK     (0x000000, "f");

		public final int R;
		public final int G;
		public final int B;
		public final String code;

		// LRU cache with a maximum of 1000 entries
		private static final LinkedHashMap<Integer, ComputerColors> LOOKUP_CACHE = new LinkedHashMap<Integer, ComputerColors>(16, 0.75f, true) {
		  protected boolean removeEldestEntry(Map.Entry<Integer, ComputerColors> eldest) {
		    return size() > 1000;
		  }
		};

		ComputerColors(int rgb, String code) {
			this.R = (rgb >>> 16) & 0xFF;
			this.G = (rgb >>> 8) & 0xFF;
			this.B = rgb & 0xFF;
			this.code = code;
		}

		// Formula taken from http://www.compuphase.com/cmetric.htm
		private int distanceSquared(int cr, int cg, int cb) {
			int r_mean = (this.R + cr) >>> 1;
			int r_delta = this.R - cr;
			int g_delta = this.G - cg;
			int b_delta = this.B - cb;

			int rd2 = r_delta*r_delta;
			int gd2 = g_delta*g_delta;
			int bd2 = b_delta*b_delta;

			return Math.abs((((512+r_mean)*rd2)>>8)
					   + (gd2 << 2)
					   + (((767-r_mean)*bd2)>>8));
		}

		public static ComputerColors getClosestColor(int rgb) {
			ComputerColors cached = ComputerColors.LOOKUP_CACHE.get(rgb);
			if (cached != null) {
				return cached;
			}

			int cr = (rgb >>> 16) & 0xFF;
			int cg = (rgb >>> 8) & 0xFF;
			int cb = rgb & 0xFF;

			ComputerColors lowest = null;
			int lowest_score = Integer.MAX_VALUE;

			for (ComputerColors cc : ComputerColors.values()) {
				int cc_score = cc.distanceSquared(cr, cg, cb);
				if (lowest == null || cc_score < lowest_score) {
					lowest = cc;
					lowest_score = cc_score;
				}
			}

			ComputerColors.LOOKUP_CACHE.put(rgb, lowest);
			return lowest;
		}
	}

	@Override
	public Class<?> getTargetClass() {
		return ISensorEnvironment.class;
	}

	@Override
	public String getSourceId() {
		return "openperipheral_sensor";
	}

	private static AxisAlignedBB getBoundingBox(Vec3 location, double range) {
		return AxisAlignedBB.getBoundingBox(
				location.xCoord, location.yCoord, location.zCoord,
				location.xCoord + 1, location.yCoord + 1, location.zCoord + 1)
				.expand(range, range, range);
	}

	private static AxisAlignedBB getBoundingBox(ISensorEnvironment env) {
		return getBoundingBox(env.getLocation(), env.getSensorRange());
	}

	private static List<Integer> listEntityIds(ISensorEnvironment env, Class<? extends Entity> entityClass) {
		List<Integer> ids = Lists.newArrayList();

		final AxisAlignedBB aabb = getBoundingBox(env);
		for (Entity entity : WorldUtils.getEntitiesWithinAABB(env.getWorld(), entityClass, aabb))
			ids.add(entity.getEntityId());

		return ids;
	}

	private static IMetaProviderProxy getEntityInfoById(ISensorEnvironment sensor, int mobId, Class<? extends Entity> cls) {
		Entity mob = sensor.getWorld().getEntityByID(mobId);
		Preconditions.checkArgument(cls.isInstance(mob), DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING);
		return getEntityInfo(sensor, mob);
	}

	private static IMetaProviderProxy getPlayerInfo(ISensorEnvironment sensor, String username) {
		EntityPlayer player = sensor.getWorld().getPlayerEntityByName(username);
		return getEntityInfo(sensor, player);
	}

	private static IMetaProviderProxy getPlayerInfo(ISensorEnvironment sensor, UUID uuid) {
		EntityPlayer player = sensor.getWorld().func_152378_a(uuid);
		return getEntityInfo(sensor, player);
	}

	private static IMetaProviderProxy getEntityInfo(ISensorEnvironment sensor, Entity mob) {
		Preconditions.checkNotNull(mob, DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING);
		final AxisAlignedBB aabb = getBoundingBox(sensor);

		Preconditions.checkArgument(mob.boundingBox.intersectsWith(aabb), DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING);
		final Vec3 sensorPos = sensor.getLocation();
		return OpcAccess.entityMetaBuilder.createProxy(mob, sensorPos);
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get the ids of all the mobs in range. Deprecated, please use getEntityIds('mob')")
	public List<Integer> getMobIds(ISensorEnvironment env) {
		return listEntityIds(env, EntityLiving.class);
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get full details of a particular mob if it's in range. Deprecated, please use getEntityData(id, 'mob')")
	public IMetaProviderProxy getMobData(ISensorEnvironment sensor,
			@Arg(name = "mobId", description = "The id retrieved from getMobIds()") int id) {
		return getEntityInfoById(sensor, id, EntityLiving.class);
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get the ids of all the minecarts in range. Deprecated, please use getEntityIds('minecart')")
	public List<Integer> getMinecartIds(ISensorEnvironment env) {
		return listEntityIds(env, EntityMinecart.class);
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get full details of a particular minecart if it's in range. Deprecated, please use getEntityIds(id, 'minecraft')")
	public IMetaProviderProxy getMinecartData(ISensorEnvironment sensor,
			@Arg(name = "minecartId", description = "The id retrieved from getMinecartIds()") int id) {
		return getEntityInfoById(sensor, id, EntityMinecart.class);
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get the ids of all entities of single type in range")
	public List<Integer> getEntityIds(ISensorEnvironment env, @Arg(name = "type") SupportedEntityTypes type) {
		return listEntityIds(env, type.cls);
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get full details of a particular entity if it's in range")
	public IMetaProviderProxy getEntityData(ISensorEnvironment sensor,
			@Arg(name = "id", description = "The id retrieved from getEntityIds()") int id,
			@Arg(name = "type") SupportedEntityTypes type) {
		return getEntityInfoById(sensor, id, type.cls);
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get the usernames of all the players in range")
	public List<GameProfile> getPlayers(ISensorEnvironment env) {
		List<EntityPlayer> players = WorldUtils.getEntitiesWithinAABB(env.getWorld(), EntityPlayer.class, getBoundingBox(env));

		List<GameProfile> names = Lists.newArrayList();
		for (EntityPlayer player : players)
			names.add(player.getGameProfile());

		return names;
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get full details of a particular player if they're in range")
	public IMetaProviderProxy getPlayerByName(ISensorEnvironment env,
			@Arg(name = "username", description = "The players username") String username) {
		return getPlayerInfo(env, username);
	}

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get full details of a particular player if they're in range")
	public IMetaProviderProxy getPlayerByUUID(ISensorEnvironment env,
			@Arg(name = "uuid", description = "The players uuid") UUID uuid) {
		return getPlayerInfo(env, uuid);
	}

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get a table of information about the surrounding area. Includes map color and whether each block is UNKNOWN, AIR, LIQUID, or SOLID.")
	public Map<Integer, Map<String, Object>> sonicScan(ISensorEnvironment env) {

		int range = 1 + env.getSensorRange() / 2;
		World world = env.getWorld();
		Map<Integer, Map<String, Object>> results = Maps.newHashMap();
		Vec3 sensorPos = env.getLocation();
		int sx = MathHelper.floor_double(sensorPos.xCoord);
		int sy = MathHelper.floor_double(sensorPos.yCoord);
		int sz = MathHelper.floor_double(sensorPos.zCoord);

		final int rangeSq = range * range;
		int i = 0;
		for (int x = -range; x <= range; x++) {
			for (int y = -range; y <= range; y++) {
				for (int z = -range; z <= range; z++) {
					final int bx = sx + x;
					final int by = sy + y;
					final int bz = sz + z;
					if (!world.blockExists(bx, by, bz)) continue;

					final int distSq = x * x + y * y + z * z;
					if (distSq == 0 || distSq > rangeSq) continue;
					Block block = world.getBlock(bx, by, bz);

					String type;
					if (block == null || world.isAirBlock(bx, by, bz)) type = "AIR";
					else if (block.getMaterial().isLiquid()) type = "LIQUID";
					else if (block.getMaterial().isSolid()) type = "SOLID";
					else type = "UNKNOWN";

					final String color;
					if (block == null) {
						color = "f"; // Default black -- same as air.
					} else {
						int rgb = block.getMapColor(block.getDamageValue(world, bx, by, bz)).colorValue;
						color = ComputerColors.getClosestColor(rgb).code;
					}

					Map<String, Object> tmp = Maps.newHashMap();
					tmp.put("x", x);
					tmp.put("y", y);
					tmp.put("z", z);
					tmp.put("type", type);
					tmp.put("color", color);
					results.put(++i, tmp);

				}
			}
		}
		return results;
	}

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get a table of information about a single block in the surrounding area. Includes map color and whether each block is UNKNOWN, AIR, LIQUID, or SOLID.")
	public Map<String, Object> sonicScanTarget(ISensorEnvironment env,
			@Arg(name = "xOffset", description = "The target's offset from the sensor on the X-Axis.") int xOff,
			@Arg(name = "yOffset", description = "The target's offset from the sensor on the Y-Axis.") int yOff,
			@Arg(name = "zOffset", description = "The target's offset from the sensor on the Z-Axis.") int zOff) {

		int range = 1 + env.getSensorRange() / 2;
		World world = env.getWorld();
		Vec3 sensorPos = env.getLocation();
		int sx = MathHelper.floor_double(sensorPos.xCoord);
		int sy = MathHelper.floor_double(sensorPos.yCoord);
		int sz = MathHelper.floor_double(sensorPos.zCoord);

		final int rangeSq = range * range;

		final int bx = sx + xOff;
		final int by = sy + yOff;
		final int bz = sz + zOff;
		if (!world.blockExists(bx, by, bz)) return null;

		final int distSq = xOff * xOff + yOff * yOff + zOff * zOff;
		if (distSq == 0 || distSq > rangeSq) return null;
		Block block = world.getBlock(bx, by, bz);

		String type;
		if (block == null || world.isAirBlock(bx, by, bz)) type = "AIR";
		else if (block.getMaterial().isLiquid()) type = "LIQUID";
		else if (block.getMaterial().isSolid()) type = "SOLID";
		else type = "UNKNOWN";

		final String color;
		if (block == null) {
			color = "f"; // Default black -- same as air.
		} else {
			int rgb = block.getMapColor(block.getDamageValue(world, bx, by, bz)).colorValue;
			color = ComputerColors.getClosestColor(rgb).code;
		}

		Map<String, Object> tmp = Maps.newHashMap();
		tmp.put("x", xOff);
		tmp.put("y", yOff);
		tmp.put("z", zOff);
		tmp.put("type", type);
		tmp.put("color", color);

		return tmp;
	}
}
