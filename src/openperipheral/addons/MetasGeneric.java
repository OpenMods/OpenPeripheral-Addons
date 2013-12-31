package openperipheral.addons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmods.item.IMetaItem;
import openmods.item.MetaGeneric;
import openperipheral.addons.OpenPeripheralAddons.Items;

public enum MetasGeneric {	
	duckAntenna {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric(MOD_ID, "duckantenna", new ShapedOreRecipe(result, " sl", "sll", "lll", 's', "stickWood", 'l', Item.leather), new ShapedOreRecipe(result, "ls ", "lls", "lll", 's', "stickWood", 'l', Item.leather));
		}
	};
	
	private static final String MOD_ID = "openperipheraladdons";

	public ItemStack newItemStack(int size) {
		return new ItemStack(Items.generic, size, ordinal());
	}

	public ItemStack newItemStack() {
		return new ItemStack(Items.generic, 1, ordinal());
	}

	public boolean isA(ItemStack stack) {
		return (stack.getItem() instanceof ItemOPGeneric) && (stack.getItemDamage() == ordinal());
	}

	protected abstract IMetaItem createMetaItem();

	protected boolean isEnabled() {
		return true;
	}

	public static void registerItems() {
		for (MetasGeneric m : values())
			if (m.isEnabled()) Items.generic.registerItem(m.ordinal(), m.createMetaItem());
	}
}