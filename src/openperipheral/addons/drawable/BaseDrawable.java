package openperipheral.addons.drawable;

import java.lang.ref.WeakReference;

import openperipheral.addons.utils.CCUtils;
import dan200.computer.api.ILuaContext;

public abstract class BaseDrawable implements IDrawable {

	protected String[] methodNames;

	private boolean deleted = false;
	protected byte zIndex = 0;

	private WeakReference<ISurface> surface;

	public BaseDrawable() {}

	public BaseDrawable(ISurface _bridge) {
		surface = new WeakReference<ISurface>(_bridge);
	}

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(ILuaContext context, int methodId, Object[] arguments) throws Exception {

		if (deleted) { return null; }

		final String methodName = methodNames[methodId];
		Object[] result = CCUtils.callSelfMethod(this, methodName, arguments);

		if (methodName.startsWith("set")) {
			if (surface.get() != null) {
				surface.get().markChanged(this, (Integer)result[0]);
				return new Object[] {};
			}
		}

		return result;
	}

	public void delete() {
		deleted = true;
		if (surface.get() != null) {
			surface.get().setDeleted(this);
			surface.clear();
		}
	}

}
