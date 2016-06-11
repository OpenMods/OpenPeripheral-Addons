package openperipheral.addons.narcissistic;

import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openperipheral.addons.utils.CCUtils;
import openperipheral.api.adapter.IWorldPosProvider;

public class TurtleInventoryDelegate implements IInventory, IWorldPosProvider {

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
	public String getInventoryName() {
		return inventory().getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventory().hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory().getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		inventory().markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory().isUseableByPlayer(entityplayer);
	}

	@Override
	public void openInventory() {
		inventory().openInventory();
	}

	@Override
	public void closeInventory() {
		inventory().closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory().isItemValidForSlot(i, itemstack);
	}

	@Override
	public boolean isValid() {
		return CCUtils.isTurtleValid(wrapped);
	}

	@Override
	public int getX() {
		return wrapped.getPosition().posX;
	}

	@Override
	public int getY() {
		return wrapped.getPosition().posY;
	}

	@Override
	public int getZ() {
		return wrapped.getPosition().posZ;
	}

}
