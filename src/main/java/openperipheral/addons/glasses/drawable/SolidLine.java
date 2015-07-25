package openperipheral.addons.glasses.drawable;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.*;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@AdapterSourceName("glasses_line")
public class SolidLine extends Line<Point2d> {

	@Property
	@StructureField
	public int color = 0xFFFFFF;

	@Property
	@StructureField
	public float opacity = 1.0f;

	public SolidLine() {
		this(Point2d.NULL, Point2d.NULL, 0x0, 0.0f);
	}

	public SolidLine(Point2d p1, Point2d p2, int color, float opacity) {
		super(p1, p2);

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
	protected Type getTypeEnum() {
		return Type.LINE;
	}

}
