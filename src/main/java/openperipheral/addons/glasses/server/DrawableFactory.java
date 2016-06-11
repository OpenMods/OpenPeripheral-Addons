package openperipheral.addons.glasses.server;

import com.google.common.base.Objects;
import openperipheral.addons.glasses.IContainer;
import openperipheral.addons.glasses.drawable.Drawable;
import openperipheral.addons.glasses.drawable.GradientBox;
import openperipheral.addons.glasses.drawable.GradientLine;
import openperipheral.addons.glasses.drawable.GradientLineStrip;
import openperipheral.addons.glasses.drawable.GradientPolygon;
import openperipheral.addons.glasses.drawable.GradientQuad;
import openperipheral.addons.glasses.drawable.GradientTriangle;
import openperipheral.addons.glasses.drawable.ItemIcon;
import openperipheral.addons.glasses.drawable.LiquidIcon;
import openperipheral.addons.glasses.drawable.Point;
import openperipheral.addons.glasses.drawable.SolidBox;
import openperipheral.addons.glasses.drawable.SolidLine;
import openperipheral.addons.glasses.drawable.SolidLineStrip;
import openperipheral.addons.glasses.drawable.SolidPolygon;
import openperipheral.addons.glasses.drawable.SolidQuad;
import openperipheral.addons.glasses.drawable.SolidTriangle;
import openperipheral.addons.glasses.drawable.Text;
import openperipheral.addons.glasses.utils.ColorPoint2d;
import openperipheral.addons.glasses.utils.Point2d;

public class DrawableFactory implements IDrawableFactory {

	private final IContainer<Drawable> container;

	private static Integer defaultColor(Integer color) {
		return Objects.firstNonNull(color, 0xFFFFFF);
	}

	private static Float defaultOpacity(Float opacity) {
		return Objects.firstNonNull(opacity, 1.0f);
	}

	public DrawableFactory(IContainer<Drawable> container) {
		this.container = container;
	}

	@Override
	public synchronized Drawable addText(float x, float y, String text, Integer color) {
		return container.addObject(new Text(x, y, text, defaultColor(color)));
	}

	@Override
	public synchronized Drawable addBox(float x, float y, float width, float height, Integer color, Float opacity) {
		return container.addObject(new SolidBox(x, y, width, height, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public synchronized Drawable addGradientBox(float x, float y, float width, float height, int color, float alpha, int color2, float alpha2, int gradient) {
		return container.addObject(new GradientBox(x, y, width, height, color, alpha, color2, alpha2, gradient));
	}

	@Override
	public synchronized Drawable addIcon(float x, float y, String id, Short meta) {
		return container.addObject(new ItemIcon(x, y, id, meta != null? meta : 0));
	}

	@Override
	public synchronized Drawable addLiquid(float x, float y, float width, float height, String id) {
		return container.addObject(new LiquidIcon(x, y, width, height, id));
	}

	@Override
	public Drawable addTriangle(Point2d p1, Point2d p2, Point2d p3, Integer color, Float opacity) {
		return container.addObject(new SolidTriangle(p1, p2, p3, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addGradientTriangle(ColorPoint2d p1, ColorPoint2d p2, ColorPoint2d p3) {
		return container.addObject(new GradientTriangle(p1, p2, p3));
	}

	@Override
	public Drawable addQuad(Point2d p1, Point2d p2, Point2d p3, Point2d p4, Integer color, Float opacity) {
		return container.addObject(new SolidQuad(p1, p2, p3, p4, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addGradientQuad(ColorPoint2d p1, ColorPoint2d p2, ColorPoint2d p3, ColorPoint2d p4) {
		return container.addObject(new GradientQuad(p1, p2, p3, p4));
	}

	@Override
	public Drawable addLine(Point2d p1, Point2d p2, Integer color, Float opacity) {
		return container.addObject(new SolidLine(p1, p2, defaultColor(color), defaultOpacity(opacity)));
	}

	@Override
	public Drawable addGradientLine(ColorPoint2d p1, ColorPoint2d p2) {
		return container.addObject(new GradientLine(p1, p2));
	}

	@Override
	public Drawable addLineList(Integer color, Float opacity, Point2d... points) {
		return container.addObject(new SolidLineStrip(defaultColor(color), defaultOpacity(opacity), points));
	}

	@Override
	public Drawable addGradientLineList(ColorPoint2d... points) {
		return container.addObject(new GradientLineStrip(points));
	}

	@Override
	public Drawable addPolygon(Integer color, Float opacity, Point2d... points) {
		return container.addObject(new SolidPolygon(defaultColor(color), defaultOpacity(opacity), points));
	}

	@Override
	public Drawable addGradientPolygon(ColorPoint2d... points) {
		return container.addObject(new GradientPolygon(points));
	}

	@Override
	public Drawable addPoint(Point2d p, Integer color, Float opacity) {
		return container.addObject(new Point(p, defaultColor(color), defaultOpacity(opacity)));
	}

}
