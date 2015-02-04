package openperipheral.addons.glasses.drawable;

import net.minecraft.client.gui.FontRenderer;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_text")
public class Text extends Drawable {
	@CallbackProperty
	public String text;

	@CallbackProperty
	public int color;

	@CallbackProperty
	public float alpha = 1;

	@CallbackProperty
	public float scale = 1;

	Text() {}

	public Text(short x, short y, String text, int color) {
		super(x, y);
		this.text = text;
		this.color = color;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(float partialTicks) {
		FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
		GL11.glScalef(scale, scale, scale);
		fontRenderer.drawString(text, 0, 0, ((int)(alpha * 255) << 24 | color));
	}

	@Override
	public Type getTypeEnum() {
		return Type.TEXT;
	}

	@Override
	public int getWidth() {
		FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
		return Math.round(fontRenderer.getStringWidth(text) * scale);
	}

	@Override
	public int getHeight() {
		return Math.round(8 * scale);
	}

	@Override
	public boolean isVisible() {
		return alpha > 0;
	}

}