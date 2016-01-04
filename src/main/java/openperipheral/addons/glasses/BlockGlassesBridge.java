package openperipheral.addons.glasses;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openmods.block.OpenBlock;

public class BlockGlassesBridge extends OpenBlock {

	public BlockGlassesBridge() {
		super(Material.ground);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking() || world.isRemote) return false;

		final TileEntityGlassesBridge te = getTileEntity(world, blockPos, TileEntityGlassesBridge.class);
		if (te == null) return false;

		return TerminalIdAccess.instance.setIdFor(player, te.getGuid());
	}
}
