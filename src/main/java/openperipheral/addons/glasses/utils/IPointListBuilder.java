package openperipheral.addons.glasses.utils;

import openmods.geometry.Box2d;

public interface IPointListBuilder<P> {
	public void add(P point);

	public IPointList<P> buildPointList();

	public Box2d buildBoundingBox();
}
