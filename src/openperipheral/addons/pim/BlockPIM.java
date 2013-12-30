package openperipheral.addons.pim;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.addons.BlockOP;
import openperipheral.addons.Config;

public class BlockPIM extends BlockOP {

	public boolean secondPass = false;

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
		blockIcon = Icons.black;
	}

	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2,
			int par3, int par4, int par5) {
		return getIcon(0, 0);
	}

	public Icon getIcon(int par1, int par2) {
		if (secondPass) {
			return Icons.black;
		}
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
	public boolean isBlockSolidOnSide(World world, int x, int y, int z,
			ForgeDirection side) {
		return side == ForgeDirection.DOWN;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z,
			Entity entity) {
		world.markBlockForRenderUpdate(x, y, z);
		if (!world.isRemote) {
			TileEntityPIM pi = this.getTileEntity(world, x, y, z, TileEntityPIM.class);
			if (entity instanceof EntityPlayer && pi != null) {
				if (pi.getPlayer() == null) {
					ChunkCoordinates coordinates = ((EntityPlayer) entity)
							.getPlayerCoordinates();
					if (coordinates.posX == x && coordinates.posY == y
							&& coordinates.posZ == z) {
						pi.setPlayer((EntityPlayer) entity);
					}
				}
			}
		}
	}

}
