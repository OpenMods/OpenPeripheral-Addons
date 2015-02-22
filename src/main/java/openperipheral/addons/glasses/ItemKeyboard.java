package openperipheral.addons.glasses;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.addons.api.ITerminalItem;
import cpw.mods.fml.common.FMLCommonHandler;

public class ItemKeyboard extends Item {

	public ItemKeyboard() {
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
		setMaxStackSize(1);
	}

	@Override
	public void registerIcons(IIconRegister register) {
		itemIcon = register.registerIcon("openperipheraladdons:keyboard");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			ItemStack helmet = TerminalUtils.getHeadSlot(player);
			if (helmet != null) {
				Item item = helmet.getItem();
				if (item instanceof ITerminalItem) {
					Long guid = ((ITerminalItem)item).getTerminalGuid(helmet);
					if (guid != null) FMLCommonHandler.instance().showGuiScreen(new GuiCapture(guid));
				}
			}
		}

		return stack;
	}

}
