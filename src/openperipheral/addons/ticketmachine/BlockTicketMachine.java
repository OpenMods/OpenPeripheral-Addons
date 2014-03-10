package openperipheral.addons.ticketmachine;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.addons.BlockOP;
import openperipheral.addons.Config;

public class BlockTicketMachine extends BlockOP {

	public static Icon iconFrontEmpty;
	public static Icon iconFrontTicket;

	public BlockTicketMachine() {
		super(Config.blockTicketMachineId, Material.iron);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		iconFrontEmpty = registry.registerIcon("openperipheraladdons:ticketmachine_front_empty");
		iconFrontTicket = registry.registerIcon("openperipheraladdons:ticketmachine_front_ticket");

		setTexture(ForgeDirection.EAST, registry.registerIcon("openperipheraladdons:ticketmachine_left"));
		setTexture(ForgeDirection.WEST, registry.registerIcon("openperipheraladdons:ticketmachine_right"));
		setTexture(ForgeDirection.SOUTH, iconFrontEmpty);
		setTexture(ForgeDirection.NORTH, registry.registerIcon("openperipheraladdons:ticketmachine_back"));
		setTexture(ForgeDirection.UP, registry.registerIcon("openperipheraladdons:ticketmachine_top"));

		Icon bottom = registry.registerIcon("openperipheraladdons:ticketmachine_bottom");
		setTexture(ForgeDirection.DOWN, bottom);
		setDefaultTexture(bottom);
	}

}
