package openperipheral.addons.glasses.drawable;

import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_gradient")
public class GradientBox extends Drawable {
	@CallbackProperty
	public short width;

	@CallbackProperty
	public short height;

	@CallbackProperty
	public int color1;

	@CallbackProperty
	public float opacity1;

	@CallbackProperty
	public int color2;

	@CallbackProperty
	public float opacity2;

	@CallbackProperty
	public int gradient;

	GradientBox() {}

	public GradientBox(short x, short y, short width, short height, int color1, float opacity1, int color2, float opacity2, int gradient) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.color1 = color1;
		this.opacity1 = opacity1;
		// compat hack
		if (gradient == 0) {
			this.color2 = color1;
			this.opacity2 = opacity1;
		} else {
			this.color2 = color2;
			this.opacity2 = opacity2;
		}
		this.gradient = gradient;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(float partialTicks) {
		GL11.glShadeModel(GL11.GL_SMOOTH);

		{
			final byte r = (byte)(color1 >> 16);
			final byte g = (byte)(color1 >> 8);
			final byte b = (byte)(color1 >> 0);

			GL11.glColor4ub(r, g, b, (byte)(opacity1 * 255));
			GL11.glBegin(GL11.GL_QUADS);
			if (gradient == 1) {
				GL11.glVertex2i(0, height);
				GL11.glVertex2i(width, height);
			} else {
				GL11.glVertex2i(width, height);
				GL11.glVertex2i(width, 0);
			}
		}

		{
			final byte r = (byte)(color2 >> 16);
			final byte g = (byte)(color2 >> 8);
			final byte b = (byte)(color2 >> 0);
			GL11.glColor4ub(r, g, b, (byte)(opacity2 * 255));

			if (gradient == 1) {
				GL11.glVertex2i(width, 0);
				GL11.glVertex2i(0, 0);
			} else {
				GL11.glVertex2i(0, 0);
				GL11.glVertex2i(0, height);
			}
		}

		GL11.glEnd();
		GL11.glShadeModel(GL11.GL_FLAT);
	}

	@Override
	public Type getTypeEnum() {
		return Type.GRADIENT;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean isVisible() {
		return opacity1 > 0 || opacity2 > 0;
	}

	@Override
	protected void onUpdate() {}
}