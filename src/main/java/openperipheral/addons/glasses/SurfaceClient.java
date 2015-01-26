package openperipheral.addons.glasses;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import openmods.structured.*;
import openperipheral.addons.glasses.SurfaceClient.DrawableWrapper;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;
import openperipheral.api.CallbackProperty;

import com.google.common.collect.Lists;

public class SurfaceClient extends StructuredDataSlave<DrawableWrapper, ElementField> implements Iterable<Drawable> {

	public static class DrawableWrapper implements IStructureContainer<ElementField> {
		public int containerId;
		public final Drawable target;

		public DrawableWrapper(Drawable target) {
			this.target = target;
		}

		@Override
		public int getType() {
			return target.getType();
		}

		@Override
		public List<ElementField> createElements() {
			List<ElementField> result = Lists.newArrayList();
			for (Field field : target.getClass().getFields()) {
				field.setAccessible(true);
				if (!field.isAnnotationPresent(CallbackProperty.class)) continue;
				result.add(new ElementField(target, field));
			}

			return result;
		}

		@Override
		public void onElementAdded(ElementField element, int index) {
			element.elementId = index;
		}
	}

	private static final IStructureContainerFactory<DrawableWrapper> FACTORY = new IStructureContainerFactory<DrawableWrapper>() {

		@Override
		public DrawableWrapper createContainer(int containerId, int type) {
			Drawable newDrawable = Drawable.createFromTypeId(containerId, type);
			return new DrawableWrapper(newDrawable);
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
		final Iterator<DrawableWrapper> it = containers.values().iterator();

		return new Iterator<Drawable>() {
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Drawable next() {
				return it.next().target;
			}

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
		};
	}

}
