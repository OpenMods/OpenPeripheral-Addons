package openperipheral.addons.pim;

import net.minecraft.block.material.Material;
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
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.DOWN;
	}
}
