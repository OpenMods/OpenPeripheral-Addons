package openperipheral.addons.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;
import openperipheral.addons.Config;
import openperipheral.addons.common.tileentity.TileEntityGlassesBridge;

public class BlockGlassesBridge extends OpenBlock {

	public BlockGlassesBridge() {
		super(Config.blockGlassesBridgeId, Material.ground);
		setupBlock(this, "glassesbridge", TileEntityGlassesBridge.class);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
