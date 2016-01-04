package openperipheral.addons.glasses.drawable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.geometry.Box2d;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

@ScriptObject
@AdapterSourceName("glasses_box")
public class SolidBox extends Drawable {
	@Property
	@StructureField
	public float x;

	@Property
	@StructureField
	public float y;

	@Property
	@StructureField
	public float width;

	@Property
	@StructureField
	public float height;

	@Property
	@StructureField
	public int color;

	@Property
	@StructureField
	public float opacity;

	SolidBox() {}

	public SolidBox(float x, float y, float width, float height, int color, float opacity) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.color = color;
		this.opacity = opacity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.setupSolidRender();
		renderState.setColor(color, opacity);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(0, 0);
		GL11.glVertex2f(0, height);
		GL11.glVertex2f(width, height);
		GL11.glVertex2f(width, 0);
		GL11.glEnd();
	}

	@Override
	public DrawableType getTypeEnum() {
		return DrawableType.BOX;
	}

	@Override
	public boolean isVisible() {
		return opacity > 0;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));
	}
}