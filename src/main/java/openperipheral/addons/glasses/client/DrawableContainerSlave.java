package openperipheral.addons.glasses.client;

import openmods.structured.IStructureElement;
import openmods.structured.StructuredDataSlave;
import openperipheral.addons.glasses.TerminalEvent;
import openperipheral.addons.glasses.drawable.Drawable;

public abstract class DrawableContainerSlave extends StructuredDataSlave<Drawable, IStructureElement> {

	protected final long terminalId;

	public DrawableContainerSlave(long terminalId) {
		super(Drawable.FACTORY);
		this.terminalId = terminalId;
	}

	public static class Private extends DrawableContainerSlave {
		public Private(long terminalId) {
			super(terminalId);
		}

		@Override
		protected void onConsistencyCheckFail() {
			new TerminalEvent.PrivateDrawableReset(terminalId).sendToServer();
		}
	}

	public static class Public extends DrawableContainerSlave {
		public Public(long terminalId) {
			super(terminalId);
		}

		@Override
		protected void onConsistencyCheckFail() {
			new TerminalEvent.PublicDrawableReset(terminalId).sendToServer();
		}
	}
}
