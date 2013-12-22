package openperipheral.addons.pim;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.addons.BlockOP;
import openperipheral.addons.Config;

public class BlockPIM extends BlockOP {

	public BlockPIM() {
		super(Config.blockPIMId, Material.ground);
		setStepSound(soundMetalFootstep);
		setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
	}

	@Override
	public boolean shouldRenderBlock() {
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
		if (!world.isRemote) {
			TileEntityPIM tile = this.getTileEntity(world, x, y, z,
					TileEntityPIM.class);
			if (entity instanceof EntityPlayer && tile != null) {
				TileEntityPIM pi = (TileEntityPIM) tile;
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
