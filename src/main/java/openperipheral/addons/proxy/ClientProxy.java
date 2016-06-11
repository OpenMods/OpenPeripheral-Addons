package openperipheral.addons.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
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

public class ClientProxy implements IProxy {

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(TerminalManagerClient.instance.createForgeBusListener());
		FMLCommonHandler.instance().bus().register(TerminalManagerClient.instance.createFmlBusListener());
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
