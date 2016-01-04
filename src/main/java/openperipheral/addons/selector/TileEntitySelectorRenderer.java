package openperipheral.addons.selector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import openmods.geometry.Orientation;
import openmods.renderer.rotations.TransformProvider;
import openperipheral.addons.selector.TileEntitySelector.ItemSlot;

import org.lwjgl.opengl.GL11;

@SuppressWarnings("deprecation")
public class TileEntitySelectorRenderer extends TileEntitySpecialRenderer<TileEntitySelector> {

	@Override
	public void renderTileEntityAt(TileEntitySelector selector, double x, double y, double z, float partialTicks, int destroyState) {
		final Orientation orientation = selector.getOrientation();

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		TransformProvider.instance.multMatrix(orientation);
		GL11.glTranslated(-0.5, 0.501, -0.5); // 0.001 offset for 2d items in fast mode

		final int gridSize = selector.getGridSize();

		final RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
		for (ItemSlot slot : selector.getSlots(gridSize)) {
			ItemStack stack = selector.getSlot(slot.slot);
			if (stack != null) {
				EntityItem display = selector.getDisplayEntity();

				GL11.glPushMatrix();
				GL11.glTranslated(slot.x, 0, slot.y);
				GL11.glRotated(-90, 1, 0, 0);
				final double scale = slot.size * 2;
				GL11.glScaled(scale, scale, scale);
				display.setEntityItemStack(stack);

				RenderHelper.enableStandardItemLighting();
				renderer.renderItem(stack, ItemCameraTransforms.TransformType.NONE);
				RenderHelper.disableStandardItemLighting();

				GL11.glPopMatrix();
			}
		}

		GL11.glPopMatrix();
	}

}
