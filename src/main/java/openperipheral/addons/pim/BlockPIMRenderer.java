package openperipheral.addons.pim;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import openmods.renderer.IBlockRenderer;
import openmods.utils.render.RenderUtils;

public class BlockPIMRenderer implements IBlockRenderer<BlockPIM> {

	private static boolean hasPlayer(IBlockAccess world, int x, int y, int z) {
		TileEntity pim = world.getTileEntity(x, y, z);
		return (pim instanceof TileEntityPIM) && ((TileEntityPIM)pim).hasPlayer();
	}

	private static void setTopPartBounds(RenderBlocks renderer, final boolean hasPlayer) {
		renderer.setRenderBounds(1.0 / 16.0, 0.3, 1.0 / 16.0, 15.0 / 16.0, hasPlayer? (0.4 - 0.08) : 0.4, 15.0 / 16.0);
	}

	@Override
	public void renderInventoryBlock(BlockPIM block, int metadata, int modelID, RenderBlocks renderer) {
		RenderUtils.renderInventoryBlock(renderer, block, 0);

		renderer.setOverrideBlockTexture(BlockPIM.Icons.black);
		setTopPartBounds(renderer, false);
		RenderUtils.renderInventoryBlockNoBounds(renderer, block, 0);
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockPIM block, int modelId, RenderBlocks renderer) {
		renderer.setRenderBoundsFromBlock(block);
		renderer.renderStandardBlock(block, x, y, z);

		final boolean isBreaking = renderer.hasOverrideBlockTexture();
		if (!isBreaking) renderer.setOverrideBlockTexture(BlockPIM.Icons.black);

		final boolean hasPlayer = hasPlayer(world, x, y, z);
		setTopPartBounds(renderer, hasPlayer);
		renderer.renderStandardBlock(block, x, y, z);

		if (!isBreaking) renderer.clearOverrideBlockTexture();
		return true;
	}
}
