package openperipheral.addons.glasses.drawable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openmods.geometry.Box2d;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_icon")
public class ItemIcon extends Drawable {
	private static final int BASE_HEIGHT = 16;

	private static final int BASE_WIDTH = 16;

	private ItemStack dummyStack;

	@Property
	@StructureField
	public float x;

	@Property
	@StructureField
	public float y;

	@SideOnly(Side.CLIENT)
	private RenderItem renderItem;

	private ItemStack drawStack;

	@Property
	@StructureField
	public float scale = 1;

	@Property
	@StructureField
	public String itemId;

	@Property
	@StructureField
	public int meta;

	@Property
	@StructureField
	public float damageBar;

	@Property
	@StructureField
	public String label;

	ItemIcon() {}

	public ItemIcon(float x, float y, String itemId, int meta) {
		this.x = x;
		this.y = y;
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
	protected void drawContents(RenderState renderState, float partialTicks) {
		renderState.enableTexture();
		renderState.enableDepthTest();
		renderState.disableLight();
		renderState.enableCullFace();
		renderState.disableBlending();
		renderState.enableAlphaTest();
		renderState.setColor(0xFFFFFF, 1.0f);

		GL11.glScalef(scale, scale, scale);
		final RenderItem renderItem = getRenderItem();
		final Minecraft minecraft = Minecraft.getMinecraft();
		final TextureManager textureManager = minecraft.getTextureManager();

		renderItem.renderItemAndEffectIntoGUI(minecraft.fontRenderer, textureManager, drawStack, 0, 0);

		if (damageBar > 0 || !Strings.isNullOrEmpty(label)) {
			renderItem.renderItemOverlayIntoGUI(minecraft.fontRenderer, textureManager, dummyStack, 0, 0, label);
		}
	}

	@Override
	public Type getTypeEnum() {
		return Type.ITEM;
	}

	@Override
	public boolean isVisible() {
		return drawStack != null;
	}

	@Override
	public void onUpdate() {
		this.drawStack = findDrawStack(itemId, meta);

		int damage = (int)(damageBar * Items.diamond_hoe.getMaxDamage());
		this.dummyStack = new ItemStack(Items.diamond_hoe, 1, damage);

		final int width = Math.round(BASE_WIDTH * scale);
		final int height = Math.round(BASE_HEIGHT * scale);
		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));
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