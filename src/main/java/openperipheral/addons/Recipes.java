package openperipheral.addons;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openmods.Mods;
import cpw.mods.fml.common.Loader;

public class Recipes {

	public static void register() {
		final ItemStack duckAntenna = MetasGeneric.duckAntenna.newItemStack();

		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		if (OpenPeripheralAddons.Blocks.pim != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Blocks.pim, "ooo", "rcr", 'o', Blocks.obsidian, 'r', Items.redstone, 'c', Blocks.chest));
		}

		if (OpenPeripheralAddons.Blocks.sensor != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Blocks.sensor, "ooo", " w ", "sss", 'o', Blocks.obsidian, 'w', "stickWood", 's', Blocks.stone_slab));
		}

		if (OpenPeripheralAddons.Blocks.glassesBridge != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Blocks.glassesBridge, "sas", "ses", "srs", 's', Blocks.stone, 'r', Blocks.redstone_block, 'e', Items.ender_pearl, 'a', duckAntenna.copy()));
		}

		if (OpenPeripheralAddons.Blocks.selector != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Blocks.selector, "sss", "scs", "sgs", 's', Blocks.stone, 'c', Blocks.trapped_chest, 'g', Blocks.glass_pane));
		}

		if (OpenPeripheralAddons.Items.glasses != null) {
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Items.glasses, "igi", "aei", "prp", 'g', Blocks.glowstone, 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'p', Blocks.glass_pane, 'r', Items.redstone, 'a', duckAntenna.copy()));
			recipeList.add(new ShapedOreRecipe(OpenPeripheralAddons.Items.glasses, "igi", "iea", "prp", 'g', Blocks.glowstone, 'i', Items.iron_ingot, 'e', Items.ender_pearl, 'p', Blocks.glass_pane, 'r', Items.redstone, 'a', duckAntenna.copy()));
		}

		if (OpenPeripheralAddons.Items.keyboard != null) {
			recipeList.add(new ShapelessOreRecipe(OpenPeripheralAddons.Items.keyboard, duckAntenna.copy(), Items.bone, Items.redstone, Items.ender_pearl, Items.slime_ball));
		}

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.registerRecipes(recipeList);
		if (Loader.isModLoaded(Mods.RAILCRAFT)) ModuleRailcraft.registerRecipes(recipeList);

	}
}
