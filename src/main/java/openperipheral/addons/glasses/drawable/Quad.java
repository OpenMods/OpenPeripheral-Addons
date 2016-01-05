package openperipheral.addons.glasses.drawable;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

@ScriptObject
@AdapterSourceName("glasses_quad")
public abstract class Quad<P> extends BoundedShape<P> {

	@Property
	@StructureField
	public P p1;

	@Property
	@StructureField
	public P p2;

	@Property
	@StructureField
	public P p3;

	@Property
	@StructureField
	public P p4;

	public Quad(P p1, P p2, P p3, P p4) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	@Override
	protected void drawContents(float partialTicks) {
		if (pointList != null) {
			super.drawContents(partialTicks);

			GL11.glBegin(GL11.GL_QUADS);
			pointList.drawPoint(0);
			pointList.drawPoint(1);
			pointList.drawPoint(2);
			pointList.drawPoint(3);
			GL11.glEnd();
		}
	}

	@Override
	protected boolean isVisible() {
		return true;
	}

	@Override
	protected void addPoints(IPointListBuilder<P> points) {
		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
	}

}
