package openperipheral.addons.selector;

import openmods.gui.BaseGuiContainer;
import openmods.gui.IComponentParent;
import openmods.gui.component.BaseComposite;
import openmods.gui.component.GuiComponentPanel;

public class GuiSelector extends BaseGuiContainer<ContainerSelector> {

	public GuiSelector(ContainerSelector container) {
		super(container, 176, 167, "openperipheral.gui.selector");
	}

	@Override
	protected BaseComposite createRoot(IComponentParent parent) {
		GuiComponentPanel panel = new GuiComponentPanel(parent, 0, 0, xSize, ySize, getContainer());
		panel.setSlotRenderer(0, GuiComponentPanel.coloredSlot(0xFFFFFF));

		panel.setSlotRenderer(1, GuiComponentPanel.coloredSlot(0xBBBBBB));
		panel.setSlotRenderer(3, GuiComponentPanel.coloredSlot(0xBBBBBB));
		panel.setSlotRenderer(4, GuiComponentPanel.coloredSlot(0xBBBBBB));

		panel.setSlotRenderer(2, GuiComponentPanel.coloredSlot(0x888888));
		panel.setSlotRenderer(5, GuiComponentPanel.coloredSlot(0x888888));
		panel.setSlotRenderer(6, GuiComponentPanel.coloredSlot(0x888888));
		panel.setSlotRenderer(7, GuiComponentPanel.coloredSlot(0x888888));
		panel.setSlotRenderer(8, GuiComponentPanel.coloredSlot(0x888888));
		return panel;
	}

}
