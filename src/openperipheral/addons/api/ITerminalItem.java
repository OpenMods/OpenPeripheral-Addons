package openperipheral.addons.api;

import net.minecraft.item.ItemStack;

public interface ITerminalItem {
	public Long getTerminalGuid(ItemStack stack);

	public void bindToTerminal(ItemStack stack, long guid);
}
