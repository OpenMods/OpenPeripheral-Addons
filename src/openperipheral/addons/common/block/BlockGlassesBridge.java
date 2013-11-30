package openperipheral.addons.common.block;

import net.minecraft.block.material.Material;
import openperipheral.addons.Config;

public class BlockGlassesBridge extends BlockOP {

	public BlockGlassesBridge() {
		super(Config.blockGlassesBridgeId, Material.ground);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
