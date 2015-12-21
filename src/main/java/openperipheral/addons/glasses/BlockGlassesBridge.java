package openperipheral.addons.glasses;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import openperipheral.addons.BlockOP;

public class BlockGlassesBridge extends BlockOP {

	public BlockGlassesBridge() {
		super(Material.ground);
		setRenderMode(RenderMode.BLOCK_ONLY);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking() || world.isRemote) return false;

		final TileEntityGlassesBridge te = getTileEntity(world, x, y, z, TileEntityGlassesBridge.class);
		if (te == null) return false;

		return TerminalIdAccess.instance.setIdFor(player, te.getGuid());
	}
}
