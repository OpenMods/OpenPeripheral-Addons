package openperipheral.addons.glasses.drawable;

import net.minecraft.client.gui.FontRenderer;
import openmods.geometry.Box2d;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_text")
public class Text extends Drawable {
	@Property
	@StructureField
	public float x;

	@Property
	@StructureField
	public float y;

	@Property
	@StructureField
	public String text;

	@Property
	@StructureField
	public int color;

	@Property
	@StructureField
	public float alpha = 1;

	@Property
	@StructureField
	public float scale = 1;

	Text() {}

	public Text(float x, float y, String text, int color) {
		this.x = x;
		this.y = y;
		this.text = text;
		this.color = color;

		updateBoundingBox();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.enableTexture();
		renderState.setColor(color, alpha);
		FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
		GL11.glScalef(scale, scale, scale);
		fontRenderer.drawString(text, 0, 0, ((int)(alpha * 255) << 24 | color));
	}

	@Override
	public Type getTypeEnum() {
		return Type.TEXT;
	}

	@Override
	public boolean isVisible() {
		return alpha > 0;
	}

	@Override
	public void onUpdate() {
		updateBoundingBox();
	}

	private void updateBoundingBox() {
		final int height = Math.round(8 * scale);

		final int width;
		if (Strings.isNullOrEmpty(text)) {
			width = 0;
		} else {
			FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
			width = Math.round(fontRenderer.getStringWidth(text) * scale);
		}

		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));
	}
}