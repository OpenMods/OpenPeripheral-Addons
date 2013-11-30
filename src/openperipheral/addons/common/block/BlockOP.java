package openperipheral.addons.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public abstract class BlockOP extends OpenBlock {

	public BlockOP(int id, Material material) {
		super(id, material);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getModId() {
		return "openperipheral";
	}

}
