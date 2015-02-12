package openperipheral.addons.selector;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SelectorHighlightHandler {

	@SubscribeEvent
	public void onHighlightDraw(DrawBlockHighlightEvent evt) {
		final MovingObjectPosition mop = evt.target;

		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			final int x = mop.blockX;
			final int y = mop.blockY;
			final int z = mop.blockZ;

			final World world = evt.player.worldObj;
			final Block block = world.getBlock(x, y, z);

			if (block == Blocks.selector) {

				TileEntity te = world.getTileEntity(x, y, z);

				AxisAlignedBB selection = null;
				if (te instanceof TileEntitySelector) {
					selection = ((TileEntitySelector)te).getSelection(mop.hitVec, mop.sideHit);
				}

				Blocks.selector.overrideSelection(selection);
			}
		}
	}
}
