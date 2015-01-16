package openperipheral.addons.glasses;

import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import openmods.include.IncludeInterface;
import openmods.tileentity.OpenTileEntity;
import openperipheral.addons.glasses.TerminalEvent.TerminalClearEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
import openperipheral.api.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import dan200.computercraft.api.peripheral.IComputerAccess;

@PeripheralTypeId("openperipheral_bridge")
public class TileEntityGlassesBridge extends OpenTileEntity implements IAttachable {

	private static final String EVENT_CHAT_MESSAGE = "chat_command";

	private static final String EVENT_PLAYER_JOIN = "registered_player_join";

	private static class PlayerInfo {
		public final GameProfile profile;
		public final WeakReference<EntityPlayerMP> player;
		public SurfaceServer surface;

		public PlayerInfo(TileEntityGlassesBridge parent, EntityPlayerMP player) {
			this.player = new WeakReference<EntityPlayerMP>(player);
			this.profile = player.getGameProfile();
			this.surface = new SurfaceServer();
		}
	}

	private final Map<UUID, PlayerInfo> knownPlayersByUUID = Maps.newHashMap();
	private final Map<String, PlayerInfo> knownPlayersByName = Maps.newHashMap();
	private final Set<EntityPlayerMP> newPlayers = Sets.newSetFromMap(new WeakHashMap<EntityPlayerMP, Boolean>());

	private List<IComputerAccess> computers = Lists.newArrayList();

	private long guid = TerminalUtils.generateGuid();

	@IncludeInterface(IDrawableContainer.class)
	private SurfaceServer globalSurface = new SurfaceServer();

	public TileEntityGlassesBridge() {}

	public void registerTerminal(EntityPlayerMP player) {
		if (!knownPlayersByUUID.containsKey(player.getGameProfile().getId())) newPlayers.add(player);
	}

	public void onChatCommand(String command, String username) {
		for (IComputerAccess computer : computers) {
			computer.queueEvent(EVENT_CHAT_MESSAGE, new Object[] { command, username, guid, computer.getAttachmentName() });
		}
	}

	@Override
	public void validate() {
		super.validate();
		TerminalManagerServer.instance.registerBridge(guid, this);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote || globalSurface == null) return;

		Iterator<PlayerInfo> it = knownPlayersByUUID.values().iterator();
		while (it.hasNext()) {
			final PlayerInfo info = it.next();
			final EntityPlayerMP player = info.player.get();

			if (!isPlayerValid(player)) {
				sendCleanPackets(player);
				it.remove();
			}
		}

		for (EntityPlayerMP newPlayer : newPlayers) {
			if (isPlayerValid(newPlayer)) {
				final PlayerInfo playerInfo = new PlayerInfo(this, newPlayer);
				final GameProfile gameProfile = newPlayer.getGameProfile();

				knownPlayersByUUID.put(gameProfile.getId(), playerInfo);
				knownPlayersByName.put(gameProfile.getName(), playerInfo);
				onPlayerJoin(gameProfile);
			}
		}
	}

	private void sendCleanPackets(EntityPlayerMP player) {
		new TerminalClearEvent(guid, false).sendToPlayer(player);
		new TerminalClearEvent(guid, true).sendToPlayer(player);
	}

	private boolean isPlayerValid(EntityPlayerMP player) {
		if (player == null) return false;

		if (player.isDead && !isPlayerLogged(player)) return false;

		Long guid = TerminalUtils.tryGetTerminalGuid(player);
		return guid != null && guid == this.guid;
	}

	private static boolean isPlayerLogged(EntityPlayerMP player) {
		final GameProfile gameProfile = player.getGameProfile();
		@SuppressWarnings("unchecked")
		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for (EntityPlayerMP p : players) {
			if (p.getGameProfile().equals(gameProfile)) return true;
		}

		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setLong("guid", guid);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		Long guid = TerminalUtils.extractGuid(tag);
		if (guid != null) this.guid = guid;
	}

	public void onPlayerJoin(GameProfile player) {
		for (IComputerAccess computer : computers) {
			computer.queueEvent(EVENT_PLAYER_JOIN, new Object[] { player.getName(), player.getId() });
		}
	}

	@Override
	public void addComputer(IComputerAccess computer) {
		if (!computers.contains(computer)) {
			computers.add(computer);
		}
	}

	@Override
	public void removeComputer(IComputerAccess computer) {
		computers.remove(computer);
	}

	public SurfaceServer getSurface(String username) {
		if (TerminalUtils.GLOBAL_MARKER.equals(username)) return globalSurface;
		PlayerInfo info = knownPlayersByName.get(username);
		return info != null? info.surface : null;
	}

	public SurfaceServer getSurface(UUID uuid) {
		if (TerminalUtils.GLOBAL_SURFACE_UUID.equals(uuid)) return globalSurface;
		PlayerInfo info = knownPlayersByUUID.get(uuid);
		return info != null? info.surface : null;
	}

	// never, ever make this asynchronous
	@LuaCallable(description = "Send updates to client. Without it changes won't be visible", name = "sync")
	public void syncContents() {
		TerminalDataEvent globalChange = null;

		final boolean sendGlobalUpdate = globalSurface.hasUpdates();

		synchronized (globalSurface) {
			for (PlayerInfo info : knownPlayersByUUID.values()) {
				final EntityPlayerMP player = info.player.get();

				if (!isPlayerValid(player)) continue;

				if (sendGlobalUpdate) {
					if (globalChange == null) globalChange = TerminalManagerServer.createUpdateDataEvent(globalSurface, guid, false);
					globalChange.sendToPlayer(player);
				}

				final SurfaceServer privateSurface = info.surface;

				if (privateSurface != null) {
					synchronized (privateSurface) {
						if (privateSurface.hasUpdates()) {
							TerminalDataEvent privateData = TerminalManagerServer.createUpdateDataEvent(privateSurface, guid, true);
							privateData.sendToPlayer(player);
						}
					}
				}
			}

			TerminalDataEvent globalFull = null;

			for (EntityPlayerMP newPlayer : newPlayers) {
				if (isPlayerValid(newPlayer)) {
					if (globalFull == null) globalFull = TerminalManagerServer.createFullDataEvent(globalSurface, guid, false);
					globalFull.sendToPlayer(newPlayer);
				}
			}

		}
		newPlayers.clear();
	}

	@Asynchronous
	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get the names of all the users linked up to this bridge")
	public List<GameProfile> getUsers() {
		List<GameProfile> result = Lists.newArrayList();
		for (PlayerInfo info : knownPlayersByName.values())
			result.add(info.profile);

		return result;
	}

	@Asynchronous
	@LuaCallable(returnTypes = LuaReturnType.STRING, name = "getGuid", description = "Get the Guid of this bridge")
	public String getGuidString() {
		return TerminalUtils.formatTerminalId(guid);
	}

	public long getGuid() {
		return guid;
	}

	@Asynchronous
	@LuaCallable(returnTypes = LuaReturnType.NUMBER, description = "Get the display width of some text")
	public int getStringWidth(@Arg(name = "text", description = "The text you want to measure") String text) {
		return GlassesRenderingUtils.getStringWidth(text);
	}

	@Asynchronous
	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Get the surface of a user to draw privately on their screen")
	public IDrawableContainer getSurfaceByName(@Arg(name = "username", description = "The username of the user to get the draw surface for") String username) {
		SurfaceServer playerSurface = getSurface(username);
		Preconditions.checkNotNull(playerSurface, "Invalid player");
		return playerSurface;
	}

	@Asynchronous
	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Get the surface of a user to draw privately on their screen")
	public IDrawableContainer getSurfaceByUUID(@Arg(name = "uuid", description = "The uuid of the user to get the draw surface for") String username) {
		UUID uuid = UUID.fromString(username);
		SurfaceServer playerSurface = getSurface(uuid);
		Preconditions.checkNotNull(playerSurface, "Invalid player");
		return playerSurface;
	}
}
