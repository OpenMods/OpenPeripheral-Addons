package openperipheral.addons.client;

import openmods.renderer.BlockRenderingHandlerBase;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import openperipheral.addons.pim.BlockPIMRenderer;

public class BlockRenderingHandler extends BlockRenderingHandlerBase {

	public BlockRenderingHandler() {
		blockRenderers.put(Blocks.pim, new BlockPIMRenderer());
	}
	
	@Override
	public int getRenderId() {
		return OpenPeripheralAddons.renderId;
	}

}
