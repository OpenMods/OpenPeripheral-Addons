package openperipheral.addons.selector;

import net.minecraft.inventory.IInventory;
import openmods.container.ContainerBase;
import openmods.container.FakeSlot;

public class ContainerSelector extends ContainerBase<TileEntitySelector> {

	public ContainerSelector(IInventory playerInventory, TileEntitySelector owner) {
		super(playerInventory, owner.createInventoryWrapper(), owner);
		addInventoryGrid(60, 20, 3);
		addPlayerInventorySlots(85);
	}

	// This is basically the same as addInventoryGrid from the parent class,
	// but using FakeSlots instead of normal slots.
	@Override
	protected void addInventoryGrid(int xOffset, int yOffset, int width) {
		int height = (int)Math.ceil((double)inventorySize / width);
		int slotId = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				addSlotToContainer(new FakeSlot(inventory, slotId++,
						xOffset + x * 18,
						yOffset + y * 18,
						false));
			}
		}
	}
}
