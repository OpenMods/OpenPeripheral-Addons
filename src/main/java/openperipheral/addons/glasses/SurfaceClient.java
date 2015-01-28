package openperipheral.addons.glasses;

import java.util.Iterator;

import openmods.structured.IStructureContainerFactory;
import openmods.structured.IStructureElement;
import openmods.structured.StructuredDataSlave;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;

public class SurfaceClient extends StructuredDataSlave<Drawable, IStructureElement> implements Iterable<Drawable> {

	private static final IStructureContainerFactory<Drawable> FACTORY = new IStructureContainerFactory<Drawable>() {

		@Override
		public Drawable createContainer(int containerId, int type) {
			return Drawable.createFromTypeId(containerId, type);
		}
	};

	public final long terminalId;
	public final boolean isPrivate;

	public SurfaceClient(long terminalId, boolean isPrivate) {
		super(FACTORY);
		this.terminalId = terminalId;
		this.isPrivate = isPrivate;
	}

	@Override
	protected void onConsistencyCheckFail() {
		new TerminalResetEvent(terminalId, isPrivate).sendToServer();
	}

	@Override
	public Iterator<Drawable> iterator() {
		return containers.values().iterator();
	}

}
