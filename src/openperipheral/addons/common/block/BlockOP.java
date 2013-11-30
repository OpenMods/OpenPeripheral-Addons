package openperipheral.addons.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;
import openperipheral.addons.OpenPeripheralAddons;

public abstract class BlockOP extends OpenBlock {

	public BlockOP(int id, Material material) {
		super(id, material);
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
	}

	@Override
	protected String getModId() {
		return "openperipheral";
	}

}
