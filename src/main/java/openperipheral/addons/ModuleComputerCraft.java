package openperipheral.addons;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmods.config.BlockInstances;
import openmods.config.game.ModStartupHelper;
import openmods.config.game.RegisterBlock;
import openmods.integration.modules.ComputerCraftUtils;
import openmods.renderer.CustomModelLoader;
import openperipheral.addons.narcissistic.TurtleUpgradeNarcissistic;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.peripheralproxy.TileEntityPeripheralProxy;
import openperipheral.addons.sensors.TurtleUpgradeSensor;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.TurtleSide;

public class ModuleComputerCraft {

	public static class Blocks implements BlockInstances {
		@RegisterBlock(name = "peripheralproxy", tileEntity = TileEntityPeripheralProxy.class)
		public static BlockPeripheralProxy peripheralProxy;
	}

	private static final TurtleUpgradeSensor sensorUpgrade = new TurtleUpgradeSensor();

	private static final ResourceLocation sensorUpgradeLeftModel = OpenPeripheralAddons.location("turtles/sensor_left");
	private static final ResourceLocation sensorUpgradeRightModel = OpenPeripheralAddons.location("turtles/sensor_right");

	private static final TurtleUpgradeNarcissistic narcissiticUpgrade = new TurtleUpgradeNarcissistic();

	private static final ResourceLocation narcissiticUpgradeLeftModel = OpenPeripheralAddons.location("turtles/narcissistic_left");
	private static final ResourceLocation narcissiticUpgradeRightModel = OpenPeripheralAddons.location("turtles/narcissistic_right");

	public static void preInit(ModStartupHelper helper) {
		helper.registerBlocksHolder(Blocks.class);

		ComputerCraftAPI.registerTurtleUpgrade(sensorUpgrade);
		ComputerCraftAPI.registerTurtleUpgrade(narcissiticUpgrade);
	}

	public static void clientPreInit() {
		CustomModelLoader.instance.registerModel(sensorUpgradeLeftModel, "inventory");
		CustomModelLoader.instance.registerModel(sensorUpgradeRightModel, "inventory");

		CustomModelLoader.instance.registerModel(narcissiticUpgradeLeftModel, "inventory");
		CustomModelLoader.instance.registerModel(narcissiticUpgradeRightModel, "inventory");
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
		ComputerCraftUtils.addUpgradedTurtles(result, sensorUpgrade.getCraftingItem());
	}

	public static ResourceLocation getSensorTurtleModel(TurtleSide side) {
		return side == TurtleSide.Left? sensorUpgradeLeftModel : sensorUpgradeRightModel;
	}

	public static void listNarcissisticTurtles(List<ItemStack> result) {
		ComputerCraftUtils.addUpgradedTurtles(result, narcissiticUpgrade.getCraftingItem());
	}

	public static ResourceLocation getNarcissisticTurtleModel(TurtleSide side) {
		return side == TurtleSide.Left? narcissiticUpgradeLeftModel : narcissiticUpgradeRightModel;
	}

}
