package openperipheral.addons.glasses.drawable;

import java.util.Arrays;
import java.util.List;

import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.addons.glasses.utils.IPolygonBuilder;
import openperipheral.api.adapter.IndexedProperty;
import openperipheral.api.adapter.Property;

import com.google.common.collect.Lists;

public abstract class Polygon<P> extends BoundedShape<P> {

	@Property
	@StructureField
	@IndexedProperty(expandable = true, nullable = true)
	public List<P> points = Lists.newArrayList();

	private Runnable renderCommands;

	public Polygon(P... points) {
		this.points.addAll(Arrays.asList(points));
	}

	@Override
	protected void addPoints(IPointListBuilder<P> builder) {
		for (P p : points)
			if (p != null) builder.add(p);
	}

	@Override
	protected boolean isVisible() {
		return pointList.size() > 1;
	}

	protected final boolean canRender() {
		return renderCommands != null;
	}

	protected final void renderPolygon() {
		renderCommands.run();
	}

	protected abstract IPolygonBuilder<P> createPolygonBuilder();

	@Override
	public void onUpdate() {
		super.onUpdate();
		final IPolygonBuilder<P> builder = createPolygonBuilder();

		for (P point : pointList)
			builder.addPoint(point);

		this.renderCommands = builder.build();
	}

}
