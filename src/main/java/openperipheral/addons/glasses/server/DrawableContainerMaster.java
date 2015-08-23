package openperipheral.addons.glasses.server;

import java.util.Collections;

import openmods.structured.IStructureElement;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.TerminalEvent;
import openperipheral.addons.glasses.drawable.Drawable;

public abstract class DrawableContainerMaster extends StructuredDataMaster<Drawable, IStructureElement> {

	protected final long guid;

	public final MappedContainerAdapter<Drawable> adapter;

	public DrawableContainerMaster(long guid) {
		this.guid = guid;
		this.adapter = new MappedContainerAdapter<Drawable>(Collections.unmodifiableMap(containers));
	}

	public synchronized void clear() {
		for (Drawable drawable : containers.values())
			drawable.setDeleted();
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

	public abstract TerminalEvent.Clear createClearPacket();

	public static class Public extends DrawableContainerMaster {

		public Public(long guid) {
			super(guid);
		}

		@Override
		protected TerminalEvent.Data createDataEvent() {
			return new TerminalEvent.PublicDrawableData(guid);
		}

		@Override
		public TerminalEvent.Clear createClearPacket() {
			return new TerminalEvent.PublicClear(guid);
		}
	}

	public static class Private extends DrawableContainerMaster {

		public Private(long guid) {
			super(guid);
		}

		@Override
		protected TerminalEvent.Data createDataEvent() {
			return new TerminalEvent.PrivateDrawableData(guid);
		}

		@Override
		public TerminalEvent.Clear createClearPacket() {
			return new TerminalEvent.PrivateClear(guid);
		}
	}
}
