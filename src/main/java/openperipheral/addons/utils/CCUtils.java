package openperipheral.addons.utils;

import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import openmods.Mods;

public final class CCUtils {
	private static final int NUMBER_OF_TURTLE_TOOLS = 7;

	public static Object[] wrap(Object... args) {
		return args;
	}

	public static ItemStack getExpandedTurtleItemStack() {
		return GameRegistry.findItemStack(Mods.COMPUTERCRAFT, "CC-TurtleExpanded", 1);
	}

	public static ItemStack getAdvancedTurtleItemStack() {
		return GameRegistry.findItemStack(Mods.COMPUTERCRAFT, "CC-TurtleAdvanced", 1);
	}

	public static void createTurtleItemStack(List<ItemStack> result, boolean isAdvanced, Short left, Short right) {
		ItemStack turtle = isAdvanced? getAdvancedTurtleItemStack() : getExpandedTurtleItemStack();

		if (turtle == null) return;

		NBTTagCompound tag = turtle.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			turtle.setTagCompound(tag);
		}

		if (left != null) tag.setShort("leftUpgrade", left);

		if (right != null) tag.setShort("rightUpgrade", right);

		result.add(turtle);
	}

	private static void addUpgradedTurtles(List<ItemStack> result, ITurtleUpgrade upgrade, boolean isAdvanced) {
		short upgradeId = (short)upgrade.getUpgradeID();
		createTurtleItemStack(result, isAdvanced, upgradeId, null);
		for (int i = 1; i < NUMBER_OF_TURTLE_TOOLS; i++)
			createTurtleItemStack(result, isAdvanced, upgradeId, (short)i);
	}

	public static void addUpgradedTurtles(List<ItemStack> result, ITurtleUpgrade upgrade) {
		addUpgradedTurtles(result, upgrade, false);
		addUpgradedTurtles(result, upgrade, true);
	}

	public static boolean isTurtleValid(ITurtleAccess access) {
		World world = access.getWorld();
		if (world == null) return false;
		ChunkCoordinates coords = access.getPosition();
		return world.blockExists(coords.posX, coords.posY, coords.posZ);
	}

}