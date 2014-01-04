package openperipheral.addons.glasses;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import openmods.structured.ElementField;
import openmods.structured.IStructureContainer;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.SurfaceServer.DrawableWrapper;
import openperipheral.api.*;
import openperipheral.util.LuaObjectBuilder;
import openperipheral.util.LuaObjectBuilder.IAccessCallback;
import openperipheral.util.Property;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dan200.computer.api.ILuaObject;

@Freeform
public class SurfaceServer extends StructuredDataMaster<DrawableWrapper, ElementField> {

	public class DrawableWrapper implements IStructureContainer<ElementField>, IAccessCallback {
		public int containerId;
		public final Drawable target;
		public final ILuaObject luaWrapper;

		public final Map<Field, ElementField> fields = Maps.newHashMap();

		public DrawableWrapper(Drawable target) {
			this.target = target;
			luaWrapper = LuaObjectBuilder.build(target.getClass(), this);
		}

		@Override
		public void setField(Field field, Object value) {
			ElementField fieldWrapper = fields.get(field);
			Preconditions.checkNotNull(fieldWrapper, "LOGIC FAIL. BLAME MOD DEVS");
			markElementModified(fieldWrapper.elementId);
			fieldWrapper.set(value);
		}

		@Override
		public Object getField(Field field) {
			ElementField fieldWrapper = fields.get(field);
			Preconditions.checkNotNull(fieldWrapper, "LOGIC FAIL. BLAME MOD DEVS");
			return fieldWrapper.get();
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
				if (!field.isAnnotationPresent(Property.class)) continue;

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

	public final String playerName;

	public SurfaceServer(String playerName) {
		this.playerName = playerName;
	}

	public synchronized void setDeleted(DrawableWrapper d) {
		removeContainer(d.containerId);
	}

	public synchronized ILuaObject getById(int id) {
		return containers.get(id).luaWrapper;
	}

	@LuaCallable(description = "Clear all the objects from the screen")
	public synchronized void clear() {
		removeAll();
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get the Ids of all the objects on the screen")
	public synchronized Integer[] getAllIds() {
		return containers.keySet().toArray(new Integer[containers.size()]);
	}

	@LuaCallable(returnTypes = LuaType.TABLE, description = "Get all objects on the screen")
	public synchronized Map<Integer, ILuaObject> getAllObjects() {
		Map<Integer, ILuaObject> result = Maps.newHashMap();
		for (Map.Entry<Integer, DrawableWrapper> e : containers.entrySet())
			result.put(e.getKey(), e.getValue().luaWrapper);
		return result;
	}

	private synchronized ILuaObject addDrawable(Drawable drawable) {
		DrawableWrapper wrapper = new DrawableWrapper(drawable);
		wrapper.containerId = addContainer(wrapper);
		return wrapper.luaWrapper;
	}

	@LuaCallable(returnTypes = LuaType.OBJECT, description = "Add a new text object to the screen")
	public ILuaObject addText(
			@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER) short x,
			@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER) short y,
			@Arg(name = "text", description = "The text to display", type = LuaType.STRING) String text,
			@Arg(name = "color", description = "The text color", type = LuaType.NUMBER) int color
			) {
		return addDrawable(new Drawable.Text(x, y, text, color));
	}

	@LuaCallable(returnTypes = LuaType.OBJECT, description = "Add a new box to the screen")
	public ILuaObject addBox(
			@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER) short x,
			@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER) short y,
			@Arg(name = "width", description = "The width of the box", type = LuaType.NUMBER) short width,
			@Arg(name = "height", description = "The height of the box", type = LuaType.NUMBER) short height,
			@Arg(name = "color", description = "The color of the box", type = LuaType.NUMBER) int color,
			@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)", type = LuaType.NUMBER) float opacity
			) {
		return addDrawable(new Drawable.SolidBox(x, y, width, height, color, opacity));
	}

	@LuaCallable(returnTypes = LuaType.OBJECT, description = "Add a new gradient box to the screen")
	public ILuaObject addGradientBox(
			@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER) short x,
			@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER) short y,
			@Arg(name = "width", description = "The width of the box", type = LuaType.NUMBER) short width,
			@Arg(name = "height", description = "The height of the box", type = LuaType.NUMBER) short height,
			@Arg(name = "color", description = "The color of the box", type = LuaType.NUMBER) int color,
			@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)", type = LuaType.NUMBER) float alpha,
			@Arg(name = "color", description = "The color of the other side of the box", type = LuaType.NUMBER) int color2,
			@Arg(name = "opacity", description = "The opacity of the other side of the box (from 0 to 1)", type = LuaType.NUMBER) float alpha2,
			@Arg(name = "gradient", description = "The gradient direction (1 for horizontal, 2 for vertical)", type = LuaType.NUMBER) int gradient
			) {
		return addDrawable(new Drawable.GradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient));
	}

	@LuaCallable(returnTypes = LuaType.OBJECT, description = "Add an icon of an item to the screen")
	public ILuaObject addIcon(
			@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER) short x,
			@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER) short y,
			@Arg(name = "id", description = "The id of the item to draw", type = LuaType.NUMBER) short id,
			@Arg(name = "meta", description = "The meta of the item to draw", type = LuaType.NUMBER) short meta
			) {
		return addDrawable(new Drawable.ItemIcon(x, y, id, meta));
	}

	@LuaCallable(returnTypes = LuaType.OBJECT, description = "Add a box textured like a liquid to the screen")
	public ILuaObject addLiquid(
			@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER) short x,
			@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER) short y,
			@Arg(name = "width", description = "The width of the liquid box", type = LuaType.NUMBER) short width,
			@Arg(name = "height", description = "The height of the liquid box", type = LuaType.NUMBER) short height,
			@Arg(name = "string", description = "The name of the fluid to render", type = LuaType.STRING) String id
			) {
		return addDrawable(new Drawable.LiquidIcon(x, y, width, height, id));
	}

}