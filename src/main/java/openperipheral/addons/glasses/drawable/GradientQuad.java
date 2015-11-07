package openperipheral.addons.glasses.drawable;

import openperipheral.addons.glasses.utils.ColorPoint2d;
import openperipheral.addons.glasses.utils.ColorPointListBuilder;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@AdapterSourceName("glasses_gradient_quad")
public class GradientQuad extends Quad<ColorPoint2d> {

	public GradientQuad() {
		this(ColorPoint2d.NULL, ColorPoint2d.NULL, ColorPoint2d.NULL, ColorPoint2d.NULL);
	}

	public GradientQuad(ColorPoint2d p1, ColorPoint2d p2, ColorPoint2d p3, ColorPoint2d p4) {
		super(p1, p2, p3, p4);
	}

	@Override
	protected IPointListBuilder<ColorPoint2d> createBuilder() {
		return new ColorPointListBuilder();
	}

	@Override
	protected DrawableType getTypeEnum() {
		return DrawableType.GRADIENT_QUAD;
	}

}
