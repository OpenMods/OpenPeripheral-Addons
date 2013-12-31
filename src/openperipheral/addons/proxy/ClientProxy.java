package openperipheral.addons.proxy;

import net.minecraftforge.common.MinecraftForge;
import openmods.api.IProxy;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.addons.client.BlockRenderingHandler;
import openperipheral.addons.glasses.TerminalManagerClient;
import openperipheral.addons.sensors.TileEntitySensor;
import openperipheral.addons.sensors.TileEntitySensorRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.IGuiHandler;

public class ClientProxy implements IProxy {

	@Override
	public IGuiHandler createGuiHandler() {
		return null;
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(new TerminalManagerClient());
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerRenderInformation() {
		OpenPeripheralAddons.renderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, new TileEntitySensorRenderer());
	}

}
