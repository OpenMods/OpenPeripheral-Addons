package openperipheral.addons.glasses.server;

import openmods.include.IncludeInterface;
import openperipheral.addons.glasses.IContainer;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
public class SurfaceServer {

	@IncludeInterface(IContainer.class)
	public final DrawableContainerMaster drawablesContainer;

	@IncludeInterface(IDrawableFactory.class)
	public final IDrawableFactory drawablesFactory;

	private SurfaceServer(DrawableContainerMaster drawablesContainer) {
		this.drawablesContainer = drawablesContainer;
		this.drawablesFactory = new DrawableFactory(this.drawablesContainer);
	}

	public static SurfaceServer createPublicSurface(long guid) {
		return new SurfaceServer(new DrawableContainerMaster.Public(guid));
	}

	public static SurfaceServer createPrivateSurface(long guid) {
		return new SurfaceServer(new DrawableContainerMaster.Private(guid));
	}
}