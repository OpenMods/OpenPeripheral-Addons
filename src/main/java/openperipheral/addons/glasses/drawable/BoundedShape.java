package openperipheral.addons.glasses.drawable;

import openperipheral.addons.glasses.utils.IPointList;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;

@AdapterSourceName("glasses_shape")
public abstract class BoundedShape<P> extends Drawable {

	protected IPointList<P> pointList;

	@Override
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.setupSolidRender();
	}

	protected abstract void addPoints(IPointListBuilder<P> builder);

	protected abstract IPointListBuilder<P> createBuilder();

	@Override
	public void onUpdate() {
		updateBoundingBox();
	}

	protected final void updateBoundingBox() {
		final IPointListBuilder<P> builder = createBuilder();

		addPoints(builder);

		this.pointList = builder.build();
		setBoundingBox(pointList.getBoundingBox());
	}

}
