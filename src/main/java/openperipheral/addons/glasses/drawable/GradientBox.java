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
@AdapterSourceName("glasses_gradient")
public class GradientBox extends Drawable {
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
	public int color1;

	@Property
	@StructureField
	public float opacity1;

	@Property
	@StructureField
	public int color2;

	@Property
	@StructureField
	public float opacity2;

	@Property
	@StructureField
	public int gradient;

	GradientBox() {}

	public GradientBox(short x, short y, short width, short height, int color1, float opacity1, int color2, float opacity2, int gradient) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));

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
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.setupSolidRender();

		{
			renderState.setColor(color1, opacity1);
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
			renderState.setColor(color2, opacity2);
			if (gradient == 1) {
				GL11.glVertex2i(width, 0);
				GL11.glVertex2i(0, 0);
			} else {
				GL11.glVertex2i(0, 0);
				GL11.glVertex2i(0, height);
			}
		}

		GL11.glEnd();
	}

	@Override
	public Type getTypeEnum() {
		return Type.GRADIENT;
	}

	@Override
	public boolean isVisible() {
		return opacity1 > 0 || opacity2 > 0;
	}

	@Override
	protected void onUpdate() {
		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));
	}
}