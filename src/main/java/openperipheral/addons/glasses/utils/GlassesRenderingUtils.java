package openperipheral.addons.glasses.utils;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

public final class GlassesRenderingUtils {

	public static void drawTexturedQuad(double x, double y, TextureAtlasSprite icon, float width, float height, float uMax, float vMax) {
		final float textureU = icon.getMinU() + (icon.getMaxU() - icon.getMinU()) * uMax;
		final float textureV = icon.getMinV() + (icon.getMaxV() - icon.getMinV()) * vMax;

		Tessellator tessellator = Tessellator.getInstance();
		final WorldRenderer wr = tessellator.getWorldRenderer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		wr.pos(x + 0, y + height, 0D).tex(icon.getMinU(), textureV).endVertex();
		wr.pos(x + width, y + height, 0D).tex(textureU, textureV).endVertex();
		wr.pos(x + width, y + 0, 0D).tex(textureU, icon.getMinV()).endVertex();
		wr.pos(x + 0, y + 0, 0D).tex(icon.getMinU(), icon.getMinV()).endVertex();
		tessellator.draw();
	}

}