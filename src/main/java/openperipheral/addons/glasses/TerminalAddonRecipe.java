package openperipheral.addons.glasses;

import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import openmods.utils.InventoryUtils;

import com.google.common.base.Optional;

public class TerminalAddonRecipe implements IRecipe {

	private static boolean isSuitableItem(ItemStack itemStack) {
		return EntityLiving.getArmorPosition(itemStack) == 4 &&
				itemStack.stackSize == 1 &&
				!NbtGuidProviders.hasTerminalCapabilities(itemStack);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean glassesFound = false;
		boolean targetFound = false;

		for (ItemStack itemStack : InventoryUtils.asIterable(inv)) {
			if (itemStack != null) {
				if (itemStack.getItem() instanceof ItemGlasses) {
					if (glassesFound) return false;
					glassesFound = true;
				} else if (isSuitableItem(itemStack)) {
					if (targetFound) return false;
					targetFound = true;
				} else return false;
			}
		}

		return glassesFound && targetFound;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack targetStack = null;
		ItemStack glassesStack = null;

		for (ItemStack itemStack : InventoryUtils.asIterable(inv)) {
			if (itemStack != null) {
				if (itemStack.getItem() instanceof ItemGlasses) {
					if (glassesStack != null) return null;
					glassesStack = itemStack;
				} else if (isSuitableItem(itemStack)) {
					if (targetStack != null) return null;
					targetStack = itemStack;
				} else return null;
			}
		}

		if (glassesStack == null || targetStack == null) return null;

		final ItemGlasses glassesItem = (ItemGlasses)glassesStack.getItem();
		Optional<Long> guid = Optional.fromNullable(glassesItem.getTerminalGuid(glassesStack));

		final ItemStack result = targetStack.copy();
		NbtGuidProviders.setTerminalGuid(result, guid);

		return result;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[inv.getSizeInventory()];
	}

}
