package openperipheral.addons.glasses.utils;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;

public abstract class PointList<T> implements IPointList<T> {

	private final List<T> points;

	public PointList(List<T> points) {
		this.points = ImmutableList.copyOf(points);
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
	public Iterator<T> iterator() {
		return points.iterator();
	}

	@Override
	public int size() {
		return points.size();
	}
}
