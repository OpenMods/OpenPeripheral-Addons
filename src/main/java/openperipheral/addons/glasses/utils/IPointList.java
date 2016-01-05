package openperipheral.addons.glasses.utils;

public interface IPointList<T> extends Iterable<T> {

	void drawPoint(int index);

	public void drawAllPoints();

	public int size();
}