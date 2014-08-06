package openperipheral.addons.ticketmachine;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import openperipheral.addons.BlockOP;

public class BlockTicketMachine extends BlockOP {

	public static IIcon iconFrontEmpty;
	public static IIcon iconFrontTicket;

	public BlockTicketMachine() {
		super(Material.iron);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister registry) {
		iconFrontEmpty = registry.registerIcon("openperipheraladdons:ticketmachine_front_empty");
		iconFrontTicket = registry.registerIcon("openperipheraladdons:ticketmachine_front_ticket");

		setTexture(ForgeDirection.EAST, registry.registerIcon("openperipheraladdons:ticketmachine_left"));
		setTexture(ForgeDirection.WEST, registry.registerIcon("openperipheraladdons:ticketmachine_right"));
		setTexture(ForgeDirection.SOUTH, iconFrontEmpty);
		setTexture(ForgeDirection.NORTH, registry.registerIcon("openperipheraladdons:ticketmachine_back"));
		setTexture(ForgeDirection.UP, registry.registerIcon("openperipheraladdons:ticketmachine_top"));

		IIcon bottom = registry.registerIcon("openperipheraladdons:ticketmachine_bottom");
		setTexture(ForgeDirection.DOWN, bottom);
		setDefaultTexture(bottom);
	}

}
