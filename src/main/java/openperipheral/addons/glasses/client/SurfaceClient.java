package openperipheral.addons.glasses.client;

import java.util.List;

import openperipheral.addons.glasses.drawable.Drawable;

public class SurfaceClient {

	public final DrawableContainerSlave drawablesContainer;

	public SurfaceClient(DrawableContainerSlave drawablesContainer) {
		this.drawablesContainer = drawablesContainer;
	}

	public static SurfaceClient createPublicSurface(long guid) {
		return new SurfaceClient(new DrawableContainerSlave.Public(guid));
	}

	public static SurfaceClient createPrivateSurface(long guid) {
		return new SurfaceClient(new DrawableContainerSlave.Private(guid));
	}

	public List<Drawable> getSortedDrawables() {
		throw new AbstractMethodError(); // TODO
	}

}
