package openperipheral.addons.selector;

import java.util.*;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomHarvestDrops;
import openmods.api.IHasGui;
import openmods.inventory.IInventoryProvider;
import openmods.tileentity.SyncedTileEntity;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.*;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.peripheral.IComputerAccess;

@PeripheralTypeId("openperipheral_selector")
public class TileEntitySelector extends SyncedTileEntity implements IActivateAwareTile, IAttachable, ICustomHarvestDrops, IHasGui, IInventoryProvider {
	private Set<IComputerAccess> computers = Collections.newSetFromMap(new WeakHashMap<IComputerAccess, Boolean>());

	// We need a "Fake" inventory which cannot be modified except by server
	// code, i.e. when a computer wants to set a slot or when a client clicks
	// one of the slots in the gui holding an Item.
	protected SyncableInventory inventory;

	private static class CustomInventory extends SyncableInventory {
		// Hardcoded 9 slots.
		public CustomInventory() {
			super("selector", false, 9);
		}

		// Nothing can be put in
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}

		// Nothing can be pulled out
		@Override
		public ItemStack decrStackSize(int slot, int qty) {
			return null;
		}
	}

	public TileEntitySelector() {
		super();

		// Clients need to get notified to re-render the blocks when the
		// inventory changed.
		syncMap.addUpdateListener(createRenderUpdateListener());
		registerInventoryCallback(inventory);
	}

	@Override
	protected void createSyncedFields() {
		inventory = new CustomInventory();
	}

	// Since the items displayed on a monitor with a grid size of 2 are not
	// using linear slots (but 0,1,3,4), we need to do some maths to get it
	// right.
	public int getSlotFromCoords(int row, int col) {
		int slot = 0;
		if (getGridSize() == 3) {
			slot = row * getGridSize() + col;
		} else if (getGridSize() == 2) {
			slot = col;
			if (row == 1) {
				slot += 3;
			}
		}
		return slot;
	}

	// Helper method for the TESR to get an EntityItem reference for an item
	// in a specific slot. This item is never placed in the world, but only
	// rendered.
	public EntityItem getStackEntity(int row, int col) {
		int slot = getSlotFromCoords(row, col);

		if (getInventory().getStackInSlot(slot) == null) { return null; }

		EntityItem entity = new EntityItem(getWorldObj(), 0.0D, 0.0D, 0.0D, getInventory().getStackInSlot(slot));
		entity.hoverStart = 0.0F;
		entity.lifespan = 72000;

		return entity;
	}

	private boolean hasStack(int slot) {
		return (inventory.getStackInSlot(slot) != null);
	}

	// We're having a dynamic display size, i.e. if there's only an item in the
	// first slot, only the single item will be shown - slightly enlarged. If
	// there are items one slot further out, we're showing a 2x2 grid and so on.
	// There's probably a mathematical way to do this more efficiently, but meh.
	public int getGridSize() {
		if (hasStack(2) || hasStack(5) || hasStack(6) || hasStack(7) || hasStack(8)) { return 3; }
		if (hasStack(1) || hasStack(3) || hasStack(4)) { return 2; }
		if (hasStack(0)) { return 1; }

		return 0;
	}

	// Whenever a player hits one of the buttons we want to fire an event on all
	// attached computers. For this we need to know which button he pressed and
	// act accordingly.
	// But we also need to open the gui ourselves, because the sidedness matters
	// in this case. Problem is that canOpenGui(EntityPlayer player) does not
	// have a "side" parameter which would allow for opening the GUI the
	// OpenBlocks way.
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		// Never do anything when the player is sneaking
		if (player.isSneaking()) { return false; }

		// Open the GUI unless player clicked on the screen.
		if (getRotation().ordinal() != side) {
			openGui(OpenPeripheralAddons.instance, player);
			return true;
		}

		// Go server-side only
		if (worldObj.isRemote) { return true; }

		// No computers -> no events to fire -> nothing to calculate
		if (computers.size() == 0) { return true; }

		// Figure out which slot the user clicked incorporating
		// the blocks rotation and grid size.
		float clickX = hitX;
		float clickY = hitY;
		ForgeDirection dir = getRotation();

		if (dir == ForgeDirection.SOUTH) {
			clickY = 1 - hitY;
		}

		if (dir == ForgeDirection.NORTH) {
			clickX = 1 - hitX;
			clickY = 1 - hitY;
		}

		if (dir == ForgeDirection.EAST) {
			clickX = 1 - hitZ;
			clickY = 1 - hitY;
		}

		if (dir == ForgeDirection.WEST) {
			clickX = hitZ;
			clickY = 1 - hitY;
		}

		if (dir == ForgeDirection.UP) {
			clickX = 1 - hitX;
			clickY = 1 - hitZ;
		}

		if (dir == ForgeDirection.DOWN) {
			clickX = 1 - hitX;
			clickY = hitZ;
		}

		// After that it's easy to calculate which "quadrant" the player clicked
		float squareSize = 1.0F / getGridSize();

		int col = (int)Math.floor(clickX / squareSize);
		int row = (int)Math.floor(clickY / squareSize);

		// And from that we can get slot
		int slot = getSlotFromCoords(row, col);

		// But only fire the event if there is an item in the slot
		if (getInventory().getStackInSlot(slot) != null) {
			fireEvent("slot_click", slot + 1);
		}

		// Yes, interaction happened -> don't place a block
		return true;
	}

	@LuaCallable(description = "Get the item currently being displayed in a specific slot", returnTypes = LuaReturnType.TABLE)
	public ItemStack getSlot(
			@Arg(name = "slot", description = "The slot you want to get details about") int slot) {

		Preconditions.checkArgument(slot >= 1 && slot <= 9, "slot must be between 1 and 9");
		return getInventory().getStackInSlot(slot - 1);
	}

	@LuaCallable(description = "Set the item being displayed on a specific slot")
	public void setSlots(
			@Env(Constants.ARG_CONVERTER) ITypeConvertersRegistry converter,
			@Arg(name = "items", description = "A table containing itemstacks", type = LuaArgType.TABLE) Map<Double, Object> stacks) {

		for (int slot = 1; slot <= 9; slot++) {
			final Object value = stacks.get(Double.valueOf(slot));
			ItemStack stack = (ItemStack)converter.fromLua(value, ItemStack.class);
			Preconditions.checkArgument(value == null || stack != null, "Can't convert %s to item stack", value);
			if (stack != null) stack.stackSize = 1;

			inventory.setInventorySlotContents(slot - 1, stack);
		}

		inventory.markDirty();
		sync();
	}

	@LuaCallable(description = "Set the item being displayed on a specific slot")
	public void setSlot(
			@Arg(name = "slot", description = "The slot you want to modify") int slot,
			@Optionals @Arg(name = "item", description = "The item you want to display. nil to set empty.", type = LuaArgType.TABLE) ItemStack stack) {

		Preconditions.checkArgument(slot >= 1 && slot <= 9, "slot must be between 1 and 9");
		slot -= 1;

		if (stack != null) stack.stackSize = 1;

		inventory.setInventorySlotContents(slot, stack);

		inventory.markDirty();
		sync();
	}

	private void fireEvent(String eventName, Object... args) {
		for (IComputerAccess computer : computers) {
			Object[] extendedArgs = ArrayUtils.add(args, computer.getAttachmentName());
			computer.queueEvent(eventName, extendedArgs);
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

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		// No one can open the GUI the OpenBlocks way because we're doing
		// it manually.
		return false;
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiSelector(new ContainerSelector(player.inventory, this));
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerSelector(player.inventory, this);
	}

	@Override
	public IInventory getInventory() {
		return inventory;
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

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops) {
		// The only thing we do want to drop is the Item Selector itself.
		drops.add(new ItemStack(OpenPeripheralAddons.Blocks.selector, 1));
	}

	@Override
	public boolean suppressNormalHarvestDrops() {
		// We don't want anything to drop on its own
		return true;
	}
}
