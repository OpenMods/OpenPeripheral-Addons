package openperipheral.addons;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import openmods.Log;
import openmods.api.IOpenMod;
import openperipheral.addons.common.block.BlockGlassesBridge;

@Mod(modid = "OpenPeripheralAddons", name = "OpenPeripheralAddons", version = "@VERSION@", dependencies = "required-after:OpenPeripheral")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenPeripheralAddons implements IOpenMod {

	public static class Blocks {
		public static BlockGlassesBridge glassesBridge;
	}

	public static class Items {
	}
	
	public static int renderId;
	
	public static CreativeTabs tabOpenPeripheralAddons = new CreativeTabs("tabOpenPeripheralAddons") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(ObjectUtils.firstNonNull(OpenPeripheralAddons.Blocks.glassesBridge), 1, 0);
		}
	};
	
	@Override
	public Log getLog() {
		return null;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return tabOpenPeripheralAddons;
	}

	@Override
	public String getId() {
		return "openperipheraladdons";
	}

	@Override
	public int getRenderId() {
		return renderId;
	}

}
