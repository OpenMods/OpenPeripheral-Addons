package openperipheral.addons.pim;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ArrayUtils;

import dan200.computer.api.IComputerAccess;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import openmods.tileentity.OpenTileEntity;
import openperipheral.api.IAttachable;

public class TileEntityPIM extends OpenTileEntity implements IInventory,
		IAttachable {

	private WeakReference<EntityPlayer> player;

	private Set<IComputerAccess> computers = Collections
			.newSetFromMap(new WeakHashMap<IComputerAccess, Boolean>());

	@Override
	public int getSizeInventory() {
		if (player != null && player.get() != null) { return player.get().inventory.getSizeInventory(); }
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (player != null && player.get() != null) { return player.get().inventory.getStackInSlot(i); }
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (player != null && player.get() != null) { return player.get().inventory.decrStackSize(i, j); }
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (player != null && player.get() != null) {
			player.get().inventory.setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getInvName() {
		if (player != null && player.get() != null) {
			player.get().inventory.getInvName();
		}
		return "pim";
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		if (player != null && player.get() != null) {
			player.get().inventory.getInventoryStackLimit();
		}
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (player != null && player.get() != null) {
			player.get().inventory.isItemValidForSlot(i, itemstack);
		}
		return false;
	}

	@Override
	public void addComputer(IComputerAccess computer) {
		computers.add(computer);
	}

	@Override
	public void removeComputer(IComputerAccess computer) {
		computers.remove(computer);
	}

	public EntityPlayer getPlayer() {
		return player != null? player.get() : null;
	}

	public boolean hasPlayer() {
		if (worldObj == null) return false;
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1;
	}

	public void setPlayer(EntityPlayer p) {
		player = new WeakReference<EntityPlayer>(p);
		worldObj.playSoundEffect((double)xCoord + 0.5D,
				(double)yCoord + 0.1D, (double)zCoord + 0.5D, "random.click",
				0.3F, 0.6F);
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord,
				p == null? 0 : 1, 3);
		fireEvent(p == null? "player_off" : "player_on");
	}

	public void fireEvent(String eventName, Object... args) {
		if (args == null) {
			args = new Object[0];
		}
		for (IComputerAccess computer : computers) {
			args = ArrayUtils.add(args, computer.getAttachmentName());
			computer.queueEvent(eventName, args);
		}
	}

	/**
	 * TODO: fix this. This doesnt seem.. efficient.
	 */
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			EntityPlayer player = getPlayer();
			if (player != null) {
				ChunkCoordinates coordinates = player.getPlayerCoordinates();
				if (coordinates.posX != xCoord || coordinates.posY != yCoord || coordinates.posZ != zCoord) {
					setPlayer(null);
				}
			}
		}
	}
}
