package openperipheral.addons;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmods.config.properties.ConfigProperty;
import openmods.config.properties.OnLineModifiable;

public class Config {
	@OnLineModifiable
	@ConfigProperty(category = "sensor", name = "rangeInStorm")
	public static int sensorRangeInStorm = 5;

	@OnLineModifiable
	@ConfigProperty(category = "sensor", name = "normalRange")
	public static int sensorRange = 5;

	@OnLineModifiable
	@ConfigProperty(category = "misc", comment = "Should turtles with OPA updates be visible in creative")
	public static boolean addTurtlesToCreative = true;

	public static void register() {

		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		if (OpenPeripheralAddons.Blocks.peripheralProxy != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Blocks.peripheralProxy, "iri", "iii", "iri", 'i', Items.iron_ingot, 'r', Items.redstone));
		}

		if (OpenPeripheralAddons.Blocks.pim != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Blocks.pim, "ooo", "rcr", 'o', Blocks.obsidian, 'r', Items.redstone, 'c', Blocks.chest));
		}

		if (OpenPeripheralAddons.Blocks.sensor != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Blocks.sensor, "ooo", " w ", "sss", 'o', Blocks.obsidian, 'w', "stickWood", 's', Blocks.stone_slab));
		}

		if (OpenPeripheralAddons.Blocks.ticketMachine != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Blocks.ticketMachine, "iii", "iii", "igi", 'i', Items.iron_ingot, 'g', Blocks.glass_pane));
		}

		MetasGeneric.registerItems();
	}
}
