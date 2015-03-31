package openperipheral.addons.glasses.drawable;

import net.minecraft.client.gui.FontRenderer;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

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

	private int width;

	private int height;

	Text() {}

	public Text(short x, short y, String text, int color) {
		super(x, y);
		this.text = text;
		this.color = color;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(float partialTicks) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
		GL11.glScalef(scale, scale, scale);
		fontRenderer.drawString(text, 0, 0, ((int)(alpha * 255) << 24 | color));
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public Type getTypeEnum() {
		return Type.TEXT;
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
		return alpha > 0;
	}

	@Override
	protected void onUpdate() {
		height = Math.round(8 * scale);

		if (Strings.isNullOrEmpty(text)) {
			width = 0;
		} else {
			FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
			width = Math.round(fontRenderer.getStringWidth(text) * scale);
		}
	}
}