package openperipheral.addons.glasses.utils;

import java.util.Iterator;
import java.util.List;

import openmods.geometry.Box2d;

import com.google.common.collect.ImmutableList;

public abstract class PointList<T> implements IPointList<T> {

	private final List<T> points;

	private final Box2d boundingBox;

	public PointList(List<T> points, Box2d boundingBox) {
		this.points = ImmutableList.copyOf(points);
		this.boundingBox = boundingBox;
	}

	@Override
	public void drawAllPoints(RenderState renderState) {
		for (T p : points)
			drawPoint(renderState, p);
	}

	protected abstract void drawPoint(RenderState renderState, T p);

	@Override
	public void drawPoint(RenderState renderState, int index) {
		final T p = points.get(index);
		drawPoint(renderState, p);
	}

	@Override
	public Box2d getBoundingBox() {
		return boundingBox;
	}

	@Override
	public Iterator<T> iterator() {
		return points.iterator();
	}

	@Override
	public int size() {
		return points.size();
	}
}
