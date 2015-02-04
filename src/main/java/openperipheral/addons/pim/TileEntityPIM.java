package openperipheral.addons.pim;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import openmods.tileentity.OpenTileEntity;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

public class TileEntityPIM extends OpenTileEntity implements IInventory, IAttachable {

	private WeakReference<EntityPlayer> player;

	private Set<IArchitectureAccess> computers = Sets.newIdentityHashSet();

	public EntityPlayer getPlayer() {
		return player != null? player.get() : null;
	}

	@Override
	public int getSizeInventory() {
		EntityPlayer player = getPlayer();
		return player != null? player.inventory.getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		EntityPlayer player = getPlayer();
		return player != null? player.inventory.getStackInSlot(i) : null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		EntityPlayer player = getPlayer();
		return player != null? player.inventory.decrStackSize(i, j) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		EntityPlayer player = getPlayer();
		if (player != null) player.inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInventoryName() {
		EntityPlayer player = getPlayer();
		return player != null? player.getCommandSenderName() : "pim";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		EntityPlayer player = getPlayer();
		return player != null? player.inventory.getInventoryStackLimit() : 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return getPlayer() != null;
	}

	@Override
	public void addComputer(IArchitectureAccess computer) {
		computers.add(computer);
	}

	@Override
	public void removeComputer(IArchitectureAccess computer) {
		computers.remove(computer);
	}

	public boolean hasPlayer() {
		if (worldObj == null) return false;
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1;
	}

	private void setPlayer(EntityPlayer newPlayer) {
		worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.1D, zCoord + 0.5D, "random.click", 0.3F, 0.6F);
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, newPlayer == null? 0 : 1, 3);

		if (player != null) {
			EntityPlayer prevPlayer = player.get();
			if (prevPlayer != null) {
				GameProfile profile = prevPlayer.getGameProfile();
				final UUID uuid = profile.getId();
				fireEvent("player_off", profile.getName(), uuid != null? uuid.toString() : "?");
			}
		}

		player = null;

		if (newPlayer != null) {
			player = new WeakReference<EntityPlayer>(newPlayer);
			GameProfile profile = newPlayer.getGameProfile();
			final UUID uuid = profile.getId();
			fireEvent("player_on", profile.getName(), uuid != null? uuid.toString() : "?");
		}
	}

	public void trySetPlayer(EntityPlayer newPlayer) {
		if (newPlayer == null) return;
		EntityPlayer current = getPlayer();
		if (current == null && isPlayerValid(newPlayer)) setPlayer(newPlayer);
	}

	private void fireEvent(String eventName, Object... args) {
		for (IArchitectureAccess computer : computers) {
			Object[] extendedArgs = ArrayUtils.add(args, computer.peripheralName());
			computer.signal(eventName, extendedArgs);
		}
	}

	private boolean isPlayerValid(EntityPlayer player) {
		if (player == null) return false;
		int playerX = MathHelper.floor_double(player.posX);
		int playerY = MathHelper.floor_double(player.posY + 0.5D);
		int playerZ = MathHelper.floor_double(player.posZ);
		return playerX == xCoord && playerY == yCoord && playerZ == zCoord;
	}

	/**
	 * TODO: fix this. This doesnt seem.. efficient.
	 */
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			EntityPlayer player = getPlayer();
			if (player != null && !isPlayerValid(player)) setPlayer(null);
		}
	}
}
