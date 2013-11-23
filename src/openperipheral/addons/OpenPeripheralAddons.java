package openperipheral.addons;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import openmods.Log;
import openmods.api.IOpenMod;
import openmods.api.IProxy;
import openperipheral.addons.common.block.BlockGlassesBridge;

@Mod(modid = "OpenPeripheralAddons", name = "OpenPeripheralAddons", version = "@VERSION@", dependencies = "required-after:OpenPeripheral")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenPeripheralAddons implements IOpenMod {

	public static class Blocks {
		public static BlockGlassesBridge glassesBridge;
	}

	public static class Items {
	}

	public static final String CHANNEL = "OpenPeripheral";
	
	public static int renderId;
	
	@Instance(value = "OpenPeripheralAddons")
	public static OpenPeripheralAddons instance;

	@SidedProxy(clientSide = "openperipheral.addons.client.ClientProxy", serverSide = "openperipheral.addons.common.ServerProxy")
	public static IProxy proxy;
	
	public static CreativeTabs tabOpenPeripheralAddons = new CreativeTabs("tabOpenPeripheralAddons") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(ObjectUtils.firstNonNull(OpenPeripheralAddons.Blocks.glassesBridge), 1, 0);
		}
	};
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());
		Config.readConfig(configFile);
		if (configFile.hasChanged()) {
			configFile.save();
		}
		Config.register();
		proxy.preInit();
	}
	

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();
		proxy.registerRenderInformation();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.postInit();
	}
	
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
