package openperipheral.addons.glasses.drawable;

import openmods.geometry.Box2d;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.Point2d;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

@ScriptObject
@AdapterSourceName("glasses_point")
public class Point extends Drawable {

	@Property
	@StructureField
	public int color;

	@Property
	@StructureField
	public float opacity;

	@Property
	@StructureField
	public Point2d coord = Point2d.NULL;

	@Property
	@StructureField
	public float size = 1;

	Point() {}

	public Point(Point2d coord, int color, float opacity) {
		this.coord = coord;
		this.color = color;
		this.opacity = opacity;

		updateBoundingBox();
	}

	private void updateBoundingBox() {
		setBoundingBox(Box2d.fromOriginAndSize(coord.x, coord.y, 0, 0));
	}

	@Override
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.setColor(color, opacity);

		renderState.setupSolidRender();
		renderState.setPointSize(size);

		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex2i(0, 0);
		GL11.glEnd();
	}

	@Override
	protected Type getTypeEnum() {
		return Type.POINT;
	}

	@Override
	protected boolean isVisible() {
		return true;
	}

	@Override
	public void onUpdate() {
		if (size <= 0) size = 1;
		updateBoundingBox();
	}

}
