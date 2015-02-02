package openperipheral.addons;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
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
import openperipheral.addons.glasses.GlassesEvent.GlassesChangeBackground;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseButtonEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseWheelEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyDownEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyUpEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseButtonEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseWheelEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSignalCaptureEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesStopCaptureEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalClearEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;
import openperipheral.addons.narcissistic.TurtleUpgradeNarcissistic;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.peripheralproxy.TileEntityPeripheralProxy;
import openperipheral.addons.pim.BlockPIM;
import openperipheral.addons.pim.TileEntityPIM;
import openperipheral.addons.selector.BlockSelector;
import openperipheral.addons.selector.TileEntitySelector;
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
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import dan200.computercraft.api.ComputerCraftAPI;

@Mod(modid = OpenPeripheralAddons.MODID, name = "OpenPeripheralAddons", version = "$VERSION$", dependencies = "required-after:OpenMods@[$LIB-VERSION$];required-after:ComputerCraft@[1.64,];required-after:OpenPeripheralCore")
public class OpenPeripheralAddons {

	public static final String MODID = "OpenPeripheral";

	public static class Blocks implements BlockInstances {
		@RegisterBlock(name = "glassesbridge", tileEntity = TileEntityGlassesBridge.class, itemBlock = ItemGlassesBridge.class)
		public static BlockGlassesBridge glassesBridge;

		@RegisterBlock(name = "peripheralproxy", tileEntity = TileEntityPeripheralProxy.class)
		public static BlockPeripheralProxy peripheralProxy;

		@RegisterBlock(name = "pim", tileEntity = TileEntityPIM.class, unlocalizedName = "playerinventory")
		public static BlockPIM pim;

		@RegisterBlock(name = "sensor", tileEntity = TileEntitySensor.class)
		public static BlockSensor sensor;

		@RegisterBlock(name = "ticketmachine", tileEntity = TileEntityTicketMachine.class)
		public static BlockTicketMachine ticketMachine;

		@RegisterBlock(name = "selector", tileEntity = TileEntitySelector.class)
		public static BlockSelector selector;
	}

	public static class Items implements ItemInstances {
		@RegisterItem(name = "glasses")
		public static ItemGlasses glasses;

		@RegisterItem(name = "keyboard")
		public static ItemKeyboard keyboard;

		@RegisterItem(name = "generic")
		public static ItemOPGeneric generic;
	}

	public static class Icons {
		public static IIcon sensorTurtle;
		public static IIcon narcissiticTurtle;
	}

	private GameConfigProvider gameConfig;

	public static int renderId;

	@Instance(MODID)
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

		if (OpenPeripheralAddons.Blocks.peripheralProxy != null) TileEntityPeripheralProxy.initAccess();

		Config.register();

		NetworkEventManager.INSTANCE
				.startRegistration()
				.register(TerminalResetEvent.class)
				.register(TerminalClearEvent.class)
				.register(TerminalDataEvent.class)

				.register(GlassesSignalCaptureEvent.class)
				.register(GlassesStopCaptureEvent.class)
				.register(GlassesMouseWheelEvent.class)
				.register(GlassesMouseButtonEvent.class)
				.register(GlassesComponentMouseWheelEvent.class)
				.register(GlassesComponentMouseButtonEvent.class)
				.register(GlassesKeyDownEvent.class)
				.register(GlassesKeyUpEvent.class)
				.register(GlassesChangeBackground.class);

		Items.generic.initRecipes();

		IAdapterRegistry adapters = ApiAccess.getApi(IAdapterRegistry.class);
		adapters.registerInline(TileEntityGlassesBridge.class);
		adapters.registerInline(TileEntityTicketMachine.class);
		adapters.registerInline(TileEntitySelector.class);
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
}
