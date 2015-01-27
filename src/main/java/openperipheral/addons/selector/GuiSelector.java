package openperipheral.addons.selector;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openmods.Log;
import openmods.gui.BaseGuiContainer;
import openmods.network.DimCoord;

public class GuiSelector extends BaseGuiContainer<ContainerSelector> {

	public GuiSelector(ContainerSelector container) {
		super(container, 176, 167, "openperipheral.gui.selector");
	}

}
