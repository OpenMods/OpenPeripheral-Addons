package openperipheral.addons.glasses.drawable;

import openperipheral.addons.glasses.utils.ColorPoint2d;
import openperipheral.addons.glasses.utils.ColorPointListBuilder;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@AdapterSourceName("glasses_gradient_line")
public class GradientLine extends Line<ColorPoint2d> {

	public GradientLine() {
		this(ColorPoint2d.NULL, ColorPoint2d.NULL);
	}

	public GradientLine(ColorPoint2d p1, ColorPoint2d p2) {
		super(p1, p2);
	}

	@Override
	protected IPointListBuilder<ColorPoint2d> createBuilder() {
		return new ColorPointListBuilder();
	}

	@Override
	protected DrawableType getTypeEnum() {
		return DrawableType.GRADIENT_LINE;
	}

}
