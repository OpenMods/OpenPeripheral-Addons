package openperipheral.addons;

import net.minecraftforge.common.Configuration;
import openmods.config.BlockId;
import openmods.config.ConfigProcessing;
import openmods.config.ConfigProperty;
import openmods.config.ItemId;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import openperipheral.addons.OpenPeripheralAddons.Items;
import openperipheral.addons.glasses.BlockGlassesBridge;
import openperipheral.addons.glasses.ItemGlasses;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.pim.BlockPIM;
import openperipheral.addons.sensors.BlockSensor;

public class Config {

	@ItemId(description = "The id of the glasses")
	public static int itemGlassesId = 9258;

	@BlockId(description = "The id of the glasses bridge")
	public static int blockGlassesBridgeId = 3000;

	@BlockId(description = "The id of the peripheral proxy")
	public static int blockPeripheralProxyId = 3001;

	@BlockId(description = "The id of the pim block")
	public static int blockPIMId = 3002;

	@BlockId(description = "The id of the sensor block")
	public static int sensorBlockId = 3003;

	@ConfigProperty(category = "sensor", name = "rangeInStorm")
	public static int sensorRangeInStorm = 5;

	@ConfigProperty(category = "sensor", name = "normalRange")
	public static int sensorRange = 5;

	public static void readConfig(Configuration configFile) {
		ConfigProcessing.processAnnotations(configFile, Config.class);
	}

	public static void register() {
		if (ConfigProcessing.canRegisterBlock(blockGlassesBridgeId)) {
			Blocks.glassesBridge = new BlockGlassesBridge();
		}

		if (ConfigProcessing.canRegisterBlock(blockPeripheralProxyId)) {
			Blocks.peripheralProxy = new BlockPeripheralProxy();
		}
		
		if (ConfigProcessing.canRegisterBlock(blockPIMId)) {
			Blocks.pim = new BlockPIM();
		}
		
		if (ConfigProcessing.canRegisterBlock(sensorBlockId)) {
			Blocks.sensor = new BlockSensor();
		}

		if (itemGlassesId > 0) {
			Items.glasses = new ItemGlasses();
		}

		ConfigProcessing.registerItems(OpenPeripheralAddons.Items.class, "openperipheral");
		ConfigProcessing.registerBlocks(OpenPeripheralAddons.Blocks.class, "openperipheral");
	}
}
