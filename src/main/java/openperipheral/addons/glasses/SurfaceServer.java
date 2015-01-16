package openperipheral.addons.glasses;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import openmods.structured.ElementField;
import openmods.structured.IStructureContainer;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.SurfaceServer.DrawableWrapper;
import openperipheral.api.*;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dan200.computercraft.api.lua.ILuaObject;

@ObjectTypeId("glasses_surface")
public class SurfaceServer extends StructuredDataMaster<DrawableWrapper, ElementField> implements IDrawableContainer {

	public class DrawableWrapper implements IStructureContainer<ElementField> {
		public int containerId;
		public final Drawable target;
		public final ILuaObject luaWrapper;

		public final Map<Field, ElementField> fields = Maps.newHashMap();

		public DrawableWrapper(Drawable target) {
			this.target = target;
			target.wrapper = this;
			luaWrapper = ApiAccess.getApi(IAdapterFactory.class).wrapObject(target);
		}

		public void setField(Field field, Object value) {
			ElementField fieldWrapper = fields.get(field);
			Preconditions.checkNotNull(fieldWrapper, "LOGIC FAIL. BLAME MOD DEVS");
			markElementModified(fieldWrapper.elementId);
			fieldWrapper.set(value);
		}

		public Object getField(Field field) {
			ElementField fieldWrapper = fields.get(field);
			Preconditions.checkNotNull(fieldWrapper, "LOGIC FAIL. BLAME MOD DEVS");
			return fieldWrapper.get();
		}

		public void delete() {
			removeContainer(containerId);
		}

		public void clear() {
			target.wrapper = null;
		}

		@Override
		public int getType() {
			return target.getTypeId();
		}

		@Override
		public List<ElementField> createElements() {
			List<ElementField> result = Lists.newArrayList();
			for (Field field : target.getClass().getFields()) {
				field.setAccessible(true);
				if (!field.isAnnotationPresent(CallbackProperty.class)) continue;

				ElementField fieldWrapper = new ElementField(target, field);
				result.add(fieldWrapper);
				fields.put(field, fieldWrapper);
			}

			return result;
		}

		@Override
		public void onElementAdded(ElementField element, int index) {
			element.elementId = index;
		}
	}

	public SurfaceServer() {}

	@Override
	public synchronized ILuaObject getById(int id) {
		DrawableWrapper wrapper = containers.get(id - 1);
		return wrapper != null? wrapper.luaWrapper : null;
	}

	@Override
	public synchronized void clear() {
		for (DrawableWrapper wrapper : containers.values())
			wrapper.clear();
		removeAll();
	}

	@Override
	public synchronized Integer[] getAllIds() {
		return containers.keySet().toArray(new Integer[containers.size()]);
	}

	@Override
	public synchronized Map<Integer, ILuaObject> getAllObjects() {
		Map<Integer, ILuaObject> result = Maps.newHashMap();
		for (Map.Entry<Integer, DrawableWrapper> e : containers.entrySet())
			result.put(e.getKey(), e.getValue().luaWrapper);
		return result;
	}

	private synchronized ILuaObject addDrawable(Drawable drawable) {
		DrawableWrapper wrapper = new DrawableWrapper(drawable);
		int id = addContainer(wrapper);
		wrapper.containerId = id;
		return wrapper.luaWrapper;
	}

	@Override
	public ILuaObject addText(short x, short y, String text, Integer color) {
		return addDrawable(new Drawable.Text(x, y, text, Objects.firstNonNull(color, 0xFFFFFF)));
	}

	@Override
	public ILuaObject addBox(short x, short y, short width, short height, Integer color, Float opacity) {
		return addDrawable(new Drawable.SolidBox(x, y, width, height, Objects.firstNonNull(color, 0xFFFFFF), Objects.firstNonNull(opacity, 1.0f)));
	}

	@Override
	public ILuaObject addGradientBox(short x, short y, short width, short height, int color, float alpha, int color2, float alpha2, int gradient) {
		return addDrawable(new Drawable.GradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient));
	}

	@Override
	public ILuaObject addIcon(short x, short y, String id, short meta) {
		return addDrawable(new Drawable.ItemIcon(x, y, id, meta));
	}

	@Override
	public ILuaObject addLiquid(short x, short y, short width, short height, String id) {
		return addDrawable(new Drawable.LiquidIcon(x, y, width, height, id));
	}

}