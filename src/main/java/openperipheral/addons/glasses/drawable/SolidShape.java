package openperipheral.addons.glasses.drawable;

import java.util.List;

import openmods.geometry.BoundingBoxBuilder;
import openmods.geometry.Box2d;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.Point2d;
import openperipheral.addons.glasses.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

@AdapterSourceName("glasses_shape")
public abstract class SolidShape extends Drawable {

	@Property
	@StructureField
	public int color;

	@Property
	@StructureField
	public float opacity;

	protected final List<Point2d> relPoints = Lists.newArrayList();

	SolidShape() {}

	public SolidShape(int color, float opacity) {
		this.color = color;
		this.opacity = opacity;
	}

	@Override
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.setFlatShadeModel();
		renderState.setupSolidRender();
		renderState.setColor(color, opacity);
	}

	protected void drawPoint(int index) {
		final Point2d p = relPoints.get(index);
		GL11.glVertex2i(p.x, p.y);
	}

	protected void drawPoints() {
		for (Point2d p : relPoints)
			GL11.glVertex2i(p.x, p.y);
	}

	protected abstract void addPoints(List<Point2d> points);

	@Override
	protected void onUpdate() {
		updateBoundingBox();
	}

	private static Point2d toBoundingBox(Box2d bb, Point2d point) {
		return new Point2d(point.x - bb.left, point.y - bb.top);
	}

	protected final void updateBoundingBox() {
		List<Point2d> absPoints = Lists.newArrayList();

		addPoints(absPoints);

		final BoundingBoxBuilder bbBuilder = BoundingBoxBuilder.create();

		for (Point2d p : absPoints)
			bbBuilder.addPoint(p.x, p.y);

		final Box2d bb = bbBuilder.build();
		setBoundingBox(bb);

		relPoints.clear();
		for (Point2d p : absPoints)
			relPoints.add(toBoundingBox(bb, p));
	}

}
