package openperipheral.addons.glasses;

import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import openmods.utils.ItemUtils;
import openmods.utils.StringUtils;
import openperipheral.addons.drawable.Surface;
import openperipheral.api.IAttachable;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.Player;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaObject;

public class TileEntityGlassesBridge extends TileEntity implements IAttachable {

	private static final String EVENT_CHAT_MESSAGE = "chat_command";

	private static final String EVENT_PLAYER_JOIN = "registered_player_join";

	private static class PlayerInfo {
		public final WeakReference<EntityPlayer> player;
		public Surface surface;

		public PlayerInfo(TileEntityGlassesBridge parent, EntityPlayer player) {
			this.player = new WeakReference<EntityPlayer>(player);
			this.surface = new Surface(parent, player.getEntityName());
		}
	}

	private Map<String, PlayerInfo> knownPlayers = Maps.newHashMap();
	public Set<EntityPlayer> newPlayers = Sets.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());

	public Surface globalSurface = new Surface(this);

	private List<IComputerAccess> computers = Lists.newArrayList();

	/**
	 * Unique GUID for this terminal
	 */
	private String guid = StringUtils.randomString(8);

	public TileEntityGlassesBridge() {}

	public void registerPlayer(EntityPlayer player) {
		if (!knownPlayers.containsKey(player.getEntityName())) {
			newPlayers.add(player);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) return;

		TerminalDataEvent globalChange = null;

		Iterator<PlayerInfo> it = knownPlayers.values().iterator();
		while (it.hasNext()) {
			final PlayerInfo info = it.next();
			final EntityPlayer player = info.player.get();

			if (!isPlayerValid(player)) {
				it.remove();
				continue;
			}

			if (globalChange == null) globalChange = globalSurface.createChangeEvent();

			globalChange.sendToPlayer((Player)player);

			final Surface privateSurface = info.surface;
			if (privateSurface != null) {
				TerminalDataEvent privateData = privateSurface.createFullEvent();
				privateData.sendToPlayer((Player)player);
				privateSurface.clearChanges();
			}
		}

		globalSurface.clearChanges();

		TerminalDataEvent globalFull = null;

		for (EntityPlayer newPlayer : newPlayers) {
			if (isPlayerValid(newPlayer)) {
				if (globalFull == null) globalFull = globalSurface.createFullEvent();

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

		ItemStack glasses = player.inventory.armorItemInSlot(3);
		if (glasses == null || !(glasses.getItem() instanceof ItemGlasses)) return false;

		NBTTagCompound tag = glasses.getTagCompound();
		if (tag == null) return false;

		NBTTagCompound openPTag = (NBTTagCompound)tag.getTag("openp");
		if (openPTag == null) return false;

		String guid = openPTag.getString("guid");
		return !Strings.isNullOrEmpty(guid) && guid.equals(this.guid);
	}

	public String getGuid() {
		return guid;
	}

	public void resetGuid() {
		guid = StringUtils.randomString(8);
	}

	public List<String> getUsers() {
		return ImmutableList.copyOf(knownPlayers.keySet());
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("guid", guid);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		guid = tag.getString("guid");
	}

	public void onChatCommand(String command, String username) {
		for (IComputerAccess computer : computers) {
			computer.queueEvent(EVENT_CHAT_MESSAGE, new Object[] { command,
					username, getGuid(), computer.getAttachmentName() });
		}
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

	public static TileEntityGlassesBridge getGlassesBridgeFromStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey("openp")) {
				NBTTagCompound openPTag = tag.getCompoundTag("openp");
				String guid = openPTag.getString("guid");
				int x = openPTag.getInteger("x");
				int y = openPTag.getInteger("y");
				int z = openPTag.getInteger("z");
				int d = openPTag.getInteger("d");

				World worldObj = DimensionManager.getWorld(d);

				if (worldObj != null) {
					if (worldObj.blockExists(x, y, z)) {
						TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
						if (tile instanceof TileEntityGlassesBridge) {
							if (!((TileEntityGlassesBridge)tile).getGuid()
									.equals(guid)) return null;
							return (TileEntityGlassesBridge)tile;
						}
					}
				}
			}
		}
		return null;
	}

	void writeDataToGlasses(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		NBTTagCompound openPTag = (NBTTagCompound)tag.getTag("openp");
		if (openPTag == null) {
			openPTag = new NBTTagCompound();
			tag.setTag("openp", openPTag);
		}

		openPTag.setString("guid", getGuid());
		openPTag.setInteger("x", xCoord);
		openPTag.setInteger("y", yCoord);
		openPTag.setInteger("z", zCoord);
		openPTag.setInteger("d", worldObj.provider.dimensionId);
	}

	public int getStringWidth(String text) {
		return Surface.getStringWidth(text);
	}

	public ILuaObject getUserSurface(String username) {
		PlayerInfo info = knownPlayers.get(username);
		return info != null? info.surface : null;
	}
}
