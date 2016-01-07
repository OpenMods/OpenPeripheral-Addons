package openperipheral.addons.pim;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmods.block.OpenBlock;

public class BlockPIM extends OpenBlock {

	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public BlockPIM() {
		super(Material.ground);
		setStepSound(soundTypeMetal);
		setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
		setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, getPropertyOrientation(), ACTIVE);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
		world.markBlockForUpdate(pos);
		if (!world.isRemote && entity instanceof EntityPlayer) {
			TileEntityPIM pi = getTileEntity(world, pos, TileEntityPIM.class);
			if (pi != null) pi.trySetPlayer((EntityPlayer)entity);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ACTIVE, meta != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ACTIVE)? 1 : 0;
	}

	@Override
	public boolean shouldDropFromTeAfterBreak() {
		return false;
	}

}
