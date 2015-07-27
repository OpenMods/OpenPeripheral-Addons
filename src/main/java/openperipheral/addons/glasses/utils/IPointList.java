package openperipheral.addons.glasses.utils;

public interface IPointList<T> extends Iterable<T> {

	void drawPoint(RenderState renderState, int index);

	public void drawAllPoints(RenderState renderState);

	public int size();
}