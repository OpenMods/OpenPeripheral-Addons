package openperipheral.addons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import openmods.api.IProxy;
import openmods.config.RegisterBlock;
import openmods.config.RegisterItem;
import openperipheral.OpenPeripheralCore;
import openperipheral.addons.glasses.*;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.peripheralproxy.TileEntityPeripheralProxy;
import openperipheral.api.IntegrationRegistry;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import dan200.computer.api.ComputerCraftAPI;

@Mod(modid = "OpenPeripheral", name = "OpenPeripheralAddons", version = "@VERSION@", dependencies = "required-after:OpenMods;required-after:OpenPeripheralCore")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenPeripheralAddons {

	public static class Blocks {
		@RegisterBlock(name = "glassesbridge", tileEntity = TileEntityGlassesBridge.class)
		public static BlockGlassesBridge glassesBridge;

		@RegisterBlock(name = "peripheralproxy", tileEntity = TileEntityPeripheralProxy.class)
		public static BlockPeripheralProxy peripheralProxy;
	}

	public static class Items {
		@RegisterItem(name = "glasses")
		public static ItemGlasses glasses;
	}

	public static int renderId;

	@Instance(value = "OpenPeripheralAddons")
	public static OpenPeripheralAddons instance;

	@SidedProxy(clientSide = "openperipheral.addons.proxy.ClientProxy", serverSide = "openperipheral.addons.proxy.ServerProxy")
	public static IProxy proxy;

	public static CreativeTabs tabOpenPeripheralAddons = new CreativeTabs("tabOpenPeripheralAddons") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(ObjectUtils.firstNonNull(Items.glasses, Item.fishRaw), 1, 0);
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

		IntegrationRegistry.register(new AdapterGlassesBridge());
		EventTypes.registerTypes();
		MinecraftForge.EVENT_BUS.register(TerminalManagerServer.instance);

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
		ComputerCraftAPI.registerExternalPeripheral(TileEntityPeripheralProxy.class, OpenPeripheralCore.peripheralHandler);
	}
}
