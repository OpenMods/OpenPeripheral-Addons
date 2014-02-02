package openperipheral.addons.pim;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.addons.BlockOP;
import openperipheral.addons.Config;

public class BlockPIM extends BlockOP {

	public static class Icons {
		public static Icon black;
		public static Icon blue;
	}

	public BlockPIM() {
		super(Config.blockPIMId, Material.ground);
		setStepSound(soundMetalFootstep);
		setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		Icons.black = registry.registerIcon("openperipheraladdons:pim_black");
		Icons.blue = registry.registerIcon("openperipheraladdons:pim_blue");
		blockIcon = Icons.blue;
	}

	@Override
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return getIcon(0, 0);
	}

	@Override
	public Icon getIcon(int par1, int par2) {
		return Icons.blue;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.DOWN;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		world.markBlockForRenderUpdate(x, y, z);
		if (!world.isRemote && entity instanceof EntityPlayer) {
			TileEntityPIM pi = getTileEntity(world, x, y, z, TileEntityPIM.class);
			if (pi != null) pi.trySetPlayer((EntityPlayer)entity);
		}
	}

}
