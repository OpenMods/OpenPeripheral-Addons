package openperipheral.addons.sensors;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import openperipheral.addons.OpcAccess;
import openperipheral.api.adapter.IPeripheralAdapter;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.meta.IMetaProviderProxy;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

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

	@Override
	public Class<?> getTargetClass() {
		return ISensorEnvironment.class;
	}

	@Override
	public String getSourceId() {
		return "openperipheral_sensor";
	}

	private static AxisAlignedBB getBoundingBox(Vec3 location, double range) {
		return AxisAlignedBB.fromBounds(
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
		for (Entity entity : env.getWorld().getEntitiesWithinAABB(entityClass, aabb))
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
		EntityPlayer player = sensor.getWorld().getPlayerEntityByUUID(uuid);
		return getEntityInfo(sensor, player);
	}

	private static IMetaProviderProxy getEntityInfo(ISensorEnvironment sensor, Entity mob) {
		Preconditions.checkNotNull(mob, DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING);
		final AxisAlignedBB aabb = getBoundingBox(sensor);

		Preconditions.checkArgument(mob.getEntityBoundingBox().intersectsWith(aabb), DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING);
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
		List<EntityPlayer> players = env.getWorld().getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox(env));

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

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get a table of information about the surrounding area. Includes whether each block is UNKNOWN, AIR, LIQUID or SOLID")
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
					final BlockPos pos = new BlockPos(sx + x, sy + y, sz + z);
					if (!world.isBlockLoaded(pos)) continue;

					final int distSq = x * x + y * y + z * z;
					if (distSq == 0 || distSq > rangeSq) continue;
					Block block = world.getBlockState(pos).getBlock();

					String type;
					if (block == null || world.isAirBlock(pos)) type = "AIR";
					else if (block.getMaterial().isLiquid()) type = "LIQUID";
					else if (block.getMaterial().isSolid()) type = "SOLID";
					else type = "UNKNOWN";

					Map<String, Object> tmp = Maps.newHashMap();
					tmp.put("x", x);
					tmp.put("y", y);
					tmp.put("z", z);
					tmp.put("type", type);
					results.put(++i, tmp);

				}
			}
		}
		return results;
	}
}
