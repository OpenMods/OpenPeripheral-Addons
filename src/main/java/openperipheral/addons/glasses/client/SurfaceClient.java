package openperipheral.addons.glasses.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import openmods.structured.IStructureElement;
import openmods.structured.StructureObserver;
import openperipheral.addons.glasses.drawable.Drawable;

import com.google.common.collect.Lists;

public abstract class SurfaceClient {

	private static final Comparator<Drawable> COMPARATOR = new Comparator<Drawable>() {
		@Override
		public int compare(Drawable o1, Drawable o2) {
			return Integer.compare(o1.z, o2.z);
		}
	};

	private class SurfaceClientObserver extends StructureObserver<Drawable, IStructureElement> {
		private final List<Drawable> sortedElements = Lists.newArrayList();

		private final List<Drawable> sortedElementsView;

		public SurfaceClientObserver() {
			this.sortedElementsView = Collections.unmodifiableList(sortedElements);
		}

		@Override
		public void onContainerAdded(int containerId, Drawable container) {
			container.setId(containerId);
			sortedElements.add(container);
		}

		@Override
		public void onContainerRemoved(int containerId, Drawable container) {
			sortedElements.remove(container);
		}

		@Override
		public void onContainerUpdated(int containerId, Drawable container) {
			container.onUpdate();
		}

		@Override
		public void onStructureUpdate() {
			Collections.sort(sortedElements, COMPARATOR);
		}

		@Override
		public void onDataUpdate() {
			Collections.sort(sortedElements, COMPARATOR);
		}
	}

	public final DrawableContainerSlave drawablesContainer;

	private final SurfaceClientObserver surfaceObserver = new SurfaceClientObserver();

	private SurfaceClient(long guid) {
		this.drawablesContainer = createDrawableContainer(guid, surfaceObserver);

	}

	protected abstract DrawableContainerSlave createDrawableContainer(long guid, StructureObserver<Drawable, IStructureElement> observer);

	public static SurfaceClient createPublicSurface(long guid) {
		return new SurfaceClient(guid) {
			@Override
			protected DrawableContainerSlave createDrawableContainer(long guid, StructureObserver<Drawable, IStructureElement> observer) {
				return new DrawableContainerSlave.Public(guid, observer);
			}
		};
	}

	public static SurfaceClient createPrivateSurface(long guid) {
		return new SurfaceClient(guid) {
			@Override
			protected DrawableContainerSlave createDrawableContainer(long guid, StructureObserver<Drawable, IStructureElement> observer) {
				return new DrawableContainerSlave.Private(guid, observer);
			}
		};
	}

	public List<Drawable> getSortedDrawables() {
		return surfaceObserver.sortedElementsView;
	}
}
