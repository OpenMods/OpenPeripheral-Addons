package openperipheral.addons.glasses.drawable;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.addons.glasses.utils.Point2d;
import openperipheral.addons.glasses.utils.PointListBuilder;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@AdapterSourceName("glasses_triangle")
public class SolidTriangle extends Triangle<Point2d> {

	@Property
	@StructureField
	public int color = 0xFFFFFF;

	@Property
	@StructureField
	public float opacity = 1.0f;

	public SolidTriangle() {
		super(Point2d.NULL, Point2d.NULL, Point2d.NULL);
	}

	public SolidTriangle(Point2d p1, Point2d p2, Point2d p3, int color, float opacity) {
		super(p1, p2, p3);

		this.color = color;
		this.opacity = opacity;
	}

	@Override
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.setColor(color, opacity);
		super.drawContents(renderState, partialTicks);
	}

	@Override
	protected IPointListBuilder<Point2d> createBuilder() {
		return new PointListBuilder();
	}

	@Override
	protected DrawableType getTypeEnum() {
		return DrawableType.TRIANGLE;
	}
}
