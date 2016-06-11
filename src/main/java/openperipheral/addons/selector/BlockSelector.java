package openperipheral.addons.selector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.api.ISelectionAware;
import openmods.block.BlockRotationMode;
import openmods.geometry.Orientation;
import openperipheral.addons.BlockOP;

public class BlockSelector extends BlockOP implements ISelectionAware {
	private AxisAlignedBB selectorAABB;

	public BlockSelector() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.TWELVE_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setRenderMode(RenderMode.BOTH);
		setInventoryRenderOrientation(Orientation.XP_ZP);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);

		setTexture(ForgeDirection.UP, registry.registerIcon("openperipheraladdons:selector_front"));
		setTexture(ForgeDirection.DOWN, registry.registerIcon("openperipheraladdons:selector_bottom"));
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return selectorAABB != null? selectorAABB : super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onSelected(World world, int x, int y, int z, DrawBlockHighlightEvent evt) {
		final TileEntitySelector selector = getTileEntity(world, x, y, z, TileEntitySelector.class);

		if (selector != null) {
			final MovingObjectPosition mop = evt.target;
			this.selectorAABB = selector.getSelection(mop.hitVec, mop.sideHit);
		} else this.selectorAABB = null;

		return false;
	}
}
