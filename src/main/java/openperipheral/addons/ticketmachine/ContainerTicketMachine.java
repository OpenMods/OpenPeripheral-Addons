package openperipheral.addons.ticketmachine;

import net.minecraft.inventory.IInventory;
import openmods.container.ContainerInventory;

public class ContainerTicketMachine extends ContainerInventory<TileEntityTicketMachine> {

	public ContainerTicketMachine(IInventory playerInventory, TileEntityTicketMachine owner) {
		super(playerInventory, owner);
		addInventoryLine(38, 34, 0, 2, 3);
		addInventoryLine(120, 34, 2, 1);
		addPlayerInventorySlots(85);
	}
}
