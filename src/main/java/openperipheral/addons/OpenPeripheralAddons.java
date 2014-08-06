package openperipheral.addons;

import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmods.Mods;
import openmods.OpenMods;
import openmods.api.IProxy;
import openmods.config.BlockInstances;
import openmods.config.ItemInstances;
import openmods.config.game.*;
import openmods.config.game.ConfigurableFeatureManager.CustomFeatureRule;
import openmods.config.properties.ConfigProcessing;
import openmods.network.event.NetworkEventManager;
import openperipheral.addons.glasses.*;
import openperipheral.addons.glasses.TerminalEvent.TerminalClearEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;
import openperipheral.addons.narcissistic.TurtleUpgradeNarcissistic;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.peripheralproxy.TileEntityPeripheralProxy;
import openperipheral.addons.pim.BlockPIM;
import openperipheral.addons.pim.TileEntityPIM;
import openperipheral.addons.sensors.*;
import openperipheral.addons.ticketmachine.BlockTicketMachine;
import openperipheral.addons.ticketmachine.TileEntityTicketMachine;
import openperipheral.api.ApiAccess;
import openperipheral.api.IAdapterRegistry;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;

@Mod(modid = "OpenPeripheral", name = "OpenPeripheralAddons", version = "@VERSION@", dependencies = "required-after:OpenMods@[0.5.1,];required-after:ComputerCraft@[1.60,];required-after:OpenPeripheralCore")
public class OpenPeripheralAddons {

	public static class Blocks implements BlockInstances {
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

	public static class Items implements ItemInstances {
		@RegisterItem(name = "glasses")
		public static ItemGlasses glasses;

		@RegisterItem(name = "generic")
		public static ItemOPGeneric generic;
	}

	public static class Icons {
		public static IIcon sensorTurtle;
		public static IIcon narcissiticTurtle;
	}

	private GameConfigProvider gameConfig;

	public static int renderId;

	@Instance(value = "OpenPeripheral")
	public static OpenPeripheralAddons instance;

	@SidedProxy(clientSide = "openperipheral.addons.proxy.ClientProxy", serverSide = "openperipheral.addons.proxy.ServerProxy")
	public static IProxy proxy;

	public static CreativeTabs tabOpenPeripheralAddons = new CreativeTabs("tabOpenPeripheralAddons") {
		@Override
		public Item getTabIconItem() {
			return ObjectUtils.firstNonNull(Items.glasses, net.minecraft.init.Items.fish);
		}
	};

	public static TurtleUpgradeSensor sensorUpgrade;

	public static TurtleUpgradeNarcissistic narcissiticUpgrade;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		ConfigurableFeatureManager features = new ConfigurableFeatureManager();
		features.collectFromBlocks(OpenPeripheralAddons.Blocks.class);
		features.collectFromItems(OpenPeripheralAddons.Items.class);

		features.addCustomRule(AbstractFeatureManager.CATEGORY_BLOCKS, "ticketmachine", new CustomFeatureRule() {
			@Override
			public boolean isEnabled(boolean flag) {
				return flag && Loader.isModLoaded(Mods.RAILCRAFT);
			}
		});

		final File configFile = evt.getSuggestedConfigurationFile();
		Configuration config = new Configuration(configFile);

		ConfigProcessing.processAnnotations(configFile, "OpenPeripheralAddons", config, Config.class);
		features.loadFromConfiguration(config);

		if (config.hasChanged()) config.save();

		gameConfig = new GameConfigProvider("openperipheral");
		gameConfig.setFeatures(features);

		gameConfig.registerBlocks(OpenPeripheralAddons.Blocks.class);
		gameConfig.registerItems(OpenPeripheralAddons.Items.class);

		Config.register();

		NetworkEventManager.INSTANCE
				.startRegistration()
				.register(TerminalResetEvent.class)
				.register(TerminalClearEvent.class)
				.register(TerminalDataEvent.class);

		Items.generic.initRecipes();

		IAdapterRegistry adapters = ApiAccess.getApi(IAdapterRegistry.class);
		adapters.registerInline(TileEntityGlassesBridge.class);
		adapters.registerInline(TileEntityTicketMachine.class);
		adapters.register(new AdapterSensor());

		sensorUpgrade = new TurtleUpgradeSensor();
		ComputerCraftAPI.registerTurtleUpgrade(sensorUpgrade);

		narcissiticUpgrade = new TurtleUpgradeNarcissistic();
		ComputerCraftAPI.registerTurtleUpgrade(narcissiticUpgrade);

		MinecraftForge.EVENT_BUS.register(TerminalManagerServer.instance);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, OpenMods.proxy.wrapHandler(null));

		proxy.preInit();
	}

	@EventHandler
	public void handleRenames(FMLMissingMappingsEvent event) {
		Preconditions.checkNotNull(gameConfig, "What?");
		gameConfig.handleRemaps(event.get());
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

		recipeList.add(new ShapedOreRecipe(Blocks.glassesBridge, "lwl", "wrw", "lwl", 'w', wiredModem, 'r', net.minecraft.init.Blocks.redstone_block, 'l', wirelessModem));
		recipeList.add(new ShapedOreRecipe(Items.glasses, "mcm", 'm', advancedMonitor, 'c', cable));

	}
}
