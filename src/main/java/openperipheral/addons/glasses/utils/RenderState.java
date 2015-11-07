package openperipheral.addons.glasses.utils;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class RenderState {

	private final FloatBuffer colors = BufferUtils.createFloatBuffer(16);

	private boolean lighting;

	private boolean alphaTest;

	private boolean depthTest;

	private boolean texture;

	private boolean blend;

	private int blendSrc;

	private int blendDst;

	private float pointSize;

	private float lineWidth;

	private boolean cullFace;

	private int color;

	public void forceKnownState() {
		GL11.glDisable(GL11.GL_LIGHTING);
		this.lighting = false;

		GL11.glDisable(GL11.GL_ALPHA_TEST);
		this.alphaTest = false;

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		this.texture = false;

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		this.depthTest = false;

		GL11.glEnable(GL11.GL_BLEND);
		this.blend = true;

		this.blendDst = GL11.GL_ONE_MINUS_SRC_ALPHA;
		this.blendSrc = GL11.GL_SRC_ALPHA;
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glDisable(GL11.GL_CULL_FACE);
		this.cullFace = false;

		GL11.glPointSize(1.0f);
		this.pointSize = 1.0f;

		GL11.glLineWidth(1.0f);
		this.lineWidth = 1.0f;
	}

	public void readState() {
		this.lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
		this.alphaTest = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
		this.texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
		this.depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
		this.blend = GL11.glIsEnabled(GL11.GL_BLEND);
		this.blendSrc = GL11.glGetInteger(GL11.GL_BLEND_SRC);
		this.blendDst = GL11.glGetInteger(GL11.GL_BLEND_DST);
		this.cullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE);
		this.lineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		this.pointSize = GL11.glGetFloat(GL11.GL_POINT_SIZE);

		GL11.glGetFloat(GL11.GL_CURRENT_COLOR, colors);

		float r = colors.get();
		float g = colors.get();
		float b = colors.get();
		float a = colors.get();
		this.color = (((int)(255 * r) & 0xFF) << 24) + (((int)(255 * g) & 0xFF) << 16) + (((int)(255 * b) & 0xFF) << 8) + (((int)(255 * a) & 0xFF) << 0);
		colors.clear();
	}

	public void setupCommonRender() {
		disableLight();
		disableAlphaTest();
		disableDepthTest();
		enableBlending();
		setBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		disableCullFace();
	}

	public void setupSolidRender() {
		setupCommonRender();
		disableTexture();
	}

	public void setupTexturedRender() {
		setupCommonRender();
		enableTexture();
	}

	public void setColor(int rgb, float opacity) {
		final byte scaledOpacity = (byte)(opacity * 255);
		int newColor = rgb << 8 | (scaledOpacity & 0xFF);

		if (newColor != color) {
			final byte r = (byte)(rgb >> 16);
			final byte g = (byte)(rgb >> 8);
			final byte b = (byte)(rgb >> 0);

			GL11.glColor4ub(r, g, b, scaledOpacity);
			this.color = newColor;
		}
	}

	public void setLineWidth(float lineWidth) {
		if (lineWidth != this.lineWidth) {
			GL11.glLineWidth(lineWidth);
			this.lineWidth = lineWidth;
		}
	}

	public void setPointSize(final float pointSize) {
		if (pointSize != this.pointSize) {
			GL11.glPointSize(pointSize);
			this.pointSize = pointSize;
		}
	}

	public void enableBlending() {
		if (!blend) {
			GL11.glEnable(GL11.GL_BLEND);
			blend = true;
		}
	}

	public void setBlendFunc(int src, int dst) {
		if (dst != this.blendDst || src != this.blendSrc) {
			GL11.glBlendFunc(src, dst);
			this.blendDst = dst;
			this.blendSrc = src;
		}
	}

	public void disableBlending() {
		if (blend) {
			GL11.glDisable(GL11.GL_BLEND);
			blend = false;
		}
	}

	public void enableDepthTest() {
		if (!depthTest) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			depthTest = true;
		}
	}

	public void disableDepthTest() {
		if (depthTest) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			depthTest = false;
		}
	}

	public void enableTexture() {
		if (!texture) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			texture = true;
		}
	}

	public void disableTexture() {
		if (texture) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			texture = false;
		}
	}

	public void enableAlphaTest() {
		if (!alphaTest) {
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			alphaTest = true;
		}
	}

	public void disableAlphaTest() {
		if (alphaTest) {
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			alphaTest = false;
		}
	}

	public void enableLight() {
		if (!lighting) {
			GL11.glEnable(GL11.GL_LIGHTING);
			lighting = true;
		}
	}

	public void disableLight() {
		if (lighting) {
			GL11.glDisable(GL11.GL_LIGHTING);
			lighting = false;
		}
	}

	public void enableCullFace() {
		if (!cullFace) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			cullFace = true;
		}
	}

	public void disableCullFace() {
		if (cullFace) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			cullFace = false;
		}
	}

}
