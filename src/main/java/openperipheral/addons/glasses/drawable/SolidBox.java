package openperipheral.addons.glasses.drawable;

import openmods.geometry.Box2d;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_box")
public class SolidBox extends Drawable {
	@Property
	@StructureField
	public short x;

	@Property
	@StructureField
	public short y;

	@Property
	@StructureField
	public short width;

	@Property
	@StructureField
	public short height;

	@Property
	@StructureField
	public int color;

	@Property
	@StructureField
	public float opacity;

	SolidBox() {}

	public SolidBox(short x, short y, short width, short height, int color, float opacity) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));

		this.color = color;
		this.opacity = opacity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.setupSolidRender();
		renderState.setColor(color, opacity);

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
	public boolean isVisible() {
		return opacity > 0;
	}

	@Override
	protected void onUpdate() {
		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));
	}
}