package openperipheral.addons.glasses.utils;

import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;

public class RenderStateHelper {

	private static float currentPointSize = -1;

	private static float currentLineWidth = -1;

	public static void setupCommonRender() {
		GlStateManager.disableLighting();
		GlStateManager.disableAlpha();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableCull();
	}

	public static void setupSolidRender() {
		setupCommonRender();
		GlStateManager.disableTexture2D();
	}

	public static void setupTexturedRender() {
		setupCommonRender();
		GlStateManager.enableTexture2D();
	}

	public static void color(int rgb, float alpha) {

		final float r = (float)((rgb >> 16) & 0xFF) / 255;
		final float g = (float)((rgb >> 8) & 0xFF) / 255;
		final float b = (float)((rgb >> 0) & 0xFF) / 255;

		GlStateManager.color(r, g, b, alpha);
	}

	public static void setLineWidth(float lineWidth) {
		if (lineWidth != currentLineWidth) {
			GL11.glLineWidth(lineWidth);
			currentLineWidth = lineWidth;
		}
	}

	public static void setPointSize(float pointSize) {
		if (pointSize != currentPointSize) {
			GL11.glPointSize(pointSize);
			currentPointSize = pointSize;
		}
	}

}
