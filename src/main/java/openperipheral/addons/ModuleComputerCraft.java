package openperipheral.addons;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmods.config.BlockInstances;
import openmods.config.game.ModStartupHelper;
import openmods.config.game.RegisterBlock;
import openperipheral.addons.narcissistic.TurtleUpgradeNarcissistic;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.peripheralproxy.TileEntityPeripheralProxy;
import openperipheral.addons.sensors.TurtleUpgradeSensor;
import openperipheral.addons.utils.CCUtils;
import dan200.computercraft.api.ComputerCraftAPI;

public class ModuleComputerCraft {

	public static class Blocks implements BlockInstances {
		@RegisterBlock(name = "peripheralproxy", tileEntity = TileEntityPeripheralProxy.class)
		public static BlockPeripheralProxy peripheralProxy;
	}

	private static TurtleUpgradeSensor sensorUpgrade;

	private static TurtleUpgradeNarcissistic narcissiticUpgrade;

	public static void preInit(ModStartupHelper helper) {
		helper.registerBlocksHolder(Blocks.class);

		sensorUpgrade = new TurtleUpgradeSensor();
		ComputerCraftAPI.registerTurtleUpgrade(sensorUpgrade);

		narcissiticUpgrade = new TurtleUpgradeNarcissistic();
		ComputerCraftAPI.registerTurtleUpgrade(narcissiticUpgrade);
	}

	public static void init() {
		if (Blocks.peripheralProxy != null) TileEntityPeripheralProxy.initAccess();
	}

	public static void registerRecipes(List<IRecipe> recipeList) {
		if (Blocks.peripheralProxy != null) {
			recipeList.add(new ShapedOreRecipe(Blocks.peripheralProxy, "iri", "iii", "iri", 'i', Items.iron_ingot, 'r', Items.redstone));
		}
	}

	public static void listSensorTurtles(List<ItemStack> result) {
		CCUtils.addUpgradedTurtles(result, sensorUpgrade);
	}

	public static void listNarcissisticTurtles(List<ItemStack> result) {
		CCUtils.addUpgradedTurtles(result, narcissiticUpgrade);
	}

}
