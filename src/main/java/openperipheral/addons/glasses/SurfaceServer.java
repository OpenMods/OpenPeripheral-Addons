package openperipheral.addons.glasses;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import openmods.structured.IStructureElement;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.drawable.*;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.ScriptObject;

import com.google.common.base.Objects;

@ScriptObject
@Asynchronous
@AdapterSourceName("glasses_surface")
public class SurfaceServer extends StructuredDataMaster<Drawable, IStructureElement> implements IDrawableContainer {

	public final boolean isPrivate;

	public SurfaceServer(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	@Override
	public synchronized Drawable getById(int id) {
		return containers.get(id - 1);
	}

	@Override
	public synchronized void clear() {
		for (Drawable drawable : containers.values())
			drawable.setDeleted();
		removeAll();
	}

	@Override
	public synchronized Set<Integer> getAllIds() {
		return Collections.unmodifiableSet(containers.keySet());
	}

	@Override
	public synchronized Map<Integer, Drawable> getAllObjects() {
		return Collections.unmodifiableMap(containers);
	}

	private synchronized Drawable addDrawable(Drawable drawable) {
		int id = addContainer(drawable);
		drawable.onAdded(this, id);
		return drawable;
	}

	private static Integer defaultColor(Integer color) {
		return Objects.firstNonNull(color, 0xFFFFFF);
	}

	private static Float defaultOpacity(Float opacity) {
		return Objects.firstNonNull(opacity, 1.0f);
	}

	@Override
	public synchronized Drawable addText(short x, short y, String text, Integer color) {
		return addDrawable(new Text(x, y, text, defaultColor(color)));
	}

	@Override
	public synchronized Drawable addBox(short x, short y, short width, short height, Integer color, Float opacity) {
		return addDrawable(new SolidBox(x, y, width, height, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public synchronized Drawable addGradientBox(short x, short y, short width, short height, int color, float alpha, int color2, float alpha2, int gradient) {
		return addDrawable(new GradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient));
	}

	@Override
	public synchronized Drawable addIcon(short x, short y, String id, Short meta) {
		return addDrawable(new ItemIcon(x, y, id, meta != null? meta : 0));
	}

	@Override
	public synchronized Drawable addLiquid(short x, short y, short width, short height, String id) {
		return addDrawable(new LiquidIcon(x, y, width, height, id));
	}

	@Override
	public Drawable addTriangle(Point2d p1, Point2d p2, Point2d p3, Integer color, Float opacity) {
		return addDrawable(new Triangle(p1, p2, p3, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addQuad(Point2d p1, Point2d p2, Point2d p3, Point2d p4, Integer color, Float opacity) {
		return addDrawable(new Quad(p1, p2, p3, p4, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addLine(Point2d p1, Point2d p2, Integer color, Float opacity) {
		return addDrawable(new Line(p1, p2, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addLineList(Integer color, Float opacity, Point2d... points) {
		return addDrawable(new LineStrip(defaultColor(color), defaultOpacity(opacity), points));
	}

	@Override
	public Drawable addPoint(Point2d p, Integer color, Float opacity) {
		return addDrawable(new Point(p, defaultColor(color), defaultOpacity(opacity)));
	}

}