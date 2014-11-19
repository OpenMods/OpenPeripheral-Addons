package openperipheral.addons.sensors;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openmods.utils.WorldUtils;
import openperipheral.api.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

public class AdapterSensor implements IPeripheralAdapter {

	private static final String DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING = "Entity not found";

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

	private static Map<String, Object> getEntityInfoById(ISensorEnvironment sensor, int mobId) {
		Entity mob = sensor.getWorld().getEntityByID(mobId);
		return getEntityInfo(sensor, mob);
	}

	private static Map<String, Object> getPlayerInfo(ISensorEnvironment sensor, String username) {
		EntityPlayer player = sensor.getWorld().getPlayerEntityByName(username);
		return getEntityInfo(sensor, player);
	}

	private static Map<String, Object> getPlayerInfo(ISensorEnvironment sensor, UUID uuid) {
		EntityPlayer player = sensor.getWorld().func_152378_a(uuid);
		return getEntityInfo(sensor, player);
	}

	protected static Map<String, Object> getEntityInfo(ISensorEnvironment sensor, Entity mob) {
		Preconditions.checkNotNull(mob, DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING);
		final AxisAlignedBB aabb = getBoundingBox(sensor);

		Preconditions.checkArgument(mob.boundingBox.intersectsWith(aabb), DONT_EVER_CHANGE_THIS_TEXT_OTHERWISE_YOU_WILL_RUIN_EVERYTHING);
		final Vec3 sensorPos = sensor.getLocation();
		return ApiAccess.getApi(IEntityMetaBuilder.class).getEntityMetadata(mob, sensorPos);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get the usernames of all the players in range")
	public List<GameProfile> getPlayers(ISensorEnvironment env) {
		List<EntityPlayer> players = WorldUtils.getEntitiesWithinAABB(env.getWorld(), EntityPlayer.class, getBoundingBox(env));

		List<GameProfile> names = Lists.newArrayList();
		for (EntityPlayer player : players)
			names.add(player.getGameProfile());

		return names;
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get the ids of all the mobs in range")
	public List<Integer> getMobIds(ISensorEnvironment env) {
		return listEntityIds(env, EntityLiving.class);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get the ids of all the minecarts in range")
	public List<Integer> getMinecartIds(ISensorEnvironment env) {
		return listEntityIds(env, EntityMinecart.class);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get full details of a particular player if they're in range")
	public Map<?, ?> getPlayerByName(ISensorEnvironment env,
			@Arg(name = "username", description = "The players username") String username) {
		return getPlayerInfo(env, username);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get full details of a particular player if they're in range")
	public Map<?, ?> getPlayerByUUID(ISensorEnvironment env,
			@Arg(name = "uuid", description = "The players uuid") String uuid) {
		UUID parsedUUID = UUID.fromString(uuid);
		return getPlayerInfo(env, parsedUUID);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get full details of a particular mob if it's in range")
	public Map<String, Object> getMobData(ISensorEnvironment sensor,
			@Arg(name = "mobId", description = "The mob id retrieved from getMobIds()") int mobId) {
		return getEntityInfoById(sensor, mobId);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get full details of a particular minecart if it's in range")
	public Map<?, ?> getMinecartData(ISensorEnvironment sensor,
			@Arg(name = "minecartId", description = "The minecart id retrieved from getMobIds()") int minecartId) {
		return getEntityInfoById(sensor, minecartId);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get a table of information about the surrounding area. Includes whether each block is UNKNOWN, AIR, LIQUID or SOLID")
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
