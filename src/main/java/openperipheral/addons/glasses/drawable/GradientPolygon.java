package openperipheral.addons.glasses.drawable;

import openperipheral.addons.glasses.utils.ColorPoint2d;
import openperipheral.addons.glasses.utils.ColorPointListBuilder;
import openperipheral.addons.glasses.utils.GradientPolygonBuilder;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.addons.glasses.utils.IPolygonBuilder;
import openperipheral.addons.glasses.utils.RenderState;
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
	protected void drawContents(RenderState renderState, float partialTicks) {
		if (canRender()) {
			super.drawContents(renderState, partialTicks);
			renderPolygon(renderState);
		}
	}

}
