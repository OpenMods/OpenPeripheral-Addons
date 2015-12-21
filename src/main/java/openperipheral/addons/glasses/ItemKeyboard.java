package openperipheral.addons.glasses;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import openperipheral.addons.OpenPeripheralAddons;

import com.google.common.base.Optional;

import cpw.mods.fml.common.FMLCommonHandler;

public class ItemKeyboard extends Item {

	public ItemKeyboard() {
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			final Optional<Long> terminalGuid = TerminalIdAccess.instance.getIdFrom(player);
			if (terminalGuid.isPresent()) {
				FMLCommonHandler.instance().showGuiScreen(new GuiCapture(terminalGuid.get()));
			} else {
				player.addChatMessage(new ChatComponentTranslation("openperipheral.misc.no_glasses"));
			}
		}

		return stack;
	}
}
