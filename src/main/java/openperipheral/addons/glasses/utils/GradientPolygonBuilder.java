package openperipheral.addons.glasses.utils;

import java.util.List;

import org.lwjgl.opengl.GL11;

class PointData {
	public final double[] coords;
	public final int rgb;
	public final float opacity;

	public PointData(double[] coords, int rgb, float opacity) {
		this.coords = coords;
		this.rgb = rgb;
		this.opacity = opacity;
	}

}

public class GradientPolygonBuilder extends PolygonBuilderBase<ColorPoint2d, PointData> {

	private static IRenderCommand createVertexCommand(final double x, final double y, final int rgb, final float opacity) {
		return new IRenderCommand() {
			@Override
			public void execute(RenderState renderState) {
				renderState.setColor(rgb, opacity);
				GL11.glVertex2d(x, y);
			}
		};
	}

	@Override
	protected IRenderCommand createVertexCommand(PointData v) {
		return createVertexCommand(v.coords[0], v.coords[1], v.rgb, v.opacity);
	}

	@Override
	protected PointData onCombine(double[] coords, List<CombineData<PointData>> data) {
		float r = 0;
		float g = 0;
		float b = 0;
		float opacity = 0;

		for (CombineData<PointData> d : data) {
			final PointData p = d.object;
			final int pR = (p.rgb >> 16) & 0xFF;
			final int pG = (p.rgb >> 8) & 0xFF;
			final int pB = (p.rgb >> 0) & 0xFF;

			r += pR * d.weight;
			g += pG * d.weight;
			b += pB * d.weight;
			opacity += p.opacity * d.weight;
		}

		int rgb = (((int)r & 0xFF) << 16) +
				(((int)g & 0xFF) << 8) +
				(((int)b & 0xFF) << 0);

		return new PointData(coords, rgb, opacity);
	}

	@Override
	protected double[] createCoords(ColorPoint2d point) {
		return new double[] { point.x, point.y, 0 };
	}

	@Override
	protected PointData convertToData(ColorPoint2d point) {
		return new PointData(new double[] { point.x, point.y, 0 }, point.rgb, point.opacity);
	}

}
