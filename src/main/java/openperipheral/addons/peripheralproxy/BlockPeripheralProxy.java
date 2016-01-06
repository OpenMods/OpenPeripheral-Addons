package openperipheral.addons.peripheralproxy;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public class BlockPeripheralProxy extends OpenBlock.SixDirections {

	public BlockPeripheralProxy() {
		super(Material.ground);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
	}

}
