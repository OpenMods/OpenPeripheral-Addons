package openperipheral.addons.glasses.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import openmods.Log;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallback;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

public abstract class PolygonBuilderBase<P, D> implements IPolygonBuilder<P> {

	private static final IRenderCommand NULL_COMMAND = new IRenderCommand() {
		@Override
		public void execute(RenderState renderState) {}
	};

	private static final IRenderCommand RENDER_END_COMMAND = new IRenderCommand() {
		@Override
		public void execute(RenderState renderState) {
			GL11.glEnd();
		}
	};

	private final GLUtessellator tesselator = GLU.gluNewTess();

	private final List<IRenderCommand> commands = Lists.newArrayList();

	private boolean failed = false;

	private boolean started;

	public static class CombineData<D> {
		public final D object;
		public final float weight;

		public CombineData(D object, float weight) {
			this.object = object;
			this.weight = weight;
		}
	}

	private GLUtessellatorCallback collector = new GLUtessellatorCallbackAdapter() {

		@Override
		public void begin(final int type) {
			commands.add(createRenderBeginCommand(type));
		}

		@Override
		public void end() {
			commands.add(RENDER_END_COMMAND);
		}

		@Override
		public void vertex(Object vertexData) {
			@SuppressWarnings("unchecked")
			final D data = (D)vertexData;
			commands.add(createVertexCommand(data));
		}

		@Override
		public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
			List<CombineData<D>> objects = Lists.newArrayList();

			for (int i = 0; i < data.length; i++) {
				@SuppressWarnings("unchecked")
				D element = (D)data[i];
				if (element != null) objects.add(new CombineData<D>(element, weight[i]));
			}
			outData[0] = onCombine(coords, objects);
		}

		@Override
		public void error(int errorCode) {
			Log.debug("Failed to create polygon, cause %d=%s", errorCode, GLU.gluErrorString(errorCode));
			failed = true;
		}

	};

	protected abstract IRenderCommand createVertexCommand(D vertexData);

	protected abstract D onCombine(double[] coords, List<CombineData<D>> objects);

	public PolygonBuilderBase() {
		tesselator.gluBeginPolygon();
		tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);

		started = true;

		tesselator.gluTessCallback(GLU.GLU_TESS_BEGIN, collector);
		tesselator.gluTessCallback(GLU.GLU_TESS_END, collector);
		tesselator.gluTessCallback(GLU.GLU_TESS_VERTEX, collector);
		tesselator.gluTessCallback(GLU.GLU_TESS_COMBINE, collector);
		tesselator.gluTessCallback(GLU.GLU_TESS_ERROR, collector);
	}

	@Override
	public void addPoint(P point) {
		if (!failed) {
			final double[] coords = createCoords(point);
			final D data = convertToData(point);

			tesselator.gluTessVertex(coords, 0, data);
		}
	}

	protected abstract double[] createCoords(P point);

	protected abstract D convertToData(P point);

	private static IRenderCommand createCompositeCommand(final List<IRenderCommand> commands) {
		return new IRenderCommand() {
			@Override
			public void execute(RenderState renderState) {
				for (IRenderCommand command : commands)
					command.execute(renderState);
			}
		};
	}

	private static IRenderCommand createRenderBeginCommand(final int type) {
		return new IRenderCommand() {
			@Override
			public void execute(RenderState renderState) {
				GL11.glBegin(type);
			}
		};
	}

	@Override
	public IRenderCommand build() {
		Preconditions.checkState(started, "Builder already finished");
		tesselator.gluEndPolygon();
		started = failed;

		return failed? NULL_COMMAND : createCompositeCommand(commands);
	}

}
