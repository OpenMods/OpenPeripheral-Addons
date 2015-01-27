package openperipheral.addons.selector;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class TileEntitySelectorRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);

		TileEntitySelector selector = (TileEntitySelector)tileEntity;

		// Determine the the position and rotation of the items
		ForgeDirection rotation = selector.getRotation();

		// Position
		GL11.glTranslatef((float)(rotation.offsetX / 2.0), (float)(rotation.offsetY / 2.0), (float)(rotation.offsetZ / 2.0));

		// Rotation
		if (rotation == ForgeDirection.EAST) {
			GL11.glRotatef(270F, 0F, 1F, 0F);
		}

		if (rotation == ForgeDirection.WEST) {
			GL11.glRotatef(270F, 0F, -1F, 0F);
		}

		if (rotation == ForgeDirection.SOUTH) {
			GL11.glRotatef(180F, 0F, 1F, 0F);
		}

		if (rotation == ForgeDirection.UP) {
			GL11.glRotatef(90F, 1F, 0F, 0F);
		}

		if (rotation == ForgeDirection.DOWN) {
			GL11.glRotatef(90F, -1F, 0F, 0F);
		}

		// Determine the size the items are being rendered at
		int gridSize = selector.getGridSize();
		float scale = 0.5F;
		float padding = 0.3F;

		if (gridSize == 1) {
			scale = 1.25F;
			GL11.glTranslatef(-0.3F, -0.55F, 0.0F);
		} else if (gridSize == 2) {
			scale = 0.7F;
			padding = 0.375F;
			GL11.glTranslatef(-0.57F, -0.71F, 0.0F);
		} else if (gridSize == 3) {
			scale = 0.5F;
			padding = 0.25F;
			GL11.glTranslatef(-0.50F, -0.60F, 0.0F);
		}

		GL11.glPushMatrix();
		for (int row = selector.getGridSize() - 1; row >= 0; row--) {
			GL11.glTranslatef(0F, padding, 0F);
			GL11.glPushMatrix();

			for (int col = selector.getGridSize() - 1; col >= 0; col--) {
				GL11.glTranslatef(padding, 0F, 0F);
				GL11.glPushMatrix();
				GL11.glScalef(scale, scale, scale);
				EntityItem entity = selector.getStackEntity(row, col);
				if (entity != null) {
					RenderItem.renderInFrame = true;
					RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
					RenderItem.renderInFrame = false;
				}
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		GL11.glPopMatrix();
	}

}
