package openperipheral.addons.selector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.api.IIconProvider;
import openmods.block.BlockRotationMode;
import openmods.block.OpenBlock.BlockPlacementMode;
import openmods.block.OpenBlock.RenderMode;
import openperipheral.addons.BlockOP;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy.Icons;

public class BlockSelector extends BlockOP {
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

	@Override
	public boolean isOpaqueCube() {
		// Make sure the TESR renders the items correctly.
		return false;
	}

	@Override
	public ForgeDirection calculateSide(EntityPlayer player, ForgeDirection direction) {
		// Mimic ComputerCraft Monitor placement behaviour, i.e. the screen is
		// looking at the player when placing the block.
		return super.calculateSide(player, direction).getOpposite();
	}

	@Override
	public boolean shouldDropFromTeAfterBreak() {
		// Prevent the fake inventory contents from spilling into the world
		// when the block is being broken
		return false;
	}
}
