package openperipheral.addons.glasses.drawable;

import openmods.geometry.Box2d;
import openperipheral.addons.glasses.utils.IPointList;
import openperipheral.addons.glasses.utils.IPointListBuilder;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;

@AdapterSourceName("glasses_shape")
public abstract class BoundedShape<P> extends Drawable {

	protected IPointList<P> pointList;

	@Override
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.setupSolidRender();
	}

	protected abstract void addPoints(IPointListBuilder<P> builder);

	protected abstract IPointListBuilder<P> createBuilder();

	@ScriptStruct
	public static class SimpleBox {

		public SimpleBox() {}

		public SimpleBox(Box2d box) {
			this.top = box.top;
			this.left = box.left;
			this.width = box.width;
			this.height = box.height;
		}

		@StructField
		public float top;

		@StructField
		public float left;

		@StructField
		public float width;

		@StructField
		public float height;

	}

	@ScriptCallable(returnTypes = ReturnType.TABLE, name = "getBoundingBox", description = "Get position of top left corner and dimensions")
	public SimpleBox getBox() {
		final Box2d bb = createAndFillBuilder().buildBoundingBox();
		return new SimpleBox(bb);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		updateBoundingBox();
	}

	private IPointListBuilder<P> createAndFillBuilder() {
		final IPointListBuilder<P> builder = createBuilder();
		addPoints(builder);
		return builder;
	}

	protected final void updateBoundingBox() {
		final IPointListBuilder<P> builder = createAndFillBuilder();
		this.pointList = builder.buildPointList();
		setBoundingBox(builder.buildBoundingBox());
	}

}
