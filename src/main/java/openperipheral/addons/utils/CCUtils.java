package openperipheral.addons.utils;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;

public final class CCUtils {

	public static Object[] wrap(Object... args) {
		return args;
	}

	public static void addUpgradedTurtles(List<ItemStack> result, ITurtleUpgrade upgrade) {
		// TODO ... or maybe not. Looks for saner solution.
	}

	public static boolean isTurtleValid(ITurtleAccess access) {
		World world = access.getWorld();
		if (world == null) return false;
		BlockPos pos = access.getPosition();
		return world.isBlockLoaded(pos);
	}

}