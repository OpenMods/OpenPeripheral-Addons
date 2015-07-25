package openperipheral.addons.glasses.drawable;

import openperipheral.addons.glasses.utils.ColorPoint2d;
import openperipheral.addons.glasses.utils.ColorPointListBuilder;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@AdapterSourceName("glasses_gradient_triangle")
public class GradientTriangle extends Triangle<ColorPoint2d> {

	public GradientTriangle() {
		this(ColorPoint2d.NULL, ColorPoint2d.NULL, ColorPoint2d.NULL);
	}

	public GradientTriangle(ColorPoint2d p1, ColorPoint2d p2, ColorPoint2d p3) {
		super(p1, p2, p3);
	}

	@Override
	protected IPointListBuilder<ColorPoint2d> createBuilder() {
		return new ColorPointListBuilder();
	}

	@Override
	protected Type getTypeEnum() {
		return Type.GRADIENT_TRIANGLE;
	}

}
