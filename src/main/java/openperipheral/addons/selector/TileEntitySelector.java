package openperipheral.addons.selector;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import openmods.api.IActivateAwareTile;
import openmods.api.IHasGui;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableItemStack;
import openmods.tileentity.SyncedTileEntity;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.Constants;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.*;
import openperipheral.api.architecture.*;
import openperipheral.api.helpers.Index;
import openperipheral.api.peripheral.PeripheralTypeId;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@PeripheralTypeId("openperipheral_selector")
@FeatureGroup("openperipheral-selector")
public class TileEntitySelector extends SyncedTileEntity implements IActivateAwareTile, IAttachable, IHasGui {

	public static class ItemSlot {
		public final int slot;
		public final double x;
		public final double y;
		public final AxisAlignedBB box;
		public final double size;

		public ItemSlot(int absSlot, double size, double x, double y) {
			this.slot = absSlot;
			this.size = size;
			this.x = x;
			this.y = y;
			this.box = AxisAlignedBB.getBoundingBox(x - size, 1 - size, y - size, x + size, 1 + size, y + size);
		}
	}

	private static final List<ItemSlot> BOXES_0 = ImmutableList.of();

	private static final List<ItemSlot> BOXES_1 = ImmutableList.of(new ItemSlot(0, 0.2, 0.5, 0.5));

	private static final double PADDING_2 = 0.05;

	private static final double SIZE_2 = 0.12;

	private static final double DELTA_2 = SIZE_2 + PADDING_2;

	private static final ImmutableList<ItemSlot> BOXES_2 = ImmutableList.of(
			new ItemSlot(3, SIZE_2, 0.5 - DELTA_2, 0.5 + DELTA_2),
			new ItemSlot(4, SIZE_2, 0.5 + DELTA_2, 0.5 + DELTA_2),
			new ItemSlot(0, SIZE_2, 0.5 - DELTA_2, 0.5 - DELTA_2),
			new ItemSlot(1, SIZE_2, 0.5 + DELTA_2, 0.5 - DELTA_2));

	private static final double SIZE_3 = 0.085;

	private static final double PADDING_3 = 0.08;

	private static final double DELTA_3 = SIZE_3 + PADDING_3 + SIZE_3;

	private static final List<ItemSlot> BOXES_3 = ImmutableList.of(
			new ItemSlot(6, SIZE_3, 0.5 - DELTA_3, 0.5 + DELTA_3),
			new ItemSlot(7, SIZE_3, 0.5, 0.5 + DELTA_3),
			new ItemSlot(8, SIZE_3, 0.5 + DELTA_3, 0.5 + DELTA_3),
			new ItemSlot(3, SIZE_3, 0.5 - DELTA_3, 0.5),
			new ItemSlot(4, SIZE_3, 0.5, 0.5),
			new ItemSlot(5, SIZE_3, 0.5 + DELTA_3, 0.5),
			new ItemSlot(0, SIZE_3, 0.5 - DELTA_3, 0.5 - DELTA_3),
			new ItemSlot(1, SIZE_3, 0.5, 0.5 - DELTA_3),
			new ItemSlot(2, SIZE_3, 0.5 + DELTA_3, 0.5 - DELTA_3));

	private Set<IArchitectureAccess> computers = Sets.newIdentityHashSet();

	private final SyncableItemStack[] slots = new SyncableItemStack[9];

	private SoftReference<EntityItem> displayEntity = new SoftReference<EntityItem>(null);

	private int gridSize;

	public TileEntitySelector() {
		for (int i = 0; i < 9; i++) {
			SyncableItemStack item = new SyncableItemStack();
			syncMap.put("Stack" + i, item);
			slots[i] = item;
		}

		final ISyncListener listener = new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				gridSize = calculateGridSize();
			}
		};
		syncMap.addUpdateListener(listener);
		syncMap.addSyncListener(listener);
	}

	@Override
	protected void createSyncedFields() {}

	private boolean hasStack(int slot) {
		return slots[slot].get() != null;
	}

	// We're having a dynamic display size, i.e. if there's only an item in the
	// first slot, only the single item will be shown - slightly enlarged. If
	// there are items one slot further out, we're showing a 2x2 grid and so on.
	// There's probably a mathematical way to do this more efficiently, but meh.
	public int calculateGridSize() {
		if (hasStack(2) || hasStack(5) || hasStack(6) || hasStack(7) || hasStack(8)) return 3;
		if (hasStack(1) || hasStack(3) || hasStack(4)) return 2;
		if (hasStack(0)) return 1;

		return 0;
	}

	public int getGridSize() {
		return gridSize;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return true;
		if (player.isSneaking()) return true;

		Vec3 vec = Vec3.createVectorHelper(xCoord + hitX, yCoord + hitY, zCoord + hitZ);
		ItemSlot slot = getClickedSlot(vec, side);

		if (slot == null) {
			openGui(OpenPeripheralAddons.instance, player);
		} else {
			if (hasStack(slot.slot)) signalSlotClick(slot.slot);
		}

		return true;
	}

	public ItemStack getSlot(int slot) {
		return slots[slot].get();
	}

	@Asynchronous
	@ScriptCallable(description = "Get the item currently being displayed in a specific slot", returnTypes = ReturnType.TABLE, name = "getSlot")
	public ItemStack getSlotOneBased(@Arg(name = "slot", description = "The slot you want to get details about") Index slot) {

		slot.checkElementIndex("slot id", 10);
		return slots[slot.value].get();
	}

	@Asynchronous
	@ScriptCallable(description = "Gets all slots", returnTypes = ReturnType.TABLE)
	public List<ItemStack> getSlots() {
		List<ItemStack> result = Lists.newArrayList();

		for (SyncableItemStack slot : slots)
			result.add(slot.get());

		return result;
	}

	@ScriptCallable(description = "Set the items being displayed in all slots")
	public void setSlots(
			@Env(Constants.ARG_ARCHITECTURE) IArchitecture access,
			@Arg(name = "items", description = "A table containing itemstacks") Map<Index, ItemStack> stacks) {
		for (int slot = 0; slot < 9; slot++) {
			final ItemStack value = stacks.get(access.wrapObject(slot));
			if (value != null) value.stackSize = 1;

			this.slots[slot].set(value);
		}

		sync();
	}

	@ScriptCallable(description = "Set the item being displayed on a specific slot")
	public void setSlot(
			@Arg(name = "slot", description = "The slot you want to modify") Index slot,
			@Optionals @Arg(name = "item", description = "The item you want to display. nil to set empty") ItemStack stack) {

		slot.checkElementIndex("slot id", 10);

		if (stack != null) stack.stackSize = 1;

		this.slots[slot.value].set(stack);
		sync();
	}

	private void signalSlotClick(int slot) {
		for (IArchitectureAccess computer : computers) {
			final Object[] args = new Object[] { computer.createIndex(slot), computer.peripheralName() };
			computer.signal("slot_click", args);
		}
	}

	@Override
	public void addComputer(IArchitectureAccess computer) {
		computers.add(computer);
	}

	@Override
	public void removeComputer(IArchitectureAccess computer) {
		computers.remove(computer);
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		// Don't use default GUI mechanism, since this block has special rules
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

	public IInventory createInventoryWrapper() {
		return new IInventory() {

			@Override
			public void setInventorySlotContents(int slot, ItemStack stack) {
				slots[slot].set(stack);
			}

			@Override
			public void openInventory() {}

			@Override
			public void markDirty() {
				if (!worldObj.isRemote) sync();
			}

			@Override
			public boolean isUseableByPlayer(EntityPlayer player) {
				return (getWorldObj().getTileEntity(xCoord, yCoord, zCoord) == TileEntitySelector.this)
						&& (player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64.0D);
			}

			@Override
			public boolean isItemValidForSlot(int slot, ItemStack stack) {
				return true;
			}

			@Override
			public boolean hasCustomInventoryName() {
				return false;
			}

			@Override
			public ItemStack getStackInSlotOnClosing(int slot) {
				return null;
			}

			@Override
			public ItemStack getStackInSlot(int slot) {
				return slots[slot].get();
			}

			@Override
			public int getSizeInventory() {
				return 9;
			}

			@Override
			public int getInventoryStackLimit() {
				return 1;
			}

			@Override
			public String getInventoryName() {
				return "selector";
			}

			@Override
			public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void closeInventory() {}
		};
	}

	public AxisAlignedBB getSelection(Vec3 hitVec, int side) {
		final Orientation orientation = getOrientation();
		final Vec3 mappedVec = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitVec.xCoord - xCoord, hitVec.yCoord - yCoord, hitVec.zCoord - zCoord);

		final int gridSize = getGridSize();

		for (ItemSlot center : getSlots(gridSize)) {
			final AxisAlignedBB aabb = center.box;
			if (aabb.isVecInside(mappedVec)) return BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb).offset(xCoord, yCoord, zCoord);
		}

		return null;
	}

	private ItemSlot getClickedSlot(Vec3 hitVec, int side) {
		final Orientation orientation = getOrientation();
		final Vec3 mappedVec = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitVec.xCoord - xCoord, hitVec.yCoord - yCoord, hitVec.zCoord - zCoord);

		int gridSize = getGridSize();
		for (ItemSlot center : getSlots(gridSize)) {
			if (center.box.isVecInside(mappedVec)) return center;
		}

		return null;
	}

	public List<ItemSlot> getSlots(int gridSize) {
		switch (gridSize) {
			case 1:
				return BOXES_1;
			case 2:
				return BOXES_2;
			case 3:
				return BOXES_3;
			default:
				return BOXES_0;
		}
	}

	public EntityItem getDisplayEntity() {
		EntityItem result = displayEntity.get();
		if (result == null) {
			result = new EntityItem(getWorldObj(), 0, 0, 0);
			result.hoverStart = 0.0F;

			displayEntity = new SoftReference<EntityItem>(result);
		}

		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		gridSize = calculateGridSize();
	}

}
