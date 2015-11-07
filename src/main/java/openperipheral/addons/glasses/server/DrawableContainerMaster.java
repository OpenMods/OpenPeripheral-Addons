package openperipheral.addons.glasses.server;

import java.util.Map;
import java.util.Set;

import openmods.structured.IStructureElement;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.IContainer;
import openperipheral.addons.glasses.IOwnerProxy;
import openperipheral.addons.glasses.TerminalEvent;
import openperipheral.addons.glasses.drawable.Drawable;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.helpers.Index;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class DrawableContainerMaster extends StructuredDataMaster<Drawable, IStructureElement> implements IContainer<Drawable> {

	protected final long guid;

	private final IOwnerProxy ownerProxy = new IOwnerProxy() {
		@Override
		public void removeContainer(int containerId) {
			DrawableContainerMaster.this.removeContainer(containerId);
		}

		@Override
		public void markElementModified(int elementId) {
			DrawableContainerMaster.this.markElementModified(elementId);
		}
	};

	public DrawableContainerMaster(long guid) {
		this.guid = guid;
	}

	@Override
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

	@Override
	public Drawable addObject(Drawable drawable) {
		final int containerId = addContainer(drawable);
		drawable.setId(containerId);
		drawable.setOwner(ownerProxy);
		return drawable;
	}

	@Override
	public Drawable getById(Index id) {
		return containers.get(id.value);
	}

	@Override
	public Set<Index> getAllIds(IArchitecture access) {
		final Set<Index> indices = Sets.newHashSet();
		for (int value : containers.keySet())
			indices.add(access.createIndex(value));

		return indices;
	}

	@Override
	public Map<Index, Drawable> getAllObjects(IArchitecture access) {
		final Map<Index, Drawable> result = Maps.newHashMap();
		for (Map.Entry<Integer, Drawable> e : containers.entrySet())
			result.put(access.createIndex(e.getKey()), e.getValue());

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
