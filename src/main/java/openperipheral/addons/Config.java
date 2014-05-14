package openperipheral.addons;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmods.Mods;
import openmods.config.*;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import openperipheral.addons.OpenPeripheralAddons.Items;
import openperipheral.addons.glasses.BlockGlassesBridge;
import openperipheral.addons.glasses.ItemGlasses;
import openperipheral.addons.peripheralproxy.BlockPeripheralProxy;
import openperipheral.addons.pim.BlockPIM;
import openperipheral.addons.sensors.BlockSensor;
import openperipheral.addons.ticketmachine.BlockTicketMachine;
import cpw.mods.fml.common.Loader;

public class Config {

	@ItemId(description = "The id of the generic item")
	public static int itemGenericId = 9257;

	@ItemId(description = "The id of the glasses")
	public static int itemGlassesId = 9258;

	@BlockId(description = "The id of the glasses bridge")
	public static int blockGlassesBridgeId = 3000;

	@BlockId(description = "The id of the peripheral proxy")
	public static int blockPeripheralProxyId = 3001;

	@BlockId(description = "The id of the pim block")
	public static int blockPIMId = 3002;

	@BlockId(description = "The id of the sensor block")
	public static int blockSensorId = 3003;

	@BlockId(description = "The id of the ticket machine block")
	public static int blockTicketMachineId = 3004;

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

		if (ConfigProcessing.canRegisterBlock(blockGlassesBridgeId)) {
			Blocks.glassesBridge = new BlockGlassesBridge();
			// bridge recipe added in postinit
		}

		if (ConfigProcessing.canRegisterBlock(blockPeripheralProxyId)) {
			Blocks.peripheralProxy = new BlockPeripheralProxy();
			recipeList.add(new ShapedOreRecipe(Blocks.peripheralProxy, "iri", "iii", "iri", 'i', Item.ingotIron, 'r', Item.redstone));
		}

		if (ConfigProcessing.canRegisterBlock(blockPIMId)) {
			Blocks.pim = new BlockPIM();
			recipeList.add(new ShapedOreRecipe(Blocks.pim, "ooo", "rcr", 'o', Block.obsidian, 'r', Item.redstone, 'c', Block.chest));
		}

		if (ConfigProcessing.canRegisterBlock(blockSensorId)) {
			Blocks.sensor = new BlockSensor();
			recipeList.add(new ShapedOreRecipe(Blocks.sensor, "ooo", " w ", "sss", 'o', Block.obsidian, 'w', "stickWood", 's', Block.stoneSingleSlab));
		}

		if (Loader.isModLoaded(Mods.RAILCRAFT) && ConfigProcessing.canRegisterBlock(blockTicketMachineId)) {
			Blocks.ticketMachine = new BlockTicketMachine();
			recipeList.add(new ShapedOreRecipe(Blocks.ticketMachine, "iii", "iii", "igi", 'i', Item.ingotIron, 'g', Block.thinGlass));
		}

		if (itemGlassesId > 0) {
			Items.glasses = new ItemGlasses();
			// recipe added in postinit
		}

		Items.generic = new ItemOPGeneric();
		MetasGeneric.registerItems();

		ConfigProcessing.registerItems(OpenPeripheralAddons.Items.class, "openperipheral");
		ConfigProcessing.registerBlocks(OpenPeripheralAddons.Blocks.class, "openperipheral");
	}
}
