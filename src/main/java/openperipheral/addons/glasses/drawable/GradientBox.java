package openperipheral.addons.glasses.drawable;

import net.minecraft.client.renderer.Tessellator;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_gradient")
public class GradientBox extends Drawable {
	@CallbackProperty
	public short width;

	@CallbackProperty
	public short height;

	@CallbackProperty
	public int color1;

	@CallbackProperty
	public float opacity1;

	@CallbackProperty
	public int color2;

	@CallbackProperty
	public float opacity2;

	@CallbackProperty
	public int gradient;

	GradientBox() {}

	public GradientBox(short x, short y, short width, short height, int color1, float opacity1, int color2, float opacity2, int gradient) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.color1 = color1;
		this.opacity1 = opacity1;
		// compat hack
		if (gradient == 0) {
			this.color2 = color1;
			this.opacity2 = opacity1;
		} else {
			this.color2 = color2;
			this.opacity2 = opacity2;
		}
		this.gradient = gradient;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(float partialTicks) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.instance;

		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(color1, (int)(opacity1 * 255));

		if (gradient == 1) {
			tessellator.addVertex(0, height, 0);
			tessellator.addVertex(width, height, 0);
		} else {
			tessellator.addVertex(width, height, 0);
			tessellator.addVertex(width, 0, 0);

		}

		tessellator.setColorRGBA_I(color2, (int)(opacity2 * 255));

		if (gradient == 1) {
			tessellator.addVertex(width, 0, 0);
			tessellator.addVertex(0, 0, 0);
		} else {
			tessellator.addVertex(0, 0, 0);
			tessellator.addVertex(0, height, 0);
		}

		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

	}

	@Override
	public Type getTypeEnum() {
		return Type.GRADIENT;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean isVisible() {
		return opacity1 > 0 && opacity2 > 0;
	}

	@Override
	protected void onUpdate() {}
}