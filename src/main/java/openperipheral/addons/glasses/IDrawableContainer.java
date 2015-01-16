package openperipheral.addons.glasses;

import java.util.Map;

import openperipheral.api.*;
import dan200.computercraft.api.lua.ILuaObject;

@Asynchronous
@PeripheralTypeId("drawable_container")
public interface IDrawableContainer {

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Get object by id")
	public ILuaObject getById(
			@Arg(name = "id", description = "Id of drawed object") int id
			);

	@LuaCallable(description = "Clear all the objects from the screen")
	public void clear();

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get the Ids of all the objects on the screen")
	public Integer[] getAllIds();

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get all objects on the screen")
	public Map<Integer, ILuaObject> getAllObjects();

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add a new text object to the screen")
	public ILuaObject addText(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "text", description = "The text to display") String text,
			@Optionals @Arg(name = "color", description = "The text color") Integer color
			);

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add a new box to the screen")
	public ILuaObject addBox(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "width", description = "The width of the box") short width,
			@Arg(name = "height", description = "The height of the box") short height,
			@Optionals @Arg(name = "color", description = "The color of the box") Integer color,
			@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)") Float opacity
			);

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
			);

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add an icon of an item to the screen")
	public ILuaObject addIcon(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "id", description = "The id of the item to draw") String id,
			@Arg(name = "meta", description = "The meta of the item to draw") short meta
			);

	@LuaCallable(returnTypes = LuaReturnType.OBJECT, description = "Add a box textured like a liquid to the screen")
	public ILuaObject addLiquid(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "width", description = "The width of the liquid box") short width,
			@Arg(name = "height", description = "The height of the liquid box") short height,
			@Arg(name = "string", description = "The name of the fluid to render") String id
			);
}