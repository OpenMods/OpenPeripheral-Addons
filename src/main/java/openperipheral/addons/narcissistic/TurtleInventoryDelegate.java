package openperipheral.addons.narcissistic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import openperipheral.addons.utils.CCUtils;
import openperipheral.api.adapter.IWorldPosProvider;
import dan200.computercraft.api.turtle.ITurtleAccess;

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
	public ItemStack removeStackFromSlot(int slot) {
		return inventory().removeStackFromSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getName() {
		return inventory().getName();
	}

	@Override
	public IChatComponent getDisplayName() {
		return inventory().getDisplayName();
	}

	@Override
	public boolean hasCustomName() {
		return inventory().hasCustomName();
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
	public void openInventory(EntityPlayer player) {
		inventory().openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		inventory().closeInventory(player);
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
	public BlockPos getPos() {
		return wrapped.getPosition();
	}

	@Override
	public int getField(int id) {
		return inventory().getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory().setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory().getFieldCount();
	}

	@Override
	public void clear() {
		inventory().clear();
	}

}
