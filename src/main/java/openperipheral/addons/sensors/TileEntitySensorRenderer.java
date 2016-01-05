package openperipheral.addons.sensors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class TileEntitySensorRenderer extends TileEntitySpecialRenderer<TileEntitySensor> {

	@Override
	public void renderTileEntityAt(TileEntitySensor sensor, double x, double y, double z, float partialTick, int destroyStage) {
		GL11.glPushMatrix();

		final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		final BlockPos pos = sensor.getPos();
		final World world = sensor.getWorld();
		final IBlockState state = world.getBlockState(pos);
		final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(state.withProperty(BlockSensor.STATIC, false));

		final Tessellator tes = Tessellator.getInstance();
		final WorldRenderer wr = tes.getWorldRenderer();
		bindTexture(TextureMap.locationBlocksTexture);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		GlStateManager.shadeModel(Minecraft.isAmbientOcclusionEnabled()? GL11.GL_SMOOTH : GL11.GL_FLAT);

		final float rotation = sensor.getRotation() - 1 + partialTick;
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glRotated(rotation, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);

		wr.setTranslation(0, 0, 0);
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		blockRenderer.getBlockModelRenderer().renderModel(world, model, state, BlockPos.ORIGIN, wr, false);
		tes.draw();

		RenderHelper.enableStandardItemLighting();

		GL11.glPopMatrix();

	}

}