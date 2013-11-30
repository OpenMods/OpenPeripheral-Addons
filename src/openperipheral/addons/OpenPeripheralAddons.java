package openperipheral.addons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import openmods.api.IProxy;
import openmods.config.RegisterBlock;
import openperipheral.addons.common.block.BlockGlassesBridge;
import openperipheral.addons.common.tileentity.TileEntityGlassesBridge;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "OpenPeripheral", name = "OpenPeripheral", version = "@VERSION@", dependencies = "required-after:OpenPeripheralCore,required-after:OpenMods")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenPeripheralAddons {

	public static class Blocks {
		@RegisterBlock(name = "glassesbridge", tileEntity = TileEntityGlassesBridge.class)
		public static BlockGlassesBridge glassesBridge;
	}

	public static class Items {}

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
}
