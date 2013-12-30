package openperipheral.addons.pim;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import openmods.renderer.IBlockRenderer;
import openmods.utils.render.RenderUtils;
import openperipheral.addons.OpenPeripheralAddons.Blocks;

public class BlockPIMRenderer implements IBlockRenderer {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		block.setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
		renderer.setRenderBoundsFromBlock(block);
		((BlockPIM)block).secondPass = false;
		RenderUtils.renderInventoryBlock(renderer, block, ForgeDirection.EAST);
		((BlockPIM)block).secondPass = true;
        block.setBlockBounds(0.0625f, 0.001f, 0.0625f, 0.9375f, 0.4f, 0.9375f);
		renderer.setRenderBoundsFromBlock(block);
		RenderUtils.renderInventoryBlock(renderer, block, ForgeDirection.EAST);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {

		boolean hasPlayer = false;
		TileEntity pim = world.getBlockTileEntity(x, y, z);
		if (pim instanceof TileEntityPIM) {
			hasPlayer = ((TileEntityPIM) pim).hasPlayer();
		}
		
		block.setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
		renderer.setRenderBoundsFromBlock(block);
		((BlockPIM)block).secondPass = false;
		renderer.renderStandardBlock(block, x, y, z);
		((BlockPIM)block).secondPass = true;
        block.setBlockBounds(0.0625f, 0.001f, 0.0625f, 0.9375f, hasPlayer ? 0.32f : 0.4f, 0.9375f);
		renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
		return true;
		
	}


}
