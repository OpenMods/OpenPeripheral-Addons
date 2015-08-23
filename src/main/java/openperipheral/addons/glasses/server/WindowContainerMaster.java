package openperipheral.addons.glasses.server;

import java.util.Collections;

import openmods.structured.IStructureElement;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.TerminalEvent;
import openperipheral.addons.glasses.WindowData;

public abstract class WindowContainerMaster extends StructuredDataMaster<WindowData, IStructureElement> {

	protected final long guid;

	public final MappedContainerAdapter<WindowData> adapter;

	public WindowContainerMaster(long guid) {
		this.guid = guid;
		this.adapter = new MappedContainerAdapter<WindowData>(Collections.unmodifiableMap(containers));
	}

	public synchronized void clear() {
		for (WindowData window : containers.values())
			window.setDeleted();
		removeAll();
	}

	public TerminalEvent.Data createFullDataEvent() {
		TerminalEvent.Data result = createDataEvent();
		appendFullCommands(result.commands);
		return result;
	}

	public TerminalEvent.Data createUpdateDataEvent() {
		TerminalEvent.Data result = createDataEvent();
		appendUpdateCommands(result.commands);
		return result;
	}

	protected abstract TerminalEvent.Data createDataEvent();

	public static class Public extends WindowContainerMaster {

		public Public(long guid) {
			super(guid);
		}

		@Override
		protected TerminalEvent.Data createDataEvent() {
			return new TerminalEvent.PublicStructureData(guid);
		}
	}

	public static class Private extends WindowContainerMaster {

		public Private(long guid) {
			super(guid);
		}

		@Override
		protected TerminalEvent.Data createDataEvent() {
			return new TerminalEvent.PrivateStructureData(guid);
		}
	}
}
