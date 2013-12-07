package openperipheral.addons.glasses;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import openmods.structured.ElementField;
import openmods.structured.IStructureContainer;
import openmods.structured.StructuredDataMaster;
import openperipheral.addons.glasses.Drawable.GradientBox;
import openperipheral.addons.glasses.Drawable.ItemIcon;
import openperipheral.addons.glasses.Drawable.LiquidIcon;
import openperipheral.addons.glasses.Drawable.SolidBox;
import openperipheral.addons.glasses.Drawable.Text;
import openperipheral.addons.glasses.SurfaceServer.DrawableWrapper;
import openperipheral.addons.utils.CCUtils;
import openperipheral.util.LuaObjectBuilder;
import openperipheral.util.LuaObjectBuilder.IAccessCallback;
import openperipheral.util.Property;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dan200.computer.api.ILuaContext;
import dan200.computer.api.ILuaObject;

public class SurfaceServer extends StructuredDataMaster<DrawableWrapper, ElementField> implements ILuaObject {

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

	public synchronized Integer[] getAllIds() {
		return containers.keySet().toArray(new Integer[containers.size()]);
	}

	public synchronized Map<Integer, ILuaObject> getAllObjects() {
		Map<Integer, ILuaObject> result = Maps.newHashMap();
		for (Map.Entry<Integer, DrawableWrapper> e : containers.entrySet())
			result.put(e.getKey(), e.getValue().luaWrapper);
		return result;
	}

	public synchronized ILuaObject addDrawable(Drawable drawable) {
		DrawableWrapper wrapper = new DrawableWrapper(drawable);
		wrapper.containerId = addContainer(wrapper);
		return wrapper.luaWrapper;
	}

	private static String[] methodNames = new String[] { "getPlayerName", "clear", "getAllIds", "getAllObjects", "getById", "addBox", "addText", "addGradientBox", "addIcon", "addLiquid" };

	private final static Map<String, Class<? extends Drawable>> DRAWABLE_METHODS = ImmutableMap.<String, Class<? extends Drawable>> builder()
			.put("addGradientBox", GradientBox.class)
			.put("addBox", SolidBox.class)
			.put("addText", Text.class)
			.put("addIcon", ItemIcon.class)
			.put("addLiquid", LiquidIcon.class)
			.build();

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(ILuaContext context, int methodId, Object[] arguments) throws Exception {
		String methodName = methodNames[methodId];
		Class<? extends Drawable> drawable = DRAWABLE_METHODS.get(methodName);
		if (drawable != null) {
			Drawable newDrawable = CCUtils.callConstructor(drawable, arguments);
			return CCUtils.wrap(addDrawable(newDrawable));
		} else {
			return CCUtils.wrap(CCUtils.callSelfMethod(this, methodName, arguments));
		}
	}
}