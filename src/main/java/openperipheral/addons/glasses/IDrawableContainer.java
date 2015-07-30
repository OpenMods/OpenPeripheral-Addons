package openperipheral.addons.glasses;

import java.util.Map;
import java.util.Set;

import openperipheral.addons.glasses.drawable.Drawable;
import openperipheral.addons.glasses.utils.ColorPoint2d;
import openperipheral.addons.glasses.utils.Point2d;
import openperipheral.api.Constants;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.*;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.helpers.Index;

@Asynchronous
@AdapterSourceName("glasses_container")
public interface IDrawableContainer {

	@Alias("getObjectById")
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get object by id")
	public Drawable getById(
			@Arg(name = "id", description = "Id of drawed object") Index id
			);

	@ScriptCallable(description = "Clear all the objects from the screen")
	public void clear();

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get the Ids of all the objects on the screen")
	public Set<Index> getAllIds(@Env(Constants.ARG_ARCHITECTURE) IArchitecture access);

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get all objects on the screen")
	public Map<Index, Drawable> getAllObjects(@Env(Constants.ARG_ARCHITECTURE) IArchitecture access);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a new text object to the screen")
	public Drawable addText(
			@Arg(name = "x", description = "The x position from the top left") float x,
			@Arg(name = "y", description = "The y position from the top left") float y,
			@Arg(name = "text", description = "The text to display") String text,
			@Optionals @Arg(name = "color", description = "The text color") Integer color
			);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a new box to the screen")
	public Drawable addBox(
			@Arg(name = "x", description = "The x position from the top left") float x,
			@Arg(name = "y", description = "The y position from the top left") float y,
			@Arg(name = "width", description = "The width of the box") float width,
			@Arg(name = "height", description = "The height of the box") float height,
			@Optionals @Arg(name = "color", description = "The color of the box") Integer color,
			@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)") Float opacity
			);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a new gradient box to the screen")
	public Drawable addGradientBox(
			@Arg(name = "x", description = "The x position from the top left") float x,
			@Arg(name = "y", description = "The y position from the top left") float y,
			@Arg(name = "width", description = "The width of the box") float width,
			@Arg(name = "height", description = "The height of the box") float height,
			@Arg(name = "color", description = "The color of the box") int color,
			@Arg(name = "opacity", description = "The opacity of the box (from 0 to 1)") float alpha,
			@Arg(name = "color2", description = "The color of the other side of the box") int color2,
			@Arg(name = "opacity2", description = "The opacity of the other side of the box (from 0 to 1)") float alpha2,
			@Arg(name = "gradient", description = "The gradient direction (1 for horizontal, 2 for vertical)") int gradient
			);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add an icon of an item to the screen")
	public Drawable addIcon(
			@Arg(name = "x", description = "The x position from the top left") float x,
			@Arg(name = "y", description = "The y position from the top left") float y,
			@Arg(name = "id", description = "The id of the item to draw") String id,
			@Optionals @Arg(name = "meta", description = "The meta of the item to draw") Short meta
			);

	@Alias("addFluid")
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a box textured like a liquid to the screen")
	public Drawable addLiquid(
			@Arg(name = "x", description = "The x position from the top left") float x,
			@Arg(name = "y", description = "The y position from the top left") float y,
			@Arg(name = "width", description = "The width of the liquid box") float width,
			@Arg(name = "height", description = "The height of the liquid box") float height,
			@Arg(name = "liquid", description = "The name of the fluid to render") String id
			);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a triangle")
	public Drawable addTriangle(
			@Arg(name = "p1", description = "Coordinates of first point") Point2d p1,
			@Arg(name = "p2", description = "Coordinates of second point") Point2d p2,
			@Arg(name = "p3", description = "Coordinates of third point") Point2d p3,
			@Optionals @Arg(name = "color", description = "The color of the line") Integer color,
			@Arg(name = "opacity", description = "The opacity of the line (from 0 to 1)") Float opacity);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a triangle")
	public Drawable addGradientTriangle(
			@Arg(name = "p1", description = "Coordinates of first point") ColorPoint2d p1,
			@Arg(name = "p2", description = "Coordinates of second point") ColorPoint2d p2,
			@Arg(name = "p3", description = "Coordinates of third point") ColorPoint2d p3);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a quad")
	public Drawable addQuad(
			@Arg(name = "p1", description = "Coordinates of first point") Point2d p1,
			@Arg(name = "p2", description = "Coordinates of second point") Point2d p2,
			@Arg(name = "p3", description = "Coordinates of third point") Point2d p3,
			@Arg(name = "p4", description = "Coordinates of fourth point") Point2d p4,
			@Optionals @Arg(name = "color", description = "The color of the line") Integer color,
			@Arg(name = "opacity", description = "The opacity of the line (from 0 to 1)") Float opacity);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a gradient quad")
	public Drawable addGradientQuad(
			@Arg(name = "p1", description = "Coordinates of first point") ColorPoint2d p1,
			@Arg(name = "p2", description = "Coordinates of second point") ColorPoint2d p2,
			@Arg(name = "p3", description = "Coordinates of third point") ColorPoint2d p3,
			@Arg(name = "p4", description = "Coordinates of fourth point") ColorPoint2d p4);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a line")
	public Drawable addLine(
			@Arg(name = "p1", description = "Coordinates of first point") Point2d p1,
			@Arg(name = "p2", description = "Coordinate of second point") Point2d p2,
			@Optionals @Arg(name = "color", description = "The color of the line") Integer color,
			@Arg(name = "opacity", description = "The opacity of the line (from 0 to 1)") Float opacity);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a gradient line")
	public Drawable addGradientLine(
			@Arg(name = "p1", description = "Coordinates of first point") ColorPoint2d p1,
			@Arg(name = "p2", description = "Coordinate of second point") ColorPoint2d p2);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a line strip")
	public Drawable addLineList(
			@Optionals @Arg(name = "color", description = "The color of the line") Integer color,
			@Arg(name = "opacity", description = "The opacity of the line (from 0 to 1)") Float opacity,
			@Arg(name = "points", description = "Coordinates of points") Point2d... points);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a gradient line strip")
	public Drawable addGradientLineList(
			@Arg(name = "points", description = "Coordinates of points") ColorPoint2d... points);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a concave polygon")
	public Drawable addPolygon(
			@Optionals @Arg(name = "color", description = "The color of the line") Integer color,
			@Arg(name = "opacity", description = "The opacity of the line (from 0 to 1)") Float opacity,
			@Arg(name = "points", description = "Coordinates of points") Point2d... points);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a gradient concave polygon")
	public Drawable addGradientPolygon(
			@Arg(name = "points", description = "Coordinates of points") ColorPoint2d... points);

	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Add a point")
	public Drawable addPoint(
			@Arg(name = "coord", description = "Coordinates of point") Point2d p,
			@Optionals @Arg(name = "color", description = "The color of the point") Integer color,
			@Arg(name = "opacity", description = "The opacity of the point (from 0 to 1)") Float opacity);
}