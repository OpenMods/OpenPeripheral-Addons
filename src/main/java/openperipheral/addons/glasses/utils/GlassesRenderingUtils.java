package openperipheral.addons.glasses.utils;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

public final class GlassesRenderingUtils {

	public static void drawTexturedQuad(double x, double y, IIcon icon, float width, float height, float uMax, float vMax) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float textureU = icon.getMinU() + (icon.getMaxU() - icon.getMinU()) * uMax;
		float textureV = icon.getMinV() + (icon.getMaxV() - icon.getMinV()) * vMax;
		tessellator.addVertexWithUV(x + 0, y + height, 0D, icon.getMinU(), textureV);
		tessellator.addVertexWithUV(x + width, y + height, 0D, textureU, textureV);
		tessellator.addVertexWithUV(x + width, y + 0, 0D, textureU, icon.getMinV());
		tessellator.addVertexWithUV(x + 0, y + 0, 0D, icon.getMinU(), icon.getMinV());
		tessellator.draw();
	}

}