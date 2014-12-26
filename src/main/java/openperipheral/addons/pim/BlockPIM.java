package openperipheral.addons.pim;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openperipheral.addons.BlockOP;

public class BlockPIM extends BlockOP {

	public static class Icons {
		public static IIcon black;
		public static IIcon blue;
	}

	public BlockPIM() {
		super(Material.ground);
		setStepSound(soundTypeMetal);
		setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
		setRenderMode(RenderMode.BLOCK_ONLY);
	}

	@Override
	public void registerBlockIcons(IIconRegister registry) {
		Icons.black = registry.registerIcon("openperipheraladdons:pim_black");
		Icons.blue = registry.registerIcon("openperipheraladdons:pim_blue");
		blockIcon = Icons.blue;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.DOWN;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		world.markBlockForUpdate(x, y, z);
		if (!world.isRemote && entity instanceof EntityPlayer) {
			TileEntityPIM pi = getTileEntity(world, x, y, z, TileEntityPIM.class);
			if (pi != null) pi.trySetPlayer((EntityPlayer)entity);
		}
	}

	@Override
	public boolean shouldDropFromTeAfterBreak() {
		return false;
	}

}
