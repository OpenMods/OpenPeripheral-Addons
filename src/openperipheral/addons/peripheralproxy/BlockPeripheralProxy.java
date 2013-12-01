package openperipheral.addons.peripheralproxy;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.addons.Config;
import openperipheral.addons.BlockOP;

public class BlockPeripheralProxy extends BlockOP {

	public static class Icons {
		public static Icon top;
		public static Icon bottom;
		public static Icon side;
	}

	public BlockPeripheralProxy() {
		super(Config.blockPeripheralProxyId, Material.ground);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
	}

	public void registerIcons(IconRegister registry) {
		Icons.top = registry.registerIcon("openperipheraladdons:proxy_top");
		Icons.bottom = registry.registerIcon("openperipheraladdons:proxy_bottom");
		Icons.side = registry.registerIcon("openperipheraladdons:proxy_side");
		setTexture(ForgeDirection.UP, Icons.top);
		setTexture(ForgeDirection.DOWN, Icons.bottom);
		setTexture(ForgeDirection.EAST, Icons.side);
		setTexture(ForgeDirection.WEST, Icons.side);
		setTexture(ForgeDirection.NORTH, Icons.side);
		setTexture(ForgeDirection.SOUTH, Icons.side);
		setDefaultTexture(Icons.side);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
