package openperipheral.addons.glasses;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import openmods.structured.ElementField;
import openmods.structured.StructuredDataMaster;
import openperipheral.api.AdapterSourceName;
import openperipheral.api.LuaObject;

import com.google.common.base.Objects;

@LuaObject
@AdapterSourceName("glasses_surface")
public class SurfaceServer extends StructuredDataMaster<Drawable, ElementField> implements IDrawableContainer {

	public SurfaceServer() {}

	@Override
	public synchronized Drawable getById(int id) {
		return containers.get(id - 1);
	}

	@Override
	public synchronized void clear() {
		for (Drawable drawable : containers.values())
			drawable.setDeleted();
		removeAll();
	}

	@Override
	public synchronized Set<Integer> getAllIds() {
		return Collections.unmodifiableSet(containers.keySet());
	}

	@Override
	public synchronized Map<Integer, Drawable> getAllObjects() {
		return Collections.unmodifiableMap(containers);
	}

	private synchronized Drawable addDrawable(Drawable drawable) {
		int id = addContainer(drawable);
		drawable.onAdded(this, id);
		return drawable;
	}

	@Override
	public Drawable addText(short x, short y, String text, Integer color) {
		return addDrawable(new Drawable.Text(x, y, text, Objects.firstNonNull(color, 0xFFFFFF)));
	}

	@Override
	public Drawable addBox(short x, short y, short width, short height, Integer color, Float opacity) {
		return addDrawable(new Drawable.SolidBox(x, y, width, height, Objects.firstNonNull(color, 0xFFFFFF), Objects.firstNonNull(opacity, 1.0f)));
	}

	@Override
	public Drawable addGradientBox(short x, short y, short width, short height, int color, float alpha, int color2, float alpha2, int gradient) {
		return addDrawable(new Drawable.GradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient));
	}

	@Override
	public Drawable addIcon(short x, short y, String id, short meta) {
		return addDrawable(new Drawable.ItemIcon(x, y, id, meta));
	}

	@Override
	public Drawable addLiquid(short x, short y, short width, short height, String id) {
		return addDrawable(new Drawable.LiquidIcon(x, y, width, height, id));
	}

}