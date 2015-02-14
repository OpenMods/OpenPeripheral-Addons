package openperipheral.addons;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmods.config.BlockInstances;
import openmods.config.game.ModStartupHelper;
import openmods.config.game.RegisterBlock;
import openperipheral.addons.ticketmachine.BlockTicketMachine;
import openperipheral.addons.ticketmachine.TileEntityTicketMachine;

public class ModuleRailcraft {

	public static class Blocks implements BlockInstances {
		@RegisterBlock(name = "ticketmachine", tileEntity = TileEntityTicketMachine.class)
		public static BlockTicketMachine ticketMachine;
	}

	public static void preInit(ModStartupHelper startupHelper) {
		startupHelper.registerBlocksHolder(Blocks.class);
	}

	public static void registerRecipes(List<IRecipe> recipeList) {
		if (Blocks.ticketMachine != null) {
			recipeList.add(new ShapedOreRecipe(Blocks.ticketMachine, "iii", "iii", "igi", 'i', Items.iron_ingot, 'g', net.minecraft.init.Blocks.glass_pane));
		}
	}
}
