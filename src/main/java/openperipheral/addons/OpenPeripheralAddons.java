package openperipheral.addons;

import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmods.Mods;
import openmods.OpenMods;
import openmods.api.IProxy;
import openmods.config.ConfigProcessing;
import openmods.config.RegisterBlock;
import openmods.config.RegisterItem;
import openperipheral.addons.glasses.*;
import openperipheral.addons.narcissistic.TurtleUpgradeNarcissistic;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.peripheralproxy.TileEntityPeripheralProxy;
import openperipheral.addons.pim.BlockPIM;
import openperipheral.addons.pim.TileEntityPIM;
import openperipheral.addons.sensors.*;
import openperipheral.addons.ticketmachine.BlockTicketMachine;
import openperipheral.addons.ticketmachine.TileEntityTicketMachine;
import openperipheral.api.OpenPeripheralAPI;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;

@Mod(modid = "OpenPeripheral", name = "OpenPeripheralAddons", version = "@VERSION@", dependencies = "required-after:OpenMods@[0.5,];required-after:ComputerCraft@[1.60,];after:OpenPeripheralCore")
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

		@RegisterBlock(name = "ticketmachine", tileEntity = TileEntityTicketMachine.class)
		public static BlockTicketMachine ticketMachine;
	}

	public static class Items {
		@RegisterItem(name = "glasses")
		public static ItemGlasses glasses;

		@RegisterItem(name = "generic")
		public static ItemOPGeneric generic;
	}

	public static class Icons {
		public static Icon sensorTurtle;
		public static Icon narcissiticTurtle;
	}

	public static int renderId;

	@Instance(value = "OpenPeripheral")
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
		final File configFile = evt.getSuggestedConfigurationFile();
		Configuration config = new Configuration(configFile);
		ConfigProcessing.processAnnotations(configFile, "OpenPeripheral", config, Config.class);
		if (config.hasChanged()) {
			config.save();
		}
		Config.register();

		Items.generic.initRecipes();

		OpenPeripheralAPI.createAdapter(TileEntityGlassesBridge.class);
		OpenPeripheralAPI.createAdapter(TileEntityTicketMachine.class);
		OpenPeripheralAPI.register(new AdapterSensor());

		sensorUpgrade = new TurtleUpgradeSensor();
		ComputerCraftAPI.registerTurtleUpgrade(sensorUpgrade);

		narcissiticUpgrade = new TurtleUpgradeNarcissistic();
		ComputerCraftAPI.registerTurtleUpgrade(narcissiticUpgrade);

		EventTypes.registerTypes();
		MinecraftForge.EVENT_BUS.register(TerminalManagerServer.instance);

		NetworkRegistry.instance().registerGuiHandler(instance, OpenMods.proxy.wrapHandler(null));

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();
		proxy.registerRenderInformation();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {

		Block blockPeripheral = GameRegistry.findBlock(Mods.COMPUTERCRAFT, "CC-Peripheral");
		Block blockCable = GameRegistry.findBlock(Mods.COMPUTERCRAFT, "CC-Cable");

		final ItemStack cable = new ItemStack(blockCable, 1, 0);
		final ItemStack wiredModem = new ItemStack(blockCable, 1, 1);
		final ItemStack wirelessModem = new ItemStack(blockPeripheral, 1, 1);
		final ItemStack advancedMonitor = new ItemStack(blockPeripheral, 1, 4);

		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		recipeList.add(new ShapedOreRecipe(Blocks.glassesBridge, "lwl", "wrw", "lwl", 'w', wiredModem, 'r', Block.blockRedstone, 'l', wirelessModem));
		recipeList.add(new ShapedOreRecipe(Items.glasses, "mcm", 'm', advancedMonitor, 'c', cable));

	}
}
