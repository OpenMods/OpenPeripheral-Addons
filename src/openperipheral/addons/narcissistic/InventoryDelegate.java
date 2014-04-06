package openperipheral.addons.narcissistic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryDelegate implements IInventory {

	// looks like we need that to separate from actual stuff CC throws at us

	private final IInventory wrapped;

	public InventoryDelegate(IInventory wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public int getSizeInventory() {
		return wrapped.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return wrapped.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return wrapped.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return wrapped.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		wrapped.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return wrapped.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return wrapped.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return wrapped.getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		wrapped.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return wrapped.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
		wrapped.openChest();
	}

	@Override
	public void closeChest() {
		wrapped.closeChest();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return wrapped.isItemValidForSlot(i, itemstack);
	}

}
