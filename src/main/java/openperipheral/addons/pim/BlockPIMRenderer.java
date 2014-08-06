package openperipheral.addons.pim;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.renderer.IBlockRenderer;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class BlockPIMRenderer implements IBlockRenderer<BlockPIM> {

	private static void renderTop(Tessellator tes, boolean pressed, double x, double y, double z) {
		if (pressed) y -= 0.08;
		final IIcon icon = BlockPIM.Icons.black;

		tes.setColorOpaque(255, 255, 255);

		final double xm = x + 1.0 / 16.0;
		final double xp = x + 15.0 / 16.0;

		final double yp = y + 0.4;
		final double ym = y + 0.3;

		final double zm = z + 1.0 / 16.0;
		final double zp = z + 15.0 / 16.0;

		final float u0 = icon.getInterpolatedU(0);
		final float u1 = icon.getInterpolatedU(1);
		final float u15 = icon.getInterpolatedU(15);
		final float u16 = icon.getInterpolatedU(16);

		final float v0 = icon.getInterpolatedV(0);
		final float v1 = icon.getInterpolatedV(1);
		final float v15 = icon.getInterpolatedV(15);
		final float v16 = icon.getInterpolatedV(16);

		tes.addVertexWithUV(xm, yp, zm, u1, v1);
		tes.addVertexWithUV(xm, yp, zp, u1, v15);

		tes.addVertexWithUV(xp, yp, zp, u15, v15);
		tes.addVertexWithUV(xp, yp, zm, u15, v1);

		tes.addVertexWithUV(xm, yp, zm, u1, v1);

		tes.addVertexWithUV(xm, ym, zm, u0, v1);
		tes.addVertexWithUV(xm, ym, zp, u0, v15);
		tes.addVertexWithUV(xm, yp, zp, u1, v15);

		tes.addVertexWithUV(xp, yp, zm, u15, v1);
		tes.addVertexWithUV(xp, yp, zp, u15, v15);

		tes.addVertexWithUV(xp, ym, zp, u16, v15);
		tes.addVertexWithUV(xp, ym, zm, u16, v1);

		tes.addVertexWithUV(xm, yp, zp, u1, v15);
		tes.addVertexWithUV(xm, ym, zp, u1, v16);
		tes.addVertexWithUV(xp, ym, zp, u15, v16);
		tes.addVertexWithUV(xp, yp, zp, u15, v15);

		tes.addVertexWithUV(xm, yp, zm, u1, v0);
		tes.addVertexWithUV(xp, yp, zm, u15, v0);
		tes.addVertexWithUV(xp, ym, zm, u15, v1);
		tes.addVertexWithUV(xm, ym, zm, u1, v1);
	}

	@Override
	public void renderInventoryBlock(BlockPIM block, int metadata, int modelID, RenderBlocks renderer) {
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.5F, -0.4F, -0.5F);
		RenderUtils.renderInventoryBlock(renderer, block, ForgeDirection.EAST);
		Tessellator tes = new Tessellator();
		tes.startDrawingQuads();
		renderTop(tes, false, -0.5, -0.5, -0.5);
		tes.draw();
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockPIM block, int modelId, RenderBlocks renderer) {
		renderer.renderStandardBlock(block, x, y, z);
		boolean hasPlayer = hasPlayer(world, x, y, z);
		renderTop(Tessellator.instance, hasPlayer, x, y, z);
		return true;
	}

	protected boolean hasPlayer(IBlockAccess world, int x, int y, int z) {
		TileEntity pim = world.getTileEntity(x, y, z);
		return (pim instanceof TileEntityPIM) && ((TileEntityPIM)pim).hasPlayer();
	}
}
