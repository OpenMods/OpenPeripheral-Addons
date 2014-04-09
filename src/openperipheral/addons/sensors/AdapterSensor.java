package openperipheral.addons.sensors;

import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import openperipheral.api.*;
import openperipheral.util.EntityUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@OnTick
@Prefixed("target")
public class AdapterSensor implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return ISensorEnvironment.class;
	}

	private static AxisAlignedBB getBoundingBox(ChunkCoordinates location, double range) {
		return AxisAlignedBB.getAABBPool().getAABB(location.posX, location.posY, location.posZ, location.posX + 1, location.posY + 1, location.posZ + 1).expand(range, range, range);
	}

	private static List<Integer> listEntityIds(ISensorEnvironment env, Class<? extends Entity> entityClass) {
		@SuppressWarnings("unchecked")
		List<Entity> entities = env.getWorld().getEntitiesWithinAABB(entityClass, getBoundingBox(env.getLocation(), env.getSensorRange()));

		List<Integer> ids = Lists.newArrayList();
		for (Entity entity : entities)
			ids.add(entity.entityId);

		return ids;
	}

	private static Map<String, Object> getEntityInfo(ISensorEnvironment sensor, int mobId) {
		Entity mob = sensor.getWorld().getEntityByID(mobId);
		return mob != null? EntityUtils.entityToMap(mob, sensor.getLocation()) : null;
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get the usernames of all the players in range")
	public List<String> getPlayerNames(ISensorEnvironment env) {
		@SuppressWarnings("unchecked")
		List<EntityPlayer> players = env.getWorld().getEntitiesWithinAABB(EntityPlayer.class, getBoundingBox(env.getLocation(), env.getSensorRange()));

		List<String> names = Lists.newArrayList();
		for (EntityPlayer player : players)
			names.add(player.username);

		return names;
	}

	@LuaCallable(returnTypes = { LuaType.TABLE }, description = "Get the ids of all the mobs in range")
	public List<Integer> getMobIds(ISensorEnvironment env) {
		return listEntityIds(env, EntityLiving.class);
	}

	@LuaCallable(returnTypes = { LuaType.TABLE }, description = "Get the ids of all the minecarts in range")
	public List<Integer> getMinecartIds(ISensorEnvironment env) {
		return listEntityIds(env, EntityMinecart.class);
	}

	@LuaCallable(returnTypes = { LuaType.TABLE }, description = "Get full details of a particular player if they're in range")
	public Map<?, ?> getPlayerData(ISensorEnvironment env,
			@Arg(type = LuaType.STRING, name = "username", description = "The players username") String username) {
		EntityPlayer player = env.getWorld().getPlayerEntityByName(username);
		return player != null? EntityUtils.entityToMap(player, env.getLocation()) : null;
	}

	@LuaCallable(returnTypes = { LuaType.TABLE }, description = "Get full details of a particular mob if it's in range")
	public Map<String, Object> getMobData(ISensorEnvironment sensor,
			@Arg(type = LuaType.NUMBER, name = "mobId", description = "The mob id retrieved from getMobIds()") int mobId) {
		return getEntityInfo(sensor, mobId);
	}

	@LuaCallable(returnTypes = { LuaType.TABLE }, description = "Get full details of a particular minecart if it's in range")
	public Map<?, ?> getMinecartData(ISensorEnvironment sensor,
			@Arg(type = LuaType.NUMBER, name = "minecartId", description = "The minecart id retrieved from getMobIds()") int minecartId) {
		return getEntityInfo(sensor, minecartId);
	}

	@LuaCallable(returnTypes = { LuaType.TABLE }, description = "Get a table of information about the surrounding area. Includes whether each block is UNKNOWN, AIR, LIQUID or SOLID")
	public Map<Integer, Map<String, Object>> sonicScan(ISensorEnvironment env) {

		int range = 1 + env.getSensorRange() / 2;
		World world = env.getWorld();
		Map<Integer, Map<String, Object>> results = Maps.newHashMap();
		ChunkCoordinates sensorPos = env.getLocation();
		int sx = sensorPos.posX;
		int sy = sensorPos.posY;
		int sz = sensorPos.posZ;

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
					int id = world.getBlockId(bx, by, bz);
					Block block = Block.blocksList[id];

					String type;
					if (block == null || world.isAirBlock(bx, by, bz)) type = "AIR";
					else if (block.blockMaterial.isLiquid()) type = "LIQUID";
					else if (block.blockMaterial.isSolid()) type = "SOLID";
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
