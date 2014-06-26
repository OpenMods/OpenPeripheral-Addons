package openperipheral.addons.ticketmachine;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openmods.GenericInventory;
import openmods.api.*;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;
import openmods.utils.ItemUtils;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.registry.GameRegistry;

@Freeform
public class TileEntityTicketMachine extends SyncedTileEntity implements ISidedInventory, IInventoryCallback, IPlaceAwareTile, IHasGui, IIconProvider {

	private static final int SLOT_PAPER = 0;
	private static final int SLOT_INK = 1;
	private static final int SLOT_OUTPUT = 2;

	private final Item ticketItem;

	protected GenericInventory inventory = new GenericInventory("ticketmachine", false, 3);

	protected SyncableBoolean hasTicket;
	protected SyncableString owner;

	@Override
	protected void createSyncedFields() {
		hasTicket = new SyncableBoolean();
		owner = new SyncableString();
	}

	public TileEntityTicketMachine() {
		inventory.addCallback(this);

		ItemStack ticketStack = GameRegistry.findItemStack("Railcraft", "routing.ticket", 1);
		ticketItem = ticketStack != null? ticketStack.getItem() : null;
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
		inventory.openChest();
	}

	@Override
	public void closeChest() {
		inventory.closeChest();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (itemstack == null) return false;

		switch (i) {
			case SLOT_PAPER:
				return itemstack.getItem() == Item.paper;
			case SLOT_INK: {
				ColorMeta color = ColorUtils.stackToColor(itemstack);
				return color != null && color.vanillaId == ColorUtils.BLACK;
			}
			default:
				return false;
		}
	}

	@OnTick
	@LuaCallable(returnTypes = LuaType.BOOLEAN, description = "Create a new ticket to the specified destination")
	public boolean createTicket(@Arg(name = "destination", description = "The destination for the ticket", type = LuaType.STRING) String destination,
			@Arg(name = "amount", type = LuaType.NUMBER) @Optionals Integer amount) {
		if (amount == null) amount = 1;
		else Preconditions.checkArgument(amount > 0 && amount <= 64, "Amount must be between 1 and 64");

		ItemStack paperStack = inventory.getStackInSlot(SLOT_PAPER);
		Preconditions.checkState(isItemValidForSlot(SLOT_PAPER, paperStack) && paperStack.stackSize >= amount, "Not enough paper");

		ItemStack inkStack = inventory.getStackInSlot(SLOT_INK);
		Preconditions.checkState(isItemValidForSlot(SLOT_INK, inkStack) && inkStack.stackSize >= amount, "Not enough ink");

		ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT);

		ItemStack newTicket = new ItemStack(ticketItem);
		NBTTagCompound tag = ItemUtils.getItemTag(newTicket);
		tag.setString("owner", owner.getValue());
		tag.setString("dest", destination);

		if (output == null) {
			setInventorySlotContents(SLOT_OUTPUT, newTicket);
		} else if (ItemStack.areItemStackTagsEqual(output, newTicket) && output.isItemEqual(newTicket) && output.stackSize + amount <= output.getMaxStackSize()) {
			output.stackSize += amount;
		} else throw new IllegalArgumentException("No place in output slot");

		decrStackSize(SLOT_PAPER, amount);
		decrStackSize(SLOT_INK, amount);

		worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "openperipheraladdons:ticketmachine", 0.3F, 0.6F);
		sync();

		return true;
	}

	@LuaCallable(returnTypes = LuaType.STRING, description = "Returns owner of this machine")
	public String getOwner() {
		return owner.getValue();
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { SLOT_INK, SLOT_PAPER, SLOT_OUTPUT };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return slot == SLOT_OUTPUT;
	}

	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {
		if (worldObj.isRemote) return;
		boolean nowHasTicket = inventory.getStackInSlot(SLOT_OUTPUT) != null;
		if (nowHasTicket != hasTicket.getValue()) hasTicket.setValue(nowHasTicket);
		worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		owner.setValue(player.username);
	}

	@Override
	public Icon getIcon(ForgeDirection rotatedDir) {
		if (rotatedDir == ForgeDirection.SOUTH) return hasTicket.getValue()? BlockTicketMachine.iconFrontTicket : BlockTicketMachine.iconFrontEmpty;
		return null;
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
