package openperipheral.addons.selector;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.block.BlockRotationMode;
import openperipheral.addons.BlockOP;

public class BlockSelector extends BlockOP {
	private AxisAlignedBB selectorAABB;

	public static class Icons {
		public static IIcon top;
		public static IIcon bottom;
		public static IIcon side;
	}

	public BlockSelector() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setRenderMode(RenderMode.BOTH);
		setInventoryRenderRotation(ForgeDirection.SOUTH);
	}

	@Override
	public void registerBlockIcons(IIconRegister registry) {
		Icons.top = registry.registerIcon("openperipheraladdons:selector_front");
		Icons.bottom = registry.registerIcon("openperipheraladdons:selector_bottom");
		Icons.side = registry.registerIcon("openperipheraladdons:selector_side");
		setTexture(ForgeDirection.UP, Icons.top);
		setTexture(ForgeDirection.DOWN, Icons.bottom);
		setTexture(ForgeDirection.EAST, Icons.side);
		setTexture(ForgeDirection.WEST, Icons.side);
		setTexture(ForgeDirection.NORTH, Icons.side);
		setTexture(ForgeDirection.SOUTH, Icons.side);
		setDefaultTexture(Icons.side);
	}

	public void overrideSelection(AxisAlignedBB aabb) {
		this.selectorAABB = aabb;
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
	public ForgeDirection calculateSide(EntityPlayer player, ForgeDirection direction) {
		// Mimic ComputerCraft Monitor placement behaviour, i.e. the screen is
		// looking at the player when placing the block.
		return super.calculateSide(player, direction).getOpposite();
	}
}
