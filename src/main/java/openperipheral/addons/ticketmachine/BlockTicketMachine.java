package openperipheral.addons.ticketmachine;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.block.BlockRotationMode;
import openperipheral.addons.BlockOP;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTicketMachine extends BlockOP {

	public static IIcon iconFrontEmpty;
	public static IIcon iconFrontTicket;

	public BlockTicketMachine() {
		super(Material.iron);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setRenderMode(RenderMode.BLOCK_ONLY);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);

		iconFrontEmpty = registry.registerIcon("openperipheraladdons:ticketmachine_front_empty");
		iconFrontTicket = registry.registerIcon("openperipheraladdons:ticketmachine_front_ticket");

		setTexture(ForgeDirection.EAST, registry.registerIcon("openperipheraladdons:ticketmachine_left"));
		setTexture(ForgeDirection.WEST, registry.registerIcon("openperipheraladdons:ticketmachine_right"));
		setTexture(ForgeDirection.SOUTH, iconFrontEmpty);
		setTexture(ForgeDirection.NORTH, registry.registerIcon("openperipheraladdons:ticketmachine_back"));
		setTexture(ForgeDirection.UP, registry.registerIcon("openperipheraladdons:ticketmachine_top"));

		setTexture(ForgeDirection.DOWN, registry.registerIcon("openperipheraladdons:ticketmachine_bottom"));
	}

}
