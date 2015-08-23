package openperipheral.addons.glasses;

import openmods.structured.StructuredDataMaster;

public interface IOwnerProxy {

	public static class Master implements IOwnerProxy {

		private final StructuredDataMaster<?, ?> master;

		public Master(StructuredDataMaster<?, ?> master) {
			this.master = master;
		}

		@Override
		public void removeContainer(int containerId) {
			master.removeContainer(containerId);
		}

		@Override
		public void markElementModified(int elementId) {
			master.markElementModified(elementId);
		}

	}

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
