package openperipheral.addons.glasses.utils;

import openmods.geometry.Box2d;

public interface IPointList<T> extends Iterable<T> {

	void drawPoint(RenderState renderState, int index);

	public void drawAllPoints(RenderState renderState);

	public Box2d getBoundingBox();

	public int size();
}