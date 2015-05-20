package openperipheral.addons.glasses.drawable;

import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_box")
public class SolidBox extends Drawable {
	@CallbackProperty
	public short width;

	@CallbackProperty
	public short height;

	@CallbackProperty
	public int color;

	@CallbackProperty
	public float opacity;

	SolidBox() {}

	public SolidBox(short x, short y, short width, short height, int color, float opacity) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.color = color;
		this.opacity = opacity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(float partialTicks) {
		final byte r = (byte)(color >> 16);
		final byte g = (byte)(color >> 8);
		final byte b = (byte)(color >> 0);

		GL11.glColor4ub(r, g, b, (byte)(opacity * 255));
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2i(0, 0);
		GL11.glVertex2i(0, height);
		GL11.glVertex2i(width, height);
		GL11.glVertex2i(width, 0);
		GL11.glEnd();
	}

	@Override
	public Type getTypeEnum() {
		return Type.BOX;
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
		return opacity > 0;
	}

	@Override
	protected void onUpdate() {}
}