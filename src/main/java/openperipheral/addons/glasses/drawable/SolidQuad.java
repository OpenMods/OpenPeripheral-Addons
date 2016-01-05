package openperipheral.addons.glasses.drawable;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.*;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@AdapterSourceName("glasses_quad")
public class SolidQuad extends Quad<Point2d> {

	@Property
	@StructureField
	public int color = 0xFFFFFF;

	@Property
	@StructureField
	public float opacity = 1.0f;

	public SolidQuad() {
		super(Point2d.NULL, Point2d.NULL, Point2d.NULL, Point2d.NULL);
	}

	public SolidQuad(Point2d p1, Point2d p2, Point2d p3, Point2d p4, int color, float opacity) {
		super(p1, p2, p3, p4);

		this.color = color;
		this.opacity = opacity;
	}

	@Override
	protected void drawContents(float partialTicks) {
		RenderStateHelper.color(color, opacity);
		super.drawContents(partialTicks);
	}

	@Override
	protected IPointListBuilder<Point2d> createBuilder() {
		return new PointListBuilder();
	}

	@Override
	protected DrawableType getTypeEnum() {
		return DrawableType.QUAD;
	}

}
