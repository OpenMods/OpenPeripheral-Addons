package openperipheral.addons.glasses;

import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import openmods.tileentity.OpenTileEntity;
import openperipheral.adapter.AdapterManager;
import openperipheral.addons.glasses.TerminalEvent.TerminalClearEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
import openperipheral.api.*;
import openperipheral.api.cc15x.IAttachable;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaObject;

@Freeform
public class TileEntityGlassesBridge extends OpenTileEntity implements IAttachable {

	private static final String EVENT_CHAT_MESSAGE = "chat_command";

	private static final String EVENT_PLAYER_JOIN = "registered_player_join";

	private static class PlayerInfo {
		public final WeakReference<EntityPlayer> player;
		public SurfaceServer surface;

		public PlayerInfo(TileEntityGlassesBridge parent, EntityPlayer player) {
			this.player = new WeakReference<EntityPlayer>(player);
			this.surface = new SurfaceServer(player.getEntityName());
		}
	}

	private final Map<String, PlayerInfo> knownPlayers = Maps.newHashMap();
	private final Set<EntityPlayer> newPlayers = Sets.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());

	private List<IComputerAccess> computers = Lists.newArrayList();

	public SurfaceServer globalSurface = new SurfaceServer(TerminalUtils.GLOBAL_MARKER);
	private long guid = TerminalUtils.generateGuid();

	public TileEntityGlassesBridge() {}

	public void registerTerminal(EntityPlayer player) {
		if (!knownPlayers.containsKey(player.getEntityName())) newPlayers.add(player);
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

		TerminalDataEvent globalChange = null;

		final boolean globalUpdate = globalSurface.hasUpdates();

		Iterator<PlayerInfo> it = knownPlayers.values().iterator();
		while (it.hasNext()) {
			final PlayerInfo info = it.next();
			final EntityPlayer player = info.player.get();

			if (!isPlayerValid(player)) {
				sendCleanPackets(player);
				it.remove();
				continue;
			}

			if (globalUpdate) {
				if (globalChange == null) globalChange = TerminalManagerServer.createUpdateDataEvent(globalSurface, guid, false);
				globalChange.sendToPlayer(player);
			}

			final SurfaceServer privateSurface = info.surface;
			if (privateSurface != null && privateSurface.hasUpdates()) {
				TerminalDataEvent privateData = TerminalManagerServer.createUpdateDataEvent(privateSurface, guid, true);
				privateData.sendToPlayer(player);
			}
		}

		TerminalDataEvent globalFull = null;

		for (EntityPlayer newPlayer : newPlayers) {
			if (isPlayerValid(newPlayer)) {
				if (globalFull == null) globalFull = TerminalManagerServer.createFullDataEvent(globalSurface, guid, false);
				globalFull.sendToPlayer(newPlayer);

				final String playerName = newPlayer.getEntityName();
				knownPlayers.put(playerName, new PlayerInfo(this, newPlayer));
				onPlayerJoin(playerName);
			}
		}

		newPlayers.clear();
	}

	private void sendCleanPackets(EntityPlayer player) {
		new TerminalClearEvent(guid, false).sendToPlayer(player);
		new TerminalClearEvent(guid, true).sendToPlayer(player);
	}

	private boolean isPlayerValid(EntityPlayer player) {
		if (player == null) return false;

		if (player.isDead && !isPlayerLogged(player)) return false;

		Long guid = TerminalUtils.tryGetTerminalGuid(player);
		return guid != null && guid == this.guid;
	}

	private static boolean isPlayerLogged(EntityPlayer player) {
		@SuppressWarnings("unchecked")
		List<EntityPlayer> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for (EntityPlayer p : players)
			if (p.username.equals(player.username)) return true;

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

	public void onPlayerJoin(String playerName) {
		for (IComputerAccess computer : computers) {
			computer.queueEvent(EVENT_PLAYER_JOIN, new Object[] { playerName });
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
		PlayerInfo info = knownPlayers.get(username);
		return info != null? info.surface : null;
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get the names of all the users linked up to this bridge")
	public List<String> getUsers() {
		return ImmutableList.copyOf(knownPlayers.keySet());
	}

	@LuaCallable(returnTypes = LuaType.STRING, name = "getGuid", description = "Get the Guid of this bridge")
	public String getGuidString() {
		return TerminalUtils.formatTerminalId(guid);
	}

	public long getGuid() {
		return guid;
	}

	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Get the display width of some text")
	public int getStringWidth(@Arg(name = "text", description = "The text you want to measure", type = LuaType.STRING) String text) {
		return GlassesRenderingUtils.getStringWidth(text);
	}

	@LuaCallable(returnTypes = LuaType.OBJECT, description = "Get the surface of a user to draw privately on their screen")
	public ILuaObject getUserSurface(@Arg(name = "username", description = "The username of the user to get the draw surface for", type = LuaType.STRING) String username) {
		SurfaceServer playerSurface = getSurface(username);
		Preconditions.checkNotNull(playerSurface, "Invalid player");
		return AdapterManager.wrapObject(playerSurface);
	}

	@Include
	public SurfaceServer getGlobalSurface() {
		return globalSurface;
	}
}
