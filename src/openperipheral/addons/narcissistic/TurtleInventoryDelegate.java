package openperipheral.addons.narcissistic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openperipheral.api.IWorldProvider;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class TurtleInventoryDelegate implements IInventory, IWorldProvider {

	// looks like we need that to separate from actual stuff CC throws at us

	private final ITurtleAccess wrapped;

	public TurtleInventoryDelegate(ITurtleAccess wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public World getWorld() {
		return wrapped.getWorld();
	}

	private IInventory inventory() {
		return wrapped.getInventory();
	}

	@Override
	public int getSizeInventory() {
		return inventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory().getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory().decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory().getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory().getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory().isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory().getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		inventory().onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory().isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
		inventory().openChest();
	}

	@Override
	public void closeChest() {
		inventory().closeChest();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory().isItemValidForSlot(i, itemstack);
	}

}
