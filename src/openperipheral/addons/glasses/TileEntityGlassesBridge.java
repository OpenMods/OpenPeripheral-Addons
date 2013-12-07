package openperipheral.addons.glasses;

import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ItemUtils;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
import openperipheral.api.IAttachable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.Player;
import dan200.computer.api.IComputerAccess;

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

	public void onGlassesTick(EntityPlayer player) {
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
				it.remove();
				continue;
			}

			if (globalUpdate) {
				if (globalChange == null) globalChange = TerminalManagerServer.createUpdateDataEvent(globalSurface, guid, false);
				globalChange.sendToPlayer((Player)player);
			}

			final SurfaceServer privateSurface = info.surface;
			if (privateSurface != null && privateSurface.hasUpdates()) {
				TerminalDataEvent privateData = TerminalManagerServer.createUpdateDataEvent(privateSurface, guid, true);
				privateData.sendToPlayer((Player)player);
			}
		}

		TerminalDataEvent globalFull = null;

		for (EntityPlayer newPlayer : newPlayers) {
			if (isPlayerValid(newPlayer)) {
				if (globalFull == null) globalFull = TerminalManagerServer.createFullDataEvent(globalSurface, guid, false);
				globalFull.sendToPlayer((Player)newPlayer);

				final String playerName = newPlayer.getEntityName();
				knownPlayers.put(playerName, new PlayerInfo(this, newPlayer));
				onPlayerJoin(playerName);
			}
		}

		newPlayers.clear();
	}

	private boolean isPlayerValid(EntityPlayer player) {
		if (player == null) return false;

		ItemStack glasses = ItemGlasses.getGlassesItem(player);
		if (glasses == null) return false;

		Long guid = ItemGlasses.extractGuid(glasses);
		return guid != null && guid == this.guid;
	}

	public long getGuid() {
		return guid;
	}

	public List<String> getUsers() {
		return ImmutableList.copyOf(knownPlayers.keySet());
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

	void linkGlasses(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		NBTTagCompound openPTag = (NBTTagCompound)tag.getTag("openp");
		if (openPTag == null) {
			openPTag = new NBTTagCompound();
			tag.setTag("openp", openPTag);
		}

		openPTag.setLong("guid", getGuid());
	}

	public SurfaceServer getSurface(String username) {
		if (TerminalUtils.GLOBAL_MARKER.equals(username)) return globalSurface;
		PlayerInfo info = knownPlayers.get(username);
		return info != null? info.surface : null;
	}
}
