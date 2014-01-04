package openperipheral.addons.ticketmachine;

import openmods.gui.BaseGuiContainer;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.GuiComponentPanel;

public class GuiTicketMachine extends BaseGuiContainer<ContainerTicketMachine> {

	public GuiTicketMachine(ContainerTicketMachine container) {
		super(container, 176, 167, "openperipheral.gui.ticketmachine");
	}

	@Override
	protected BaseComponent createRoot() {
		GuiComponentPanel panel = new GuiComponentPanel(0, 0, xSize, ySize, getContainer());
		panel.setSlotRenderer(2, GuiComponentPanel.bigSlot);
		return panel;
	}

}
