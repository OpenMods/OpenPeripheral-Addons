package openperipheral.addons.peripheralproxy;

import net.minecraft.block.material.Material;
import openmods.block.BlockRotationMode;
import openmods.block.OpenBlock;

public class BlockPeripheralProxy extends OpenBlock {

	public BlockPeripheralProxy() {
		super(Material.ground);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
	}

	@Override
	public BlockRotationMode getRotationMode() {
		return BlockRotationMode.SIX_DIRECTIONS;
	}

}
