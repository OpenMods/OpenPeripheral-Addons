package openperipheral.addons;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public abstract class BlockOP extends OpenBlock {

	public BlockOP(int id, Material material) {
		super(id, material);
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
	}

	@Override
	public int getRenderType() {
		return OpenPeripheralAddons.renderId;
	}

	@Override
	protected String getModId() {
		return "openperipheral";
	}

}
