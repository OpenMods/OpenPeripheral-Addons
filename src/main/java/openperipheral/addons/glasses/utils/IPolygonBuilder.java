package openperipheral.addons.glasses.utils;

public interface IPolygonBuilder<P> {

	public void addPoint(P point);

	public Runnable build();
}
