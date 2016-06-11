package openperipheral.addons.selector;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import openmods.geometry.Orientation;
import openmods.renderer.rotations.TransformProvider;
import openperipheral.addons.selector.TileEntitySelector.ItemSlot;
import org.lwjgl.opengl.GL11;

public class TileEntitySelectorRenderer extends TileEntitySpecialRenderer {

	private final RenderItem renderer = new RenderItem() {
		@Override
		public boolean shouldSpreadItems() {
			return false;
		}

		@Override
		public boolean shouldBob() {
			return false;
		}

		@Override
		public byte getMiniBlockCount(ItemStack stack, byte original) {
			return 1;
		}

		@Override
		public byte getMiniItemCount(ItemStack stack, byte original) {
			return 1;
		}
	};

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		final TileEntitySelector selector = (TileEntitySelector)tileEntity;
		final Orientation orientation = selector.getOrientation();

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		TransformProvider.instance.multMatrix(orientation);
		GL11.glTranslated(-0.5, 0.501, -0.5); // 0.001 offset for 2d items in fast mode

		final int gridSize = selector.getGridSize();

		renderer.setRenderManager(RenderManager.instance);

		for (ItemSlot slot : selector.getSlots(gridSize)) {
			ItemStack stack = selector.getSlot(slot.slot);
			if (stack != null) {
				EntityItem display = selector.getDisplayEntity();

				GL11.glPushMatrix();
				GL11.glTranslated(slot.x, 0, slot.y + 0.03); // 0.03, since items are rendered off-center
				GL11.glRotated(-90, 1, 0, 0);
				final double scale = slot.size * 5;
				GL11.glScaled(scale, scale, scale);
				display.setEntityItemStack(stack);
				RenderItem.renderInFrame = true;
				renderer.doRender(display, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
				GL11.glPopMatrix();
			}
		}

		RenderItem.renderInFrame = false;

		GL11.glPopMatrix();
	}

}
