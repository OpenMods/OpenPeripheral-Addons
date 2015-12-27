package openperipheral.addons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import openmods.Mods;
import openmods.OpenMods;
import openmods.access.ApiFactory;
import openmods.access.ApiFactory.ApiProviderSetup;
import openmods.access.ApiProviderRegistry;
import openmods.api.IProxy;
import openmods.config.BlockInstances;
import openmods.config.ItemInstances;
import openmods.config.game.*;
import openmods.config.properties.ConfigProcessing;
import openmods.network.event.NetworkEventManager;
import openperipheral.addons.api.ApiHolder;
import openperipheral.addons.api.IApiInterface;
import openperipheral.addons.glasses.*;
import openperipheral.addons.glasses.GlassesEvent.GlassesChangeBackgroundEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseButtonEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseWheelEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyDownEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyUpEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseButtonEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseDragEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseWheelEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetDragParamsEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetGuiVisibilityEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetKeyRepeatEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSignalCaptureEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesStopCaptureEvent;
import openperipheral.addons.glasses.server.TerminalManagerServer;
import openperipheral.addons.pim.BlockPIM;
import openperipheral.addons.pim.TileEntityPIM;
import openperipheral.addons.selector.BlockSelector;
import openperipheral.addons.selector.TileEntitySelector;
import openperipheral.addons.sensors.AdapterSensor;
import openperipheral.addons.sensors.BlockSensor;
import openperipheral.addons.sensors.TileEntitySensor;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = OpenPeripheralAddons.MODID, name = "OpenPeripheralAddons", version = "$VERSION$", dependencies = "required-after:OpenMods@[$LIB-VERSION$,$NEXT-LIB-VERSION$);required-after:OpenPeripheralApi@$OP-API-VERSION$;after:ComputerCraft@[1.70,]")
public class OpenPeripheralAddons {

	public static final String MODID = "OpenPeripheral";

	public static class Blocks implements BlockInstances {
		@RegisterBlock(name = "glassesbridge", tileEntity = TileEntityGlassesBridge.class, itemBlock = ItemGlassesBridge.class, textureName = "bridge")
		public static BlockGlassesBridge glassesBridge;

		@RegisterBlock(name = "pim", tileEntity = TileEntityPIM.class, unlocalizedName = "playerinventory", textureName = "pim_blue")
		public static BlockPIM pim;

		@RegisterBlock(name = "sensor", tileEntity = TileEntitySensor.class)
		public static BlockSensor sensor;

		@RegisterBlock(name = "selector", tileEntity = TileEntitySelector.class, textureName = "selector_side")
		public static BlockSelector selector;
	}

	public static class Items implements ItemInstances {
		@RegisterItem(name = "glasses")
		public static ItemGlasses glasses;

		@RegisterItem(name = "keyboard")
		public static ItemKeyboard keyboard;

		@RegisterItem(name = "generic", textureName = RegisterItem.NONE)
		public static ItemOPGeneric generic;
	}

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

	private final ModStartupHelper startupHelper = new ModStartupHelper("openperipheral") {

		@Override
		protected void setupIds(GameConfigProvider gameConfig) {
			gameConfig.setTextureModId("openperipheraladdons");
		}

		@Override
		protected void populateConfig(Configuration config) {
			ConfigProcessing.processAnnotations("OpenPeripheralAddons", config, Config.class);
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		startupHelper.registerBlocksHolder(OpenPeripheralAddons.Blocks.class);
		startupHelper.registerItemsHolder(OpenPeripheralAddons.Items.class);

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.preInit(startupHelper);
		if (Loader.isModLoaded(Mods.RAILCRAFT)) ModuleRailcraft.preInit(startupHelper);

		startupHelper.preInit(evt.getSuggestedConfigurationFile());

		Recipes.register();
		MetasGeneric.registerItems();

		NetworkEventManager.INSTANCE
				.startRegistration()
				.register(TerminalEvent.PrivateDrawableReset.class)
				.register(TerminalEvent.PublicDrawableReset.class)
				.register(TerminalEvent.PrivateStructureReset.class)
				.register(TerminalEvent.PublicStructureReset.class)

				.register(TerminalEvent.PrivateClear.class)
				.register(TerminalEvent.PublicClear.class)

				.register(TerminalEvent.PrivateDrawableData.class)
				.register(TerminalEvent.PublicDrawableData.class)
				.register(TerminalEvent.PrivateStructureData.class)
				.register(TerminalEvent.PublicStructureData.class)

				.register(GlassesSignalCaptureEvent.class)
				.register(GlassesStopCaptureEvent.class)
				.register(GlassesMouseWheelEvent.class)
				.register(GlassesMouseButtonEvent.class)
				.register(GlassesComponentMouseWheelEvent.class)
				.register(GlassesComponentMouseButtonEvent.class)
				.register(GlassesMouseDragEvent.class)
				.register(GlassesKeyDownEvent.class)
				.register(GlassesKeyUpEvent.class)
				.register(GlassesChangeBackgroundEvent.class)
				.register(GlassesSetDragParamsEvent.class)
				.register(GlassesSetKeyRepeatEvent.class)
				.register(GlassesSetGuiVisibilityEvent.class);

		Items.generic.initRecipes();

		MinecraftForge.EVENT_BUS.register(TerminalManagerServer.instance.createForgeListener());
		FMLCommonHandler.instance().bus().register(TerminalManagerServer.instance.createFmlListener());

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, OpenMods.proxy.wrapHandler(null));

		TerminalIdAccess.instance.register(new TerminalIdAccess.InterfaceGetter());
		TerminalIdAccess.instance.register(new TerminalIdAccess.InterfaceSetter());

		TerminalIdAccess.instance.register(new NbtGuidProviders.NbtGetter());
		TerminalIdAccess.instance.register(new NbtGuidProviders.NbtSetter());

		ApiFactory.instance.createApi(ApiHolder.class, IApiInterface.class, evt.getAsmData(),
				new ApiProviderSetup<IApiInterface>() {
					@Override
					public void setup(ApiProviderRegistry<IApiInterface> registry) {
						registry.registerInstance(TerminalIdAccess.instance);
					}
				});

		proxy.preInit();
	}

	@EventHandler
	public void handleRenames(FMLMissingMappingsEvent event) {
		startupHelper.handleRenames(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();
		proxy.registerRenderInformation();

		OpcAccess.checkApiPresent();

		OpcAccess.adapterRegistry.register(new AdapterSensor());

		OpcAccess.itemStackMetaBuilder.register(new ItemTerminalMetaProvider());
		OpcAccess.itemStackMetaBuilder.register(new NbtTerminalMetaProvider());

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.init();
	}
}
