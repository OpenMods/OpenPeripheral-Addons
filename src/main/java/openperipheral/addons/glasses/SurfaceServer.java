package openperipheral.addons.glasses;

import java.util.Map;
import java.util.Set;

import openmods.structured.IStructureElement;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.drawable.*;
import openperipheral.addons.glasses.utils.ColorPoint2d;
import openperipheral.addons.glasses.utils.Point2d;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.ScriptObject;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.helpers.Index;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@ScriptObject
@Asynchronous
@AdapterSourceName("glasses_surface")
public class SurfaceServer extends StructuredDataMaster<Drawable, IStructureElement> implements IDrawableContainer {

	public final boolean isPrivate;

	public SurfaceServer(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	@Override
	public synchronized Drawable getById(Index id) {
		return containers.get(id.value);
	}

	@Override
	public synchronized void clear() {
		for (Drawable drawable : containers.values())
			drawable.setDeleted();
		removeAll();
	}

	@Override
	public synchronized Set<Index> getAllIds(IArchitecture access) {
		final Set<Index> indices = Sets.newHashSet();
		for (int value : containers.keySet())
			indices.add(access.createIndex(value));

		return indices;
	}

	@Override
	public synchronized Map<Index, Drawable> getAllObjects(IArchitecture access) {
		final Map<Index, Drawable> result = Maps.newHashMap();
		for (Map.Entry<Integer, Drawable> e : containers.entrySet())
			result.put(access.createIndex(e.getKey()), e.getValue());

		return result;
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
	public synchronized Drawable addText(float x, float y, String text, Integer color) {
		return addDrawable(new Text(x, y, text, defaultColor(color)));
	}

	@Override
	public synchronized Drawable addBox(float x, float y, float width, float height, Integer color, Float opacity) {
		return addDrawable(new SolidBox(x, y, width, height, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public synchronized Drawable addGradientBox(float x, float y, float width, float height, int color, float alpha, int color2, float alpha2, int gradient) {
		return addDrawable(new GradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient));
	}

	@Override
	public synchronized Drawable addIcon(float x, float y, String id, Short meta) {
		return addDrawable(new ItemIcon(x, y, id, meta != null? meta : 0));
	}

	@Override
	public synchronized Drawable addLiquid(float x, float y, float width, float height, String id) {
		return addDrawable(new LiquidIcon(x, y, width, height, id));
	}

	@Override
	public Drawable addTriangle(Point2d p1, Point2d p2, Point2d p3, Integer color, Float opacity) {
		return addDrawable(new SolidTriangle(p1, p2, p3, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addGradientTriangle(ColorPoint2d p1, ColorPoint2d p2, ColorPoint2d p3) {
		return addDrawable(new GradientTriangle(p1, p2, p3));
	}

	@Override
	public Drawable addQuad(Point2d p1, Point2d p2, Point2d p3, Point2d p4, Integer color, Float opacity) {
		return addDrawable(new SolidQuad(p1, p2, p3, p4, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addGradientQuad(ColorPoint2d p1, ColorPoint2d p2, ColorPoint2d p3, ColorPoint2d p4) {
		return addDrawable(new GradientQuad(p1, p2, p3, p4));
	}

	@Override
	public Drawable addLine(Point2d p1, Point2d p2, Integer color, Float opacity) {
		return addDrawable(new SolidLine(p1, p2, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addGradientLine(ColorPoint2d p1, ColorPoint2d p2) {
		return addDrawable(new GradientLine(p1, p2));
	}

	@Override
	public Drawable addLineList(Integer color, Float opacity, Point2d... points) {
		return addDrawable(new SolidLineStrip(defaultColor(color), defaultOpacity(opacity), points));
	}

	@Override
	public Drawable addGradientLineList(ColorPoint2d... points) {
		return addDrawable(new GradientLineStrip(points));
	}

	@Override
	public Drawable addPolygon(Integer color, Float opacity, Point2d... points) {
		return addDrawable(new SolidPolygon(defaultColor(color), defaultOpacity(opacity), points));
	}

	@Override
	public Drawable addGradientPolygon(ColorPoint2d... points) {
		return addDrawable(new GradientPolygon(points));
	}

	@Override
	public Drawable addPoint(Point2d p, Integer color, Float opacity) {
		return addDrawable(new Point(p, defaultColor(color), defaultOpacity(opacity)));
	}

}