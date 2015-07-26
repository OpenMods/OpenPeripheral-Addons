package openperipheral.addons.glasses;

import java.util.*;

import openmods.structured.IStructureContainerFactory;
import openmods.structured.IStructureElement;
import openmods.structured.StructuredDataSlave;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;
import openperipheral.addons.glasses.drawable.Drawable;

import com.google.common.collect.Lists;

public class SurfaceClient extends StructuredDataSlave<Drawable, IStructureElement> {

	private static final IStructureContainerFactory<Drawable> FACTORY = new IStructureContainerFactory<Drawable>() {
		@Override
		public Drawable createContainer(int containerId, int type) {
			return Drawable.createFromTypeId(containerId, type);
		}
	};

	private static final Comparator<Drawable> COMPARATOR = new Comparator<Drawable>() {
		@Override
		public int compare(Drawable o1, Drawable o2) {
			return Integer.compare(o1.z, o2.z);
		}
	};

	public final long terminalId;
	public final boolean isPrivate;
	private final List<Drawable> sortedElements = Lists.newArrayList();

	public SurfaceClient(long terminalId, boolean isPrivate) {
		super(FACTORY);
		this.terminalId = terminalId;
		this.isPrivate = isPrivate;
	}

	@Override
	public void reset() {
		super.reset();
		this.sortedElements.clear();
	}

	@Override
	protected SortedSet<Integer> removeContainer(int containerId) {
		final SortedSet<Integer> result = super.removeContainer(containerId);
		this.sortedElements.clear();
		this.sortedElements.addAll(containers.values());
		return result;
	}

	@Override
	protected int addContainer(int containerId, Drawable container, int firstElementId) {
		final int result = super.addContainer(containerId, container, firstElementId);
		this.sortedElements.add(container);
		return result;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		Collections.sort(sortedElements, COMPARATOR);
	}

	@Override
	protected void onConsistencyCheckFail() {
		new TerminalResetEvent(terminalId, isPrivate).sendToServer();
	}

	public List<Drawable> getSortedDrawables() {
		return sortedElements;
	}

}
