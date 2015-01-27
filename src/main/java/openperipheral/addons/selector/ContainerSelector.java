package openperipheral.addons.selector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openmods.Log;
import openmods.container.ContainerInventoryProvider;
import openmods.container.FakeSlot;

public class ContainerSelector extends ContainerInventoryProvider<TileEntitySelector> {

	public ContainerSelector(IInventory playerInventory, TileEntitySelector owner) {
		super(playerInventory, owner);
		addInventoryGrid(60, 20, 3);
		addPlayerInventorySlots(85);
	}

	// This is basically the same as addInventoryGrid from the parent class,
	// but using FakeSlots instead of normal slots.
	protected void addInventoryGrid(int xOffset, int yOffset, int width) {
		int height = (int)Math.ceil((double)inventorySize / width);
		for (int y = 0, slotId = 0; y < height; y++) {
			for (int x = 0; x < width; x++, slotId++) {
				addSlotToContainer(new FakeSlot(inventory, slotId,
						xOffset + x * 18,
						yOffset + y * 18));
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		// Nothing can be transferred anywhere from this container.
		return null;
	}
}
