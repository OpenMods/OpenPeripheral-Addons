package openperipheral.addons.proxy;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import openmods.api.IProxy;
import openmods.renderer.BlockRenderingHandler;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import openperipheral.addons.glasses.client.TerminalManagerClient;
import openperipheral.addons.pim.BlockPIMRenderer;
import openperipheral.addons.selector.TileEntitySelector;
import openperipheral.addons.selector.TileEntitySelectorRenderer;
import openperipheral.addons.sensors.TileEntitySensor;
import openperipheral.addons.sensors.TileEntitySensorRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy implements IProxy {

	public static class IconLoader {
		@SubscribeEvent
		public void textureHook(TextureStitchEvent.Pre event) {
			if (event.map.getTextureType() == 0) {
				OpenPeripheralAddons.Icons.narcissiticTurtle = event.map.registerIcon("computercraft:turtle");
				OpenPeripheralAddons.Icons.sensorTurtle = event.map.registerIcon("openperipheraladdons:sensorturtle");
			}
		}
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(TerminalManagerClient.instance.createForgeBusListener());
		FMLCommonHandler.instance().bus().register(TerminalManagerClient.instance.createFmlBusListener());

		MinecraftForge.EVENT_BUS.register(new IconLoader());
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerRenderInformation() {
		OpenPeripheralAddons.renderId = RenderingRegistry.getNextAvailableRenderId();
		final BlockRenderingHandler blockRenderingHandler = new BlockRenderingHandler(OpenPeripheralAddons.renderId);
		RenderingRegistry.registerBlockHandler(blockRenderingHandler);

		blockRenderingHandler.addRenderer(Blocks.pim, new BlockPIMRenderer());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, new TileEntitySensorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySelector.class, new TileEntitySelectorRenderer());
	}
}
