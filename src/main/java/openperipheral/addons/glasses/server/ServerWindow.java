package openperipheral.addons.glasses.server;

import openperipheral.addons.glasses.IMutableContainer;
import openperipheral.addons.glasses.drawable.Drawable;

public class ServerWindow implements IMutableContainer {

	private final DrawableContainerMaster drawables;
	
	
	
	public ServerWindow(DrawableContainerMaster drawables) {
		this.drawables = drawables;
	}



	@Override
	public Drawable addDrawable(Drawable drawable) {
		final int containerId = drawables.addContainer(drawable);
		
	}

}
