package openperipheral.addons.glasses.drawable;

import java.util.Arrays;
import java.util.List;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.addons.glasses.utils.RenderStateHelper;
import openperipheral.api.adapter.IndexedProperty;
import openperipheral.api.adapter.Property;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public abstract class LineStrip<P> extends BoundedShape<P> {

	@Property
	@IndexedProperty(expandable = true, nullable = true)
	@StructureField
	public List<P> points = Lists.newArrayList();

	@Property
	@StructureField
	public float width = 1;

	public LineStrip(P... points) {
		this.points.addAll(Arrays.asList(points));
	}

	@Override
	protected void drawContents(float partialTicks) {
		if (points != null) {
			super.drawContents(partialTicks);

			RenderStateHelper.setLineWidth(width);

			GL11.glBegin(GL11.GL_LINE_STRIP);
			pointList.drawAllPoints();
			GL11.glEnd();
		}
	}

	@Override
	protected boolean isVisible() {
		return pointList.size() > 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (width <= 0) width = 1;
	}

	@Override
	protected void addPoints(IPointListBuilder<P> builder) {
		for (P p : points)
			if (p != null) builder.add(p);
	}

}
