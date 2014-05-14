package openperipheral.addons;

import openmods.item.ItemGeneric;

public class ItemOPGeneric extends ItemGeneric {

	public ItemOPGeneric() {
		super(Config.itemGenericId);
		setMaxStackSize(64);
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
	}
}
