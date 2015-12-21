package openperipheral.addons.peripheralproxy;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.block.BlockRotationMode;
import openperipheral.addons.BlockOP;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPeripheralProxy extends BlockOP {

	public BlockPeripheralProxy() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setRenderMode(RenderMode.BLOCK_ONLY);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);

		setTexture(ForgeDirection.UP, registry.registerIcon("openperipheraladdons:proxy_top"));
		setTexture(ForgeDirection.DOWN, registry.registerIcon("openperipheraladdons:proxy_bottom"));
	}
}
