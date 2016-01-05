package openperipheral.addons.glasses.drawable;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.addons.glasses.utils.RenderStateHelper;
import openperipheral.api.adapter.Property;

import org.lwjgl.opengl.GL11;

public abstract class Line<P> extends BoundedShape<P> {

	@Property
	@StructureField
	public float width = 1;

	@Property
	@StructureField
	public P p1;

	@Property
	@StructureField
	public P p2;

	public Line(P p1, P p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	protected void drawContents(float partialTicks) {
		if (pointList != null) {
			super.drawContents(partialTicks);

			RenderStateHelper.setLineWidth(width);

			GL11.glBegin(GL11.GL_LINES);
			pointList.drawPoint(0);
			pointList.drawPoint(1);
			GL11.glEnd();
		}
	}

	@Override
	protected boolean isVisible() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (width <= 0) width = 1;
	}

	@Override
	protected void addPoints(IPointListBuilder<P> points) {
		points.add(p1);
		points.add(p2);
	}

}
