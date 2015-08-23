package openperipheral.addons.glasses;

public enum SurfaceType {
	GLOBAL(Boolean.FALSE), PRIVATE(Boolean.TRUE);

	// legacy replacement for 'isPrivate'
	public final Object scriptValue;

	private SurfaceType(Object scriptValue) {
		this.scriptValue = scriptValue;
	}
}