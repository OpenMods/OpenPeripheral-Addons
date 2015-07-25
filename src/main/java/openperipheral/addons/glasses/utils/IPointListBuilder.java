package openperipheral.addons.glasses.utils;

public interface IPointListBuilder<P> {
	public void add(P point);

	public IPointList<P> build();
}
