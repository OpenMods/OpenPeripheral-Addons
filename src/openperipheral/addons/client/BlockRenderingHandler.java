package openperipheral.addons.client;

import openmods.renderer.BlockRenderingHandlerBase;
import openperipheral.addons.OpenPeripheralAddons;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRenderingHandler extends BlockRenderingHandlerBase implements ISimpleBlockRenderingHandler {

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return OpenPeripheralAddons.renderId;
	}

}
