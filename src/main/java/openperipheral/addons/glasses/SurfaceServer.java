package openperipheral.addons.glasses;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import openmods.structured.ElementField;
import openmods.structured.IStructureContainer;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.SurfaceServer.DrawableWrapper;
import openperipheral.api.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dan200.computercraft.api.lua.ILuaObject;

@Asynchronous
@ObjectTypeId("glasses_surface")
public class SurfaceServer extends StructuredDataMaster<DrawableWrapper, ElementField> {

	// I know this one is in guava, but I'm getting name class on Objects
	private static <T> T firstNonNull(T first, T second) {
		return first != null? first : second;
	}

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

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Get object by id")
	public synchronized ILuaObject getById(
			@Arg(name = "id", description = "Id of drawed object") int id
			) {
		DrawableWrapper wrapper = containers.get(id - 1);
		return wrapper != null? wrapper.luaWrapper : null;
	}

	@LuaCallable(description = "Clear all the objects from the screen")
	public synchronized void clear() {
		for (DrawableWrapper wrapper : containers.values())
			wrapper.clear();
		removeAll();
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get the Ids of all the objects on the screen")
	public synchronized Integer[] getAllIds() {
		return containers.keySet().toArray(new Integer[containers.size()]);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get all objects on the screen")
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

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add a new text object to the screen")
	public ILuaObject addText(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "text", description = "The text to display") String text,
			@Optionals @Arg(name = "color", description = "The text color") Integer color
			) {
		return addDrawable(new Drawable.Text(x, y, text, firstNonNull(color, 0xFFFFFF)));
	}

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add a new box to the screen")
	public ILuaObject addBox(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "width", description = "The width of the box") short width,
			@Arg(name = "height", description = "The height of the box") short height,
			@Optionals @Arg(name = "color", description = "The color of the box") Integer color,
			@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)") Float opacity
			) {
		return addDrawable(new Drawable.SolidBox(x, y, width, height, firstNonNull(color, 0xFFFFFF), firstNonNull(opacity, 1.0f)));
	}

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add a new gradient box to the screen")
	public ILuaObject addGradientBox(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "width", description = "The width of the box") short width,
			@Arg(name = "height", description = "The height of the box") short height,
			@Arg(name = "color", description = "The color of the box") int color,
			@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)") float alpha,
			@Arg(name = "color", description = "The color of the other side of the box") int color2,
			@Arg(name = "opacity", description = "The opacity of the other side of the box (from 0 to 1)") float alpha2,
			@Arg(name = "gradient", description = "The gradient direction (1 for horizontal, 2 for vertical)") int gradient
			) {
		return addDrawable(new Drawable.GradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient));
	}

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add an icon of an item to the screen")
	public ILuaObject addIcon(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "id", description = "The id of the item to draw") String id,
			@Arg(name = "meta", description = "The meta of the item to draw") short meta
			) {
		return addDrawable(new Drawable.ItemIcon(x, y, id, meta));
	}

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add a box textured like a liquid to the screen")
	public ILuaObject addLiquid(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "width", description = "The width of the liquid box") short width,
			@Arg(name = "height", description = "The height of the liquid box") short height,
			@Arg(name = "string", description = "The name of the fluid to render") String id
			) {
		return addDrawable(new Drawable.LiquidIcon(x, y, width, height, id));
	}

}