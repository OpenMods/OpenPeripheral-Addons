package openperipheral.addons.glasses.drawable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_icon")
public class ItemIcon extends Drawable {
	@SideOnly(Side.CLIENT)
	private RenderItem renderItem;

	private ItemStack drawStack;

	@CallbackProperty
	public float scale = 1;

	@CallbackProperty
	public String itemId;

	@CallbackProperty
	public int meta;

	ItemIcon() {}

	public ItemIcon(short x, short y, String itemId, int meta) {
		super(x, y);
		this.itemId = itemId;
		this.meta = meta;
	}

	@SideOnly(Side.CLIENT)
	private RenderItem getRenderItem() {
		if (renderItem == null) renderItem = new RenderItem();
		return renderItem;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(float partialTicks) {
		if (drawStack != null) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glScalef(scale, scale, scale);
			getRenderItem().renderItemAndEffectIntoGUI(null, Minecraft.getMinecraft().getTextureManager(), drawStack, 0, 0);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}

	@Override
	public Type getTypeEnum() {
		return Type.ITEM;
	}

	@Override
	public int getWidth() {
		return Math.round(16 * scale);
	}

	@Override
	public int getHeight() {
		return Math.round(16 * scale);
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	protected void onUpdate() {
		drawStack = findDrawStack(itemId, meta);
	}

	private static ItemStack findDrawStack(String itemId, int meta) {
		if (Strings.isNullOrEmpty(itemId)) return null;
		String[] itemSplit = itemId.split(":");
		if (itemSplit.length != 2) return null;

		Item item = GameRegistry.findItem(itemSplit[0], itemSplit[1]);
		if (item == null) return null;

		return new ItemStack(item, 1, meta);
	}

}