package openperipheral.addons.proxy;

import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import openmods.Mods;
import openmods.api.IProxy;
import openperipheral.addons.ModuleComputerCraft;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.addons.glasses.client.TerminalManagerClient;
import openperipheral.addons.selector.TileEntitySelector;
import openperipheral.addons.selector.TileEntitySelectorRenderer;
import openperipheral.addons.sensors.TileEntitySensor;
import openperipheral.addons.sensors.TileEntitySensorRenderer;

public class ClientProxy implements IProxy {

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(TerminalManagerClient.instance);
		OBJLoader.instance.addDomain(OpenPeripheralAddons.MODID);

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.clientPreInit();
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerRenderInformation() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, new TileEntitySensorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySelector.class, new TileEntitySelectorRenderer());
	}
}
