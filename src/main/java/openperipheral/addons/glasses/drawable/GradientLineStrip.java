package openperipheral.addons.glasses.drawable;

import openperipheral.addons.glasses.utils.ColorPoint2d;
import openperipheral.addons.glasses.utils.ColorPointListBuilder;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@AdapterSourceName("glasses_gradient_line_strip")
public class GradientLineStrip extends LineStrip<ColorPoint2d> {

	public GradientLineStrip() {}

	public GradientLineStrip(ColorPoint2d... points) {
		super(points);
	}

	@Override
	protected IPointListBuilder<ColorPoint2d> createBuilder() {
		return new ColorPointListBuilder();
	}

	@Override
	protected Type getTypeEnum() {
		return Type.GRADIENT_LINE_STRIP;
	}

}
