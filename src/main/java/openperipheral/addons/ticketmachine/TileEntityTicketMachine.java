package openperipheral.addons.ticketmachine;

import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import openmods.api.IHasGui;
import openmods.api.IInventoryCallback;
import openmods.api.IPlaceAwareTile;
import openmods.colors.ColorMeta;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.ItemUtils;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.*;
import openperipheral.api.peripheral.PeripheralTypeId;

import com.google.common.base.Preconditions;

@PeripheralTypeId("openperipheral_ticketmachine")
public class TileEntityTicketMachine extends SyncedTileEntity implements IPlaceAwareTile, IHasGui, IInventoryProvider, IInventoryCallback {

	private static final int SLOT_PAPER = 0;
	private static final int SLOT_INK = 1;
	private static final int SLOT_OUTPUT = 2;

	private static class CustomInventory extends GenericInventory implements ISidedInventory {
		private CustomInventory() {
			super("ticketmachine", false, 3);
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			if (itemstack == null) return false;

			switch (i) {
				case SLOT_PAPER:
					return itemstack.getItem() == Items.paper;
				case SLOT_INK: {
					Set<ColorMeta> color = ColorMeta.fromStack(itemstack);
					return color != null && color.contains(ColorMeta.BLACK);
				}
				default:
					return false;
			}
		}

		@Override
		public int[] getSlotsForFace(EnumFacing side) {
			return new int[] { SLOT_INK, SLOT_PAPER, SLOT_OUTPUT };
		}

		@Override
		public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
			return isItemValidForSlot(slot, stack);
		}

		@Override
		public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
			return slot == SLOT_OUTPUT;
		}
	}

	@IncludeInterface(ISidedInventory.class)
	protected GenericInventory inventory = new CustomInventory().addCallback(this);

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	protected SyncableString owner;

	@Override
	protected void createSyncedFields() {
		owner = new SyncableString();
	}

	public TileEntityTicketMachine() {}

	@ScriptCallable(returnTypes = ReturnType.BOOLEAN, description = "Create a new ticket to the specified destination")
	public boolean createTicket(@Arg(name = "destination", description = "The destination for the ticket") String destination,
			@Arg(name = "amount") @Optionals Integer amount) {
		if (amount == null) amount = 1;
		else Preconditions.checkArgument(amount > 0 && amount <= 64, "Amount must be between 1 and 64");

		ItemStack paperStack = inventory.getStackInSlot(SLOT_PAPER);
		Preconditions.checkArgument(inventory.isItemValidForSlot(SLOT_PAPER, paperStack) && paperStack.stackSize >= amount, "Not enough paper");

		ItemStack inkStack = inventory.getStackInSlot(SLOT_INK);
		Preconditions.checkArgument(inventory.isItemValidForSlot(SLOT_INK, inkStack) && inkStack.stackSize >= amount, "Not enough ink");

		ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT);

		ItemStack newTicket = new ItemStack(Items.written_book); // TODO temporary
		NBTTagCompound tag = ItemUtils.getItemTag(newTicket);
		tag.setString("author", owner.getValue());
		tag.setString("title", destination);

		if (output == null) {
			inventory.setInventorySlotContents(SLOT_OUTPUT, newTicket);
		} else if (ItemStack.areItemStackTagsEqual(output, newTicket) && output.isItemEqual(newTicket) && output.stackSize + amount <= output.getMaxStackSize()) {
			output.stackSize += amount;
		} else throw new IllegalArgumentException("No place in output slot");

		inventory.decrStackSize(SLOT_PAPER, amount);
		inventory.decrStackSize(SLOT_INK, amount);

		worldObj.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, "openperipheraladdons:ticketmachine", 0.3F, 0.6F);
		sync();

		return true;
	}

	@Asynchronous
	@ScriptCallable(returnTypes = ReturnType.STRING, description = "Returns owner of this machine")
	public String getOwner() {
		return owner.getValue();
	}

	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {
		if (worldObj.isRemote) return;
		boolean hasTicket = inventory.getStackInSlot(SLOT_OUTPUT) != null;
		final IBlockState state = worldObj.getBlockState(pos).withProperty(BlockTicketMachine.HAS_TICKET, hasTicket);
		worldObj.setBlockState(pos, state);
	}

	@Override
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
		owner.setValue(placer.getName());
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerTicketMachine(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiTicketMachine(new ContainerTicketMachine(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

}
