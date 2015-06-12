package openperipheral.addons.glasses.drawable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Items;
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
	private ItemStack dummyStack;

	@SideOnly(Side.CLIENT)
	private RenderItem renderItem;

	private ItemStack drawStack;

	@CallbackProperty
	public float scale = 1;

	@CallbackProperty
	public String itemId;

	@CallbackProperty
	public int meta;

	@CallbackProperty
	public float damageBar;

	@CallbackProperty
	public String label;

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
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glScalef(scale, scale, scale);
			final RenderItem renderItem = getRenderItem();
			final Minecraft minecraft = Minecraft.getMinecraft();
			final TextureManager textureManager = minecraft.getTextureManager();

			renderItem.renderItemAndEffectIntoGUI(minecraft.fontRenderer, textureManager, drawStack, 0, 0);

			if (damageBar > 0 || !Strings.isNullOrEmpty(label)) {
				renderItem.renderItemOverlayIntoGUI(minecraft.fontRenderer, textureManager, dummyStack, 0, 0, label);
			}

			Drawable.setupFlatRenderState();
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
		this.drawStack = findDrawStack(itemId, meta);

		int damage = (int)(damageBar * Items.diamond_hoe.getMaxDamage());
		this.dummyStack = new ItemStack(Items.diamond_hoe, 1, damage);
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