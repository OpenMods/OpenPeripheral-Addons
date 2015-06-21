package openperipheral.addons.glasses;

import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.api.ICustomHarvestDrops;
import openmods.api.IPlaceAwareTile;
import openmods.include.IncludeInterface;
import openmods.include.IncludeOverride;
import openmods.network.event.NetworkEventManager;
import openmods.network.senders.ITargetedPacketSender;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ItemUtils;
import openperipheral.addons.glasses.GlassesEvent.GlassesChangeBackground;
import openperipheral.addons.glasses.GlassesEvent.GlassesClientEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetGuiVisibility;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetKeyRepeat;
import openperipheral.addons.glasses.GlassesEvent.GlassesStopCaptureEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalClearEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;
import openperipheral.addons.utils.GuiUtils.GuiElements;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.Doc;
import openperipheral.api.adapter.method.*;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.peripheral.PeripheralTypeId;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

@Doc({ "This peripheral is used to control terminal glasses and wireless keyboard.",
		"There is one global surface and one private surface for every glasses user.",
		"All calls names .add*() will return object that can be later used to modify it.",
		"To make changes visible to players, call .sync().",
		"This peripheral signals few events. Full list available here: http://goo.gl/8Hf2yA",
		"Simple demo: http://goo.gl/n5HPN8" })
@PeripheralTypeId("openperipheral_bridge")
public class TileEntityGlassesBridge extends OpenTileEntity implements IAttachable, IPlaceAwareTile, ICustomHarvestDrops {

	public static final String TAG_GUID = "guid";

	private static final String EVENT_PLAYER_ATTACH = "glasses_attach";

	private static final String EVENT_PLAYER_DETACH = "glasses_detach";

	private static class PlayerInfo {
		public final GameProfile profile;
		public final WeakReference<EntityPlayerMP> player;
		public SurfaceServer surface;

		public PlayerInfo(EntityPlayerMP player) {
			this.player = new WeakReference<EntityPlayerMP>(player);
			this.profile = player.getGameProfile();
			this.surface = new SurfaceServer(true);
		}
	}

	@ScriptObject
	@Asynchronous
	@AdapterSourceName("glasses-capture")
	public class CaptureControl {
		private final WeakReference<EntityPlayerMP> player;

		public CaptureControl(WeakReference<EntityPlayerMP> player) {
			this.player = player;
		}

		protected EntityPlayer getPlayer() {
			EntityPlayer player = this.player.get();
			if (player == null) throw new IllegalStateException("Object is no longer valid");
			return player;
		}

		@ScriptCallable(description = "Stops capture for player")
		public void stopCapturing() {
			EntityPlayer player = getPlayer();
			new GlassesStopCaptureEvent(guid).sendToPlayer(player);
		}

		@ScriptCallable(description = "Set background on capture mode screen")
		public void setBackground(@Arg(name = "background") int background,
				@Optionals @Arg(name = "alpha") Integer alpha) {
			EntityPlayer player = getPlayer();
			final int a = alpha != null? (alpha << 24) : 0x2A000000;
			new GlassesChangeBackground(guid, background & 0x00FFFFFF | a).sendToPlayer(player);
		}

		@ScriptCallable(description = "When enabled, holding key down for long time will generate multiple events")
		public void setKeyRepeat(@Arg(name = "isEnabled") boolean keyRepeat) {
			EntityPlayer player = getPlayer();
			new GlassesSetKeyRepeat(guid, keyRepeat).sendToPlayer(player);
		}

		@ScriptCallable(description = "Sets visiblity state of various vanilla GUI elements")
		public void toggleGuiElements(@Arg(name = "visibility") Map<GuiElements, Boolean> visiblity) {
			EntityPlayer player = getPlayer();
			new GlassesSetGuiVisibility(guid, visiblity).sendToPlayer(player);
		}
	}

	private final Map<UUID, PlayerInfo> knownPlayersByUUID = Maps.newHashMap();
	private final Map<String, PlayerInfo> knownPlayersByName = Maps.newHashMap();
	private List<Object> lastSyncPackets;

	private Set<IArchitectureAccess> computers = Sets.newIdentityHashSet();

	private long guid;

	@IncludeInterface(IDrawableContainer.class)
	private SurfaceServer globalSurface = new SurfaceServer(false);

	public TileEntityGlassesBridge() {}

	private void rebuildPlayerNamesMap() {
		knownPlayersByName.clear();

		for (PlayerInfo info : knownPlayersByUUID.values()) {
			final EntityPlayerMP player = info.player.get();
			if (isPlayerValid(player)) {
				String name = player.getGameProfile().getName();
				knownPlayersByName.put(name, info);
			}
		}
	}

	public void registerTerminal(EntityPlayerMP player) {
		if (!knownPlayersByUUID.containsKey(player.getGameProfile().getId())) {
			final PlayerInfo playerInfo = new PlayerInfo(player);
			final GameProfile gameProfile = player.getGameProfile();

			knownPlayersByUUID.put(gameProfile.getId(), playerInfo);
			rebuildPlayerNamesMap();
			queueEvent(EVENT_PLAYER_ATTACH, player);

			sentFullDataToPlayer(player);
		}
	}

	private TerminalDataEvent createFullDataEvent(SurfaceServer surface) {
		TerminalDataEvent result = new TerminalDataEvent(guid, surface.isPrivate);
		surface.appendFullCommands(result.commands);
		return result;
	}

	private TerminalDataEvent createUpdateDataEvent(SurfaceServer surface) {
		TerminalDataEvent result = new TerminalDataEvent(guid, surface.isPrivate);
		surface.appendUpdateCommands(result.commands);
		return result;
	}

	private void sentFullDataToPlayer(EntityPlayer player) {
		if (lastSyncPackets != null) NetworkEventManager.INSTANCE.dispatcher().senders.player.sendMessages(lastSyncPackets, player);
	}

	private void queueEvent(String event, EntityPlayer user, Object... extra) {
		final GameProfile gameProfile = user.getGameProfile();

		final Object[] template = new Object[3 + extra.length];
		template[1] = gameProfile.getName();
		final UUID id = gameProfile.getId();
		template[2] = id != null? id.toString() : null;

		for (int i = 0; i < extra.length; i++) {
			final Object v = extra[i];
			template[i + 3] = v;
		}

		for (IArchitectureAccess computer : computers) {
			Object[] args = Arrays.copyOf(template, template.length);
			args[0] = computer.peripheralName();
			computer.signal(event, args);
		}
	}

	public void onChatCommand(String event, String content, EntityPlayer player) {
		queueEvent(event, player, content);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote || globalSurface == null) return;

		TerminalManagerServer.instance.registerBridge(guid, this);

		boolean playersRemoved = false;
		Iterator<PlayerInfo> it = knownPlayersByUUID.values().iterator();
		while (it.hasNext()) {
			final PlayerInfo info = it.next();
			final EntityPlayerMP player = info.player.get();

			if (!isPlayerValid(player)) {
				queueEvent(EVENT_PLAYER_DETACH, player);
				sendCleanPackets(player);
				it.remove();
				playersRemoved = true;
			}
		}

		if (playersRemoved) rebuildPlayerNamesMap();
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
		tag.setLong(TAG_GUID, guid);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		Long guid = TerminalUtils.extractGuid(tag);
		if (guid != null) this.guid = guid;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		NBTTagCompound tag = stack.getTagCompound();

		if (tag != null && tag.hasKey(TAG_GUID)) guid = tag.getLong(TAG_GUID);
		else guid = TerminalUtils.generateGuid();
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops) {
		ItemStack result = new ItemStack(getBlockType());
		NBTTagCompound tag = ItemUtils.getItemTag(result);
		tag.setLong(TAG_GUID, guid);
		drops.add(result);
	}

	@Override
	public boolean suppressNormalHarvestDrops() {
		return true;
	}

	@Override
	public void addComputer(IArchitectureAccess computer) {
		if (!computers.contains(computer)) {
			computers.add(computer);
		}
	}

	@Override
	public void removeComputer(IArchitectureAccess computer) {
		computers.remove(computer);
	}

	public void handleUserEvent(GlassesClientEvent evt) {
		queueEvent(evt.getEventName(), evt.sender, evt.getEventArgs());
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
	@ScriptCallable(description = "Send updates to client. Without it changes won't be visible", name = "sync")
	public void syncContents() {
		synchronized (globalSurface) {
			final boolean globalChanged = globalSurface.hasUpdates();

			if (globalChanged || lastSyncPackets == null) {
				final TerminalDataEvent globalFullData = createFullDataEvent(globalSurface);
				lastSyncPackets = globalFullData.serialize();
			}

			List<Object> globalUpdatePackets = null;

			if (globalChanged) {
				final TerminalDataEvent globalDelta = createUpdateDataEvent(globalSurface);
				globalUpdatePackets = globalDelta.serialize();
			}

			final ITargetedPacketSender<EntityPlayer> playerSender = NetworkEventManager.INSTANCE.dispatcher().senders.player;

			for (PlayerInfo info : knownPlayersByUUID.values()) {
				final EntityPlayerMP player = info.player.get();
				if (isPlayerValid(player)) {
					if (globalUpdatePackets != null) playerSender.sendMessages(globalUpdatePackets, player);
					sendPrivateUpdateToPlayer(player, info);
				}
			}
		}
	}

	private void sendPrivateUpdateToPlayer(final EntityPlayerMP player, PlayerInfo info) {
		final SurfaceServer privateSurface = info.surface;

		if (privateSurface != null) {
			synchronized (privateSurface) {
				if (privateSurface.hasUpdates()) {
					final TerminalDataEvent privateData = createUpdateDataEvent(privateSurface);
					privateData.sendToPlayer(player);
				}
			}
		}
	}

	private void sendPrivateFullToPlayer(EntityPlayer player) {
		UUID playerUuid = player.getGameProfile().getId();
		PlayerInfo info = knownPlayersByUUID.get(playerUuid);

		if (info != null) {
			TerminalDataEvent privateData = createFullDataEvent(info.surface);
			privateData.sendToPlayer(player);
		}
	}

	public void handleResetRequest(TerminalResetEvent evt) {
		if (evt.isPrivate) sendPrivateFullToPlayer(evt.sender);
		else sentFullDataToPlayer(evt.sender);
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get the names of all the users linked up to this bridge")
	public List<GameProfile> getUsers() {
		List<GameProfile> result = Lists.newArrayList();
		for (PlayerInfo info : knownPlayersByUUID.values())
			result.add(info.profile);

		return result;
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.STRING, name = "getGuid", description = "Get the Guid of this bridge")
	public String getGuidString() {
		return TerminalUtils.formatTerminalId(guid);
	}

	public long getGuid() {
		return guid;
	}

	@IncludeOverride
	public void clear() {
		globalSurface.clear();

		for (PlayerInfo info : knownPlayersByUUID.values())
			info.surface.clear();
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get the surface of a user to draw privately on their screen")
	public IDrawableContainer getSurfaceByName(@Arg(name = "username", description = "The username of the user to get the draw surface for") String username) {
		SurfaceServer playerSurface = getSurface(username);
		Preconditions.checkNotNull(playerSurface, "Invalid player");
		return playerSurface;
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get the surface of a user to draw privately on their screen")
	public IDrawableContainer getSurfaceByUUID(@Arg(name = "uuid", description = "The uuid of the user to get the draw surface for") UUID uuid) {
		SurfaceServer playerSurface = getSurface(uuid);
		Preconditions.checkNotNull(playerSurface, "Invalid player");
		return playerSurface;
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Returns object used for controlling player capture mode")
	public CaptureControl getCaptureControl(@Arg(name = "uuid") UUID uuid) {
		PlayerInfo info = knownPlayersByUUID.get(uuid);
		return info != null? new CaptureControl(info.player) : null;
	}
}
