package openperipheral.addons.selector;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import openmods.api.ISelectionAware;
import openmods.block.BlockRotationMode;
import openmods.block.OpenBlock;
import openmods.geometry.Orientation;

public class BlockSelector extends OpenBlock implements ISelectionAware {
	private AxisAlignedBB selectorAABB;

	public BlockSelector() {
		super(Material.ground);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setInventoryRenderOrientation(Orientation.XP_ZP);
	}

	@Override
	public BlockRotationMode getRotationMode() {
		return BlockRotationMode.TWELVE_DIRECTIONS;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		return selectorAABB != null? selectorAABB : super.getSelectedBoundingBox(world, pos);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onSelected(World world, BlockPos pos, DrawBlockHighlightEvent evt) {
		final TileEntitySelector selector = getTileEntity(world, pos, TileEntitySelector.class);

		if (selector != null) {
			final MovingObjectPosition mop = evt.target;
			this.selectorAABB = selector.getSelection(mop.hitVec);
		} else this.selectorAABB = null;

		return false;
	}
}
