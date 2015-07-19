package openperipheral.addons.glasses.drawable;

import java.util.Arrays;
import java.util.List;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.Point2d;
import openperipheral.addons.glasses.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.IndexedProperty;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

@ScriptObject
@AdapterSourceName("glasses_line_strip")
public class LineStrip extends SolidShape {

	@Property
	@IndexedProperty(expandable = true, nullable = true)
	@StructureField
	public List<Point2d> points = Lists.newArrayList();

	@Property
	@StructureField
	public float width = 1;

	LineStrip() {}

	public LineStrip(int color, float opacity, Point2d... points) {
		super(color, opacity);
		this.points.addAll(Arrays.asList(points));

		updateBoundingBox();
	}

	@Override
	protected void drawContents(RenderState renderState, float partialTicks) {
		super.drawContents(renderState, partialTicks);

		renderState.setLineWidth(width);

		GL11.glBegin(GL11.GL_LINE_STRIP);
		drawPoints();
		GL11.glEnd();
	}

	@Override
	protected Type getTypeEnum() {
		return Type.LINE_STRIP;
	}

	@Override
	protected boolean isVisible() {
		return relPoints.size() > 1;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		if (width <= 0) width = 1;
	}

	@Override
	protected void addPoints(List<Point2d> result) {
		for (Point2d p : points)
			if (p != null) result.add(p);
	}

}
