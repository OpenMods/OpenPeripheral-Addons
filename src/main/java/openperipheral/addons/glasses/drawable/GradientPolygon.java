package openperipheral.addons.glasses.drawable;

import openperipheral.addons.glasses.utils.*;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@AdapterSourceName("glasses_gradient_polygon")
public class GradientPolygon extends Polygon<ColorPoint2d> {

	public GradientPolygon() {}

	public GradientPolygon(ColorPoint2d... points) {
		super(points);
	}

	@Override
	protected IPointListBuilder<ColorPoint2d> createBuilder() {
		return new ColorPointListBuilder();
	}

	@Override
	protected IPolygonBuilder<ColorPoint2d> createPolygonBuilder() {
		return new GradientPolygonBuilder();
	}

	@Override
	protected DrawableType getTypeEnum() {
		return DrawableType.GRADIENT_POLYGON;
	}

	@Override
	protected void drawContents(float partialTicks) {
		if (canRender()) {
			super.drawContents(partialTicks);
			renderPolygon();
		}
	}

}
