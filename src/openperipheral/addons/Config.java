package openperipheral.addons;

import java.util.List;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.Configuration;
import openmods.interfaces.BlockId;
import openmods.utils.BlockUtils;
import openmods.utils.ConfigUtils;
import openmods.utils.ItemUtils;
import openperipheral.addons.common.block.BlockGlassesBridge;

public class Config {

	@BlockId(description = "The id of the glasses bridge")
	public static int blockGlassesBridgeId = 3000;
	
	static void readConfig(Configuration configFile) {
		ConfigUtils.processAnnotations(configFile, Config.class);
	}

	public static void register() {
		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		if (BlockUtils.canRegisterBlock(blockGlassesBridgeId)) {
			OpenPeripheralAddons.Blocks.glassesBridge = new BlockGlassesBridge();
		}
		
		ItemUtils.registerItems(OpenPeripheralAddons.Items.class, "openperipheraladdons");
	}
}
