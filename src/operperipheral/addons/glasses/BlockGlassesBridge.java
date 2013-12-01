package operperipheral.addons.glasses;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openperipheral.addons.BlockOP;
import openperipheral.addons.Config;

public class BlockGlassesBridge extends BlockOP {

	public BlockGlassesBridge() {
		super(Config.blockGlassesBridgeId, Material.ground);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public void registerIcons(IconRegister register) {
		blockIcon = register.registerIcon("openperipheraladdons:bridge");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking() || world.isRemote) return false;

		TileEntityGlassesBridge te = getTileEntity(world, x, y, z, TileEntityGlassesBridge.class);

		if (te == null) return false;

		ItemStack glassesStack = player.getHeldItem();
		if (glassesStack != null && glassesStack.getItem() instanceof ItemGlasses) {
			te.writeDataToGlasses(glassesStack);
		}

		return true;
	}
}
