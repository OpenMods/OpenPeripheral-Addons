package openperipheral.addons;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public abstract class BlockOP extends OpenBlock {

	public BlockOP(Material material) {
		super(material);
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
	}

	@Override
	public int getRenderType() {
		return OpenPeripheralAddons.renderId;
	}

	@Override
	protected Object getModInstance() {
		return OpenPeripheralAddons.instance;
	}

}
