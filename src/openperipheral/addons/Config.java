package openperipheral.addons;

import net.minecraftforge.common.Configuration;
import openmods.config.BlockId;
import openmods.config.ConfigProcessing;
import openmods.config.ItemId;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import openperipheral.addons.OpenPeripheralAddons.Items;
import operperipheral.addons.glasses.BlockGlassesBridge;
import operperipheral.addons.glasses.ItemGlasses;

public class Config {

	@ItemId(description = "The id of the glasses")
	public static int itemGlassesId = 9258;

	@BlockId(description = "The id of the glasses bridge")
	public static int blockGlassesBridgeId = 3000;

	public static void readConfig(Configuration configFile) {
		ConfigProcessing.processAnnotations(configFile, Config.class);
	}

	public static void register() {
		if (ConfigProcessing.canRegisterBlock(blockGlassesBridgeId)) {
			Blocks.glassesBridge = new BlockGlassesBridge();
		}

		if (itemGlassesId > 0) {
			Items.glasses = new ItemGlasses();
		}

		ConfigProcessing.registerItems(OpenPeripheralAddons.Items.class, "openperipheral");
		ConfigProcessing.registerBlocks(OpenPeripheralAddons.Blocks.class, "openperipheral");
	}
}
