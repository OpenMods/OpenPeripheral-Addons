package openperipheral.addons;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import openmods.Mods;
import openmods.api.IProxy;
import openmods.config.RegisterBlock;
import openmods.config.RegisterItem;
import openperipheral.addons.glasses.BlockGlassesBridge;
import openperipheral.addons.glasses.ItemGlasses;
import openperipheral.addons.glasses.TerminalManagerServer;
import openperipheral.addons.glasses.TileEntityGlassesBridge;
import openperipheral.addons.narcissistic.TurtleUpgradeNarcissistic;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.peripheralproxy.TileEntityPeripheralProxy;
import openperipheral.addons.pim.BlockPIM;
import openperipheral.addons.pim.TileEntityPIM;
import openperipheral.addons.sensors.AdapterSensor;
import openperipheral.addons.sensors.BlockSensor;
import openperipheral.addons.sensors.TileEntitySensor;
import openperipheral.addons.sensors.TurtleUpgradeSensor;
import openperipheral.api.OpenPeripheralAPI;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.turtle.api.TurtleAPI;

@Mod(modid = "OpenPeripheral", name = "OpenPeripheralAddons", version = "@VERSION@", dependencies = "required-after:OpenMods;after:OpenPeripheralCore")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenPeripheralAddons {

	public static class Blocks {
		@RegisterBlock(name = "glassesbridge", tileEntity = TileEntityGlassesBridge.class)
		public static BlockGlassesBridge glassesBridge;

		@RegisterBlock(name = "peripheralproxy", tileEntity = TileEntityPeripheralProxy.class)
		public static BlockPeripheralProxy peripheralProxy;

		@RegisterBlock(name = "pim", tileEntity = TileEntityPIM.class, unlocalizedName = "playerinventory")
		public static BlockPIM pim;

		@RegisterBlock(name = "sensor", tileEntity = TileEntitySensor.class)
		public static BlockSensor sensor;
	}

	public static class Items {
		@RegisterItem(name = "glasses")
		public static ItemGlasses glasses;

		@RegisterItem(name = "generic")
		public static ItemOPGeneric generic;
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

	public static TurtleUpgradeSensor sensorUpgrade;

	public static TurtleUpgradeNarcissistic narcissiticUpgrade;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());
		Config.readConfig(configFile);
		if (configFile.hasChanged()) {
			configFile.save();
		}
		Config.register();

		Items.generic.initRecipes();

		OpenPeripheralAPI.createAdapter(TileEntityGlassesBridge.class);

		OpenPeripheralAPI.register(new AdapterSensor());

		sensorUpgrade = new TurtleUpgradeSensor();
		TurtleAPI.registerUpgrade(sensorUpgrade);

		narcissiticUpgrade = new TurtleUpgradeNarcissistic();
		TurtleAPI.registerUpgrade(narcissiticUpgrade);

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

		Block peripheral = GameRegistry.findBlock(Mods.COMPUTERCRAFT, "CC-Peripheral");
		Block cable = GameRegistry.findBlock(Mods.COMPUTERCRAFT, "CC-Cable");

		CraftingManager crafting = CraftingManager.getInstance();

		crafting.addRecipe(new ItemStack(Blocks.glassesBridge), new Object[] { "lwl", "wrw", "lwl", 'w', new ItemStack(cable, 1, 1), 'r', Block.blockRedstone, 'l', new ItemStack(peripheral, 1, 1) });
		crafting.addRecipe(new ItemStack(Items.glasses), new Object[] { "mcm", 'm', new ItemStack(peripheral, 1, 4), 'c', new ItemStack(cable) });

	}
}
