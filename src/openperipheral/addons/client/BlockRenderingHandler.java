package openperipheral.addons.client;

import openmods.renderer.BlockRenderingHandlerBase;
import openperipheral.addons.OpenPeripheralAddons;

public class BlockRenderingHandler extends BlockRenderingHandlerBase {

	@Override
	public int getRenderId() {
		return OpenPeripheralAddons.renderId;
	}

}
