package openperipheral.addons.glasses;

import java.util.List;
import java.util.Map;

import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaObject;

public class AdapterGlassesBridge implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityGlassesBridge.class;
	}

	@LuaMethod(returnType = LuaType.VOID, onTick = false, description = "Clear all the objects from the screen")
	public void clear(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		bridge.globalSurface.removeAll();
	}

	@LuaMethod(
			returnType = LuaType.OBJECT, onTick = false, description = "Add a new text object to the screen",
			args = {
					@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER),
					@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
					@Arg(name = "text", description = "The text to display", type = LuaType.STRING),
					@Arg(name = "color", description = "The text color", type = LuaType.NUMBER) })
	public ILuaObject addText(IComputerAccess computer, TileEntityGlassesBridge bridge, short x, short y, String text, int color) {
		return bridge.globalSurface.addDrawable(new Drawable.Text(x, y, text, color));
	}

	@LuaMethod(
			returnType = LuaType.OBJECT, onTick = false, description = "Add a new box to the screen",
			args = {
					@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER), @Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
					@Arg(name = "width", description = "The width of the box", type = LuaType.NUMBER),
					@Arg(name = "height", description = "The height of the box", type = LuaType.NUMBER),
					@Arg(name = "color", description = "The color of the box", type = LuaType.NUMBER),
					@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)", type = LuaType.NUMBER) })
	public ILuaObject addBox(IComputerAccess computer, TileEntityGlassesBridge bridge, short x, short y, short width, short height, int color, float opacity) {
		return bridge.globalSurface.addDrawable(new Drawable.SolidBox(x, y, width, height, color, opacity));
	}

	@LuaMethod(
			returnType = LuaType.OBJECT, onTick = false, description = "Add a new gradient box to the screen",
			args = {
					@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER),
					@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
					@Arg(name = "width", description = "The width of the box", type = LuaType.NUMBER),
					@Arg(name = "height", description = "The height of the box", type = LuaType.NUMBER),
					@Arg(name = "color", description = "The color of the box", type = LuaType.NUMBER),
					@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)", type = LuaType.NUMBER),
					@Arg(name = "color", description = "The color of the other side of the box", type = LuaType.NUMBER),
					@Arg(name = "opacity", description = "The opacity of the other side of the box (from 0 to 1)", type = LuaType.NUMBER),
					@Arg(name = "gradient", description = "The gradient direction (1 for horizontal, 2 for vertical)", type = LuaType.NUMBER) })
	public ILuaObject addGradientBox(IComputerAccess computer, TileEntityGlassesBridge bridge, short x, short y, short width, short height, int color, float alpha, int color2, float alpha2, int gradient)
			throws InterruptedException {
		return bridge.globalSurface.addDrawable(new Drawable.GradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient));
	}

	@LuaMethod(
			returnType = LuaType.OBJECT, onTick = false, description = "Add an icon of an item to the screen",
			args = {
					@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER),
					@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
					@Arg(name = "id", description = "The id of the item to draw", type = LuaType.NUMBER),
					@Arg(name = "meta", description = "The meta of the item to draw", type = LuaType.NUMBER)
			})
	public ILuaObject addIcon(IComputerAccess computer, TileEntityGlassesBridge bridge, short x, short y, short id, short meta) {
		return bridge.globalSurface.addDrawable(new Drawable.ItemIcon(x, y, id, meta));
	}

	@LuaMethod(
			returnType = LuaType.OBJECT, onTick = false, description = "Add a box textured like a liquid to the screen",
			args = {
					@Arg(name = "x", description = "The x position from the top left", type = LuaType.NUMBER),
					@Arg(name = "y", description = "The y position from the top left", type = LuaType.NUMBER),
					@Arg(name = "width", description = "The width of the liquid box", type = LuaType.NUMBER),
					@Arg(name = "height", description = "The height of the liquid box", type = LuaType.NUMBER),
					@Arg(name = "string", description = "The name of the fluid to render", type = LuaType.STRING)
			})
	public ILuaObject addLiquid(IComputerAccess computer, TileEntityGlassesBridge bridge, short x, short y, short width, short height, String id) {
		return bridge.globalSurface.addDrawable(new Drawable.LiquidIcon(x, y, width, height, id));
	}

	@LuaMethod(
			returnType = LuaType.TABLE, onTick = false, description = "Get the Ids of all the objects on the screen")
	public Integer[] getAllIds(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.globalSurface.getAllIds();
	}

	@LuaMethod(
			returnType = LuaType.TABLE, onTick = false, description = "Get all objects on the screen")
	public Map<Integer, ILuaObject> getAllObjects(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.globalSurface.getAllObjects();
	}

	@LuaMethod(returnType = LuaType.TABLE, onTick = false, description = "Get the names of all the users linked up to this bridge")
	public List<String> getUsers(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return bridge.getUsers();
	}

	@LuaMethod(returnType = LuaType.STRING, onTick = false, description = "Get the Guid of this bridge")
	public String getGuid(IComputerAccess computer, TileEntityGlassesBridge bridge) {
		return TerminalUtils.formatTerminalId(bridge.getGuid());
	}

	@LuaMethod(
			returnType = LuaType.NUMBER, onTick = false, description = "Get the display width of some text",
			args = {
					@Arg(name = "text", description = "The text you want to measure", type = LuaType.STRING) })
	public int getStringWidth(IComputerAccess computer, TileEntityGlassesBridge bridge, String text) {
		return GlassesRenderingUtils.getStringWidth(text);
	}

	@LuaMethod(
			returnType = LuaType.OBJECT, onTick = false, description = "Get the surface of a user to draw privately on their screen",
			args = {
					@Arg(name = "username", description = "The username of the user to get the draw surface for", type = LuaType.STRING)
			})
	public ILuaObject getUserSurface(IComputerAccess computer, TileEntityGlassesBridge bridge, String username) {
		return bridge.getSurface(username);
	}

}
