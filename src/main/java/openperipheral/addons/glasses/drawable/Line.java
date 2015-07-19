package openperipheral.addons.glasses.drawable;

import java.util.List;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.Point2d;
import openperipheral.addons.glasses.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

@ScriptObject
@AdapterSourceName("glasses_line")
public class Line extends SolidShape {

	@Property
	@StructureField
	public float width = 1;

	@Property
	@StructureField
	public Point2d p1 = Point2d.NULL;

	@Property
	@StructureField
	public Point2d p2 = Point2d.NULL;

	Line() {}

	public Line(Point2d p1, Point2d p2, int color, float opacity) {
		super(color, opacity);
		this.p1 = p1;
		this.p2 = p2;

		updateBoundingBox();
	}

	@Override
	protected void drawContents(RenderState renderState, float partialTicks) {
		super.drawContents(renderState, partialTicks);

		renderState.setLineWidth(width);

		GL11.glBegin(GL11.GL_LINES);
		drawPoint(0);
		drawPoint(1);
		GL11.glEnd();
	}

	@Override
	protected Type getTypeEnum() {
		return Type.LINE;
	}

	@Override
	protected boolean isVisible() {
		return true;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		if (width <= 0) width = 1;
	}

	@Override
	protected void addPoints(List<Point2d> points) {
		points.add(p1);
		points.add(p2);
	}

}
