package openperipheral.addons.drawable;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Map.Entry;

import openmods.utils.ByteUtils;
import openmods.utils.render.FontSizeChecker;
import openperipheral.addons.glasses.TerminalDataEvent;
import openperipheral.addons.glasses.TerminalManagerClient;
import openperipheral.addons.glasses.TileEntityGlassesBridge;
import openperipheral.addons.utils.CCUtils;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import dan200.computer.api.ILuaContext;
import dan200.computer.api.ILuaObject;

public class Surface implements ISurface, ILuaObject {

	public WeakReference<TileEntityGlassesBridge> parent;

	public Map<Short, IDrawable> drawables = Maps.newHashMap();
	public Map<Short, Short> changes = Maps.newHashMap();

	private short count = 1;

	/**
	 * This Surface belongs to the entire bridge, not just one user.
	 */
	public final boolean isGlobal;
	public final String playerName;

	public Surface(TileEntityGlassesBridge parent) {
		this.parent = new WeakReference<TileEntityGlassesBridge>(parent);
		this.isGlobal = true;
		this.playerName = "GLOBAL";
	}

	public Surface(TileEntityGlassesBridge parent, String username) {
		this.parent = new WeakReference<TileEntityGlassesBridge>(parent);
		this.isGlobal = false;
		this.playerName = username;
	}

	// Should probably export these as part of the interface.. maybe

	public boolean hasChanges() {
		return changes.size() > 0;
	}

	public void clearChanges() {
		changes.clear();
	}

	@Override
	public synchronized Short getKeyForDrawable(IDrawable d) {
		for (Entry<Short, IDrawable> entry : drawables.entrySet()) {
			if (entry.getValue().equals(d)) { return entry.getKey(); }
		}

		return -1;
	}

	@Override
	public synchronized void setDeleted(IDrawable d) {
		Short key = getKeyForDrawable(d);
		if (key != -1) {
			changes.put(key, (short)0);
			drawables.remove(key);
		}
	}

	@Override
	public synchronized void markChanged(IDrawable d, int slot) {
		if (slot == -1) { return; }

		Short key = getKeyForDrawable(d);
		if (key != -1) {
			Short current = changes.get(key);

			if (current == null) {
				current = 0;
			}
			current = ByteUtils.set(current, slot, true);
			current = ByteUtils.set(current, 0, true);
			changes.put(key, current);
		}
	}

	public synchronized TerminalDataEvent createFullEvent() {
		ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
		/*
		 * If this surface is not global, then it is private to this
		 * player
		 */
		byte flag = (byte)(TerminalManagerClient.CHANGE_FLAG | (isGlobal? 0 : TerminalManagerClient.PRIVATE_FLAG));

		outputStream.writeByte(flag);
		outputStream.writeShort((short)drawables.size());

		try {
			for (Entry<Short, IDrawable> entries : drawables.entrySet()) {
				Short drawableId = entries.getKey();
				writeDrawableToStream(outputStream, drawableId, Short.MAX_VALUE);
			}
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}

		TerminalDataEvent result = new TerminalDataEvent();
		result.payload = outputStream.toByteArray();
		return result;
	}

	public synchronized TerminalDataEvent createChangeEvent() {

		ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();

		/*
		 * If this surface is not global, then it is private to this
		 * player
		 */
		byte flag = (byte)(TerminalManagerClient.CHANGE_FLAG | (isGlobal? 0 : TerminalManagerClient.PRIVATE_FLAG));

		// send the 'change' flag
		outputStream.writeByte(flag);

		// write the amount of drawables that have changed
		outputStream.writeShort((short)changes.size());

		try {
			for (Entry<Short, Short> change : changes.entrySet()) {
				Short drawableId = change.getKey();
				Short changeMask = change.getValue();
				writeDrawableToStream(outputStream, drawableId, changeMask);

			}
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}

		changes.clear();

		TerminalDataEvent result = new TerminalDataEvent();
		result.payload = outputStream.toByteArray();
		return result;

	}

	@Override
	public ILuaObject addBox(int x, int y, int width, int height, int color, double alpha) {
		return addGradientBox(x, y, width, height, color, alpha, color, alpha, (byte)0);
	}

	@Override
	public synchronized ILuaObject addGradientBox(int x, int y, int width, int height, int color, double alpha, int color2, double alpha2, byte gradient) {
		drawables.put(count, new DrawableBox(this, x, y, width, height, color, alpha, color2, alpha2, gradient));
		changes.put(count, Short.MAX_VALUE);
		return drawables.get(count++);
	}

	@Override
	public synchronized ILuaObject addIcon(int x, int y, int id, int meta) {
		drawables.put(count, new DrawableIcon(this, x, y, id, meta));
		changes.put(count, Short.MAX_VALUE);
		return drawables.get(count++);
	}

	@Override
	public synchronized ILuaObject getById(int id) {
		return drawables.get((short)id);
	}

	@Override
	public synchronized ILuaObject addText(int x, int y, String text, int color) {
		drawables.put(count, new DrawableText(this, x, y, text, color));
		changes.put(count, Short.MAX_VALUE);
		return drawables.get(count++);
	}

	@Override
	public synchronized ILuaObject addLiquid(int x, int y, int width, int height, int id) {
		drawables.put(count, new DrawableLiquid(this, x, y, width, height, id));
		changes.put(count, Short.MAX_VALUE);
		return drawables.get(count++);
	}

	private void writeDrawableToStream(DataOutput outputStream, short drawableId, Short changeMask) throws IOException {

		// write the mask
		outputStream.writeShort(changeMask);

		// write the drawable Id
		outputStream.writeShort(drawableId);

		if (ByteUtils.get(changeMask, 0)) { // if its not deleted

			IDrawable drawable = drawables.get(drawableId);

			if (drawable instanceof DrawableText) {
				outputStream.writeByte((byte)0);
			} else if (drawable instanceof DrawableBox) {
				outputStream.writeByte((byte)1);
			} else if (drawable instanceof DrawableIcon) {
				outputStream.writeByte((byte)2);
			} else if (drawable instanceof DrawableLiquid) {
				outputStream.writeByte((byte)3);
			}

			// write the rest of the drawable object
			drawable.writeTo(outputStream, changeMask);
		}
	}

	public static int getStringWidth(String str) {
		try {
			return FontSizeChecker.getInstance().getStringWidth(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.length() * 8;
	}

	@Override
	public synchronized Short[] getAllIds() {
		return drawables.keySet().toArray(new Short[drawables.size()]);
	}

	@Override
	public synchronized void clear() {
		for (Short key : drawables.keySet()) {
			changes.put(key, (short)0);
		}
		drawables.clear();
	}

	public String getPlayerName() {
		return playerName;
	}

	private static String[] methodNames = new String[] { "getPlayerName", "clear", "getAllIds", "getById", "addBox", "addText", "addGradientBox", "addIcon", "addLiquid" };

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(ILuaContext context, int methodId, Object[] arguments) throws Exception {
		String methodName = methodNames[methodId];
		return CCUtils.callSelfMethod(this, methodName, arguments);
	}
}