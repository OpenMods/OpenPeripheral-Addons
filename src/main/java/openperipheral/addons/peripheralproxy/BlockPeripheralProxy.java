package openperipheral.addons.peripheralproxy;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.block.BlockRotationMode;
import openperipheral.addons.BlockOP;

public class BlockPeripheralProxy extends BlockOP {

	public static class Icons {
		public static IIcon top;
		public static IIcon bottom;
		public static IIcon side;
	}

	public BlockPeripheralProxy() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setRenderMode(RenderMode.BLOCK_ONLY);
	}

	@Override
	public void registerBlockIcons(IIconRegister registry) {
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
}
