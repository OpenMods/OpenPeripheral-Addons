package openperipheral.addons.glasses;

import org.lwjgl.opengl.GL11;

public class RenderState {

	private boolean lighting;

	private boolean alphaTest;

	private boolean depthTest;

	private boolean texture;

	private boolean blend;

	private float pointSize;

	private float lineWidth;

	private int shadeModel;

	private boolean cullFace;

	private int color;

	public void forceKnownState() {
		GL11.glShadeModel(GL11.GL_FLAT);
		this.shadeModel = GL11.GL_FLAT;

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

		GL11.glDisable(GL11.GL_CULL_FACE);
		this.cullFace = false;

		GL11.glPointSize(1.0f);
		this.pointSize = 1.0f;

		GL11.glLineWidth(1.0f);
		this.lineWidth = 1.0f;
	}

	public void setupSolidRender() {
		disableLight();
		disableAlphaTest();
		disableTexture();
		disableDepthTest();
		enableBlending();
		disableCullFace();
	}

	private void setShadeModel(int newShadeModel) {
		if (shadeModel != newShadeModel) {
			shadeModel = newShadeModel;
			GL11.glShadeModel(shadeModel);
		}
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

	public void setFlatShadeModel() {
		setShadeModel(GL11.GL_FLAT);
	}

	public void setSmoothShadeModel() {
		setShadeModel(GL11.GL_SMOOTH);
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

	public void enableAlpha() {
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
