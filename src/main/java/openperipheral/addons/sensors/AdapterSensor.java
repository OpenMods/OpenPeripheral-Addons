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
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.RGB;
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

	// LRU cache with a maximum of 1000 entries; maps packed-RGB int -> color-bitmask int
	@SuppressWarnings("serial")
	private static final LinkedHashMap<Integer, Integer> COLOR_CACHE = new LinkedHashMap<Integer, Integer>(16, 0.75f, true) {
	  protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
	    return size() > 1000;
	  }
	};

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

	private static String getBlockType(ISensorEnvironment sensor, int xPos, int yPos, int zPos) {
		World world = sensor.getWorld();

		if (!world.blockExists(xPos, yPos, zPos)) return null;

		Block block = world.getBlock(xPos, yPos, zPos);

		if (block == null || world.isAirBlock(xPos, yPos, zPos)) return "AIR";
		else if (block.getMaterial().isLiquid()) return "LIQUID";
		else if (block.getMaterial().isSolid()) return "SOLID";
		else return "UNKNOWN";
	}

	private static int getBlockColorBitmask(ISensorEnvironment sensor, int xPos, int yPos, int zPos) {
		World world = sensor.getWorld();

		if (!world.blockExists(xPos, yPos, zPos)) return 0;

		Block block = world.getBlock(xPos, yPos, zPos);

		if (block == null) {
			return ColorUtils.ColorMeta.BLACK.bitmask; // Default black -- same as air.
		} else {
			int color = block.getMapColor(block.getDamageValue(world, xPos, yPos, zPos)).colorValue;
			Integer cached = COLOR_CACHE.get(color);
			if (cached != null) {
				return cached;
			}

			RGB rgb = new RGB(color);
			Integer nearestColorBitmask = ColorUtils.findNearestColor(rgb, 255).bitmask;
			COLOR_CACHE.put(color, nearestColorBitmask);
			return nearestColorBitmask;
		}
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

					final int distSq = x * x + y * y + z * z;
					if (distSq == 0 || distSq > rangeSq) continue;

					final String type = getBlockType(env, bx, by, bz);
					if (type == null) continue;

					final int color = getBlockColorBitmask(env, bx, by, bz);

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
		Vec3 sensorPos = env.getLocation();
		final int rangeSq = range * range;

		final int distSq = xOff * xOff + yOff * yOff + zOff * zOff;
		if (distSq == 0 || distSq > rangeSq) return null;

		final int bx = MathHelper.floor_double(sensorPos.xCoord) + xOff;
		final int by = MathHelper.floor_double(sensorPos.yCoord) + yOff;
		final int bz = MathHelper.floor_double(sensorPos.zCoord) + zOff;

		final String type = getBlockType(env, bx, by, bz);
		if (type == null) return null;

		final int color = getBlockColorBitmask(env, bx, by, bz);

		Map<String, Object> tmp = Maps.newHashMap();
		tmp.put("x", xOff);
		tmp.put("y", yOff);
		tmp.put("z", zOff);
		tmp.put("type", type);
		tmp.put("color", color);

		return tmp;
	}
}
