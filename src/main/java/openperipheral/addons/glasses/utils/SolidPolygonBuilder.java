package openperipheral.addons.glasses.utils;

import java.util.List;

import org.lwjgl.opengl.GL11;

public class SolidPolygonBuilder extends PolygonBuilderBase<Point2d, double[]> {

	private static Runnable createVertexCommand(final double x, final double y) {
		return new Runnable() {
			@Override
			public void run() {
				GL11.glVertex2d(x, y);
			}
		};
	}

	@Override
	protected Runnable createVertexCommand(double[] vertexData) {
		return createVertexCommand(vertexData[0], vertexData[1]);
	}

	@Override
	protected double[] onCombine(double[] coords, List<CombineData<double[]>> data) {
		return coords;
	}

	@Override
	protected double[] createCoords(Point2d point) {
		return new double[] { point.x, point.y, 0 };
	}

	@Override
	protected double[] convertToData(Point2d point) {
		return new double[] { point.x, point.y, 0 };
	}

}
