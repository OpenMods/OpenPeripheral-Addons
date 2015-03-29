package openperipheral.addons.glasses;

import java.util.Map;
import java.util.Set;

import openperipheral.addons.glasses.drawable.Drawable;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.*;

@Asynchronous
@AdapterSourceName("glasses_container")
public interface IDrawableContainer {

	@Alias("getObjectById")
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get object by id")
	public Drawable getById(
			@Arg(name = "id", description = "Id of drawed object") int id
			);

	@ScriptCallable(description = "Clear all the objects from the screen")
	public void clear();

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get the Ids of all the objects on the screen")
	public Set<Integer> getAllIds();

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get all objects on the screen")
	public Map<Integer, Drawable> getAllObjects();

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a new text object to the screen")
	public Drawable addText(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "text", description = "The text to display") String text,
			@Optionals @Arg(name = "color", description = "The text color") Integer color
			);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a new box to the screen")
	public Drawable addBox(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "width", description = "The width of the box") short width,
			@Arg(name = "height", description = "The height of the box") short height,
			@Optionals @Arg(name = "color", description = "The color of the box") Integer color,
			@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)") Float opacity
			);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a new gradient box to the screen")
	public Drawable addGradientBox(
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

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add an icon of an item to the screen")
	public Drawable addIcon(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "id", description = "The id of the item to draw") String id,
			@Arg(name = "meta", description = "The meta of the item to draw") short meta
			);

	@Alias("addFluid")
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a box textured like a liquid to the screen")
	public Drawable addLiquid(
			@Arg(name = "x", description = "The x position from the top left") short x,
			@Arg(name = "y", description = "The y position from the top left") short y,
			@Arg(name = "width", description = "The width of the liquid box") short width,
			@Arg(name = "height", description = "The height of the liquid box") short height,
			@Arg(name = "string", description = "The name of the fluid to render") String id
			);
}