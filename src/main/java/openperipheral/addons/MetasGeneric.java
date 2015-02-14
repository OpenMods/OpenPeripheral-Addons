package openperipheral.addons;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openmods.Mods;
import openmods.item.IMetaItem;
import cpw.mods.fml.common.Loader;

public enum MetasGeneric {
	duckAntenna {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("duckantenna",
					new ShapelessOreRecipe(result, Items.redstone, Items.redstone, Items.iron_ingot, Items.slime_ball)) {
				@Override
				public void addToCreativeList(Item item, int meta, List<ItemStack> result) {
					super.addToCreativeList(item, meta, result);
					if (Config.addTurtlesToCreative && Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.listNarcissisticTurtles(result);
				}
			};
		}
	};

	public ItemStack newItemStack(int size) {
		return new ItemStack(OpenPeripheralAddons.Items.generic, size, ordinal());
	}

	public ItemStack newItemStack() {
		return new ItemStack(OpenPeripheralAddons.Items.generic, 1, ordinal());
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
			if (m.isEnabled()) OpenPeripheralAddons.Items.generic.registerItem(m.ordinal(), m.createMetaItem());
	}
}