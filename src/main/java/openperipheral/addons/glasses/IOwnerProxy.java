package openperipheral.addons.glasses;

public interface IOwnerProxy {

	public static IOwnerProxy DUMMY = new IOwnerProxy() {
		@Override
		public void removeContainer(int containerId) {
			throw new UnsupportedOperationException("Internal error");
		}

		@Override
		public void markElementModified(int elementId) {
			throw new UnsupportedOperationException("Internal error");
		}
	};

	public void removeContainer(int containerId);

	public void markElementModified(int elementId);

}
