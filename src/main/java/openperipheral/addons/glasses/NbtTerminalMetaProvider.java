package openperipheral.addons.glasses;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.meta.IItemStackCustomMetaProvider;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class NbtTerminalMetaProvider implements IItemStackCustomMetaProvider<Item> {

	@Override
	public Class<? extends Item> getTargetClass() {
		return Item.class;
	}

	@Override
	public String getKey() {
		return "op_terminal_embedded";
	}

	@Override
	public boolean canApply(Item target, ItemStack stack) {
		return NbtGuidProviders.hasTerminalCapabilities(stack);
	}

	@Override
	public Object getMeta(Item target, ItemStack stack) {
		Map<String, Object> results = Maps.newHashMap();
		final Optional<Long> terminalGuid = NbtGuidProviders.getTerminalGuid(stack);
		if (terminalGuid.isPresent()) results.put("bridge_name", TerminalUtils.formatTerminalId(terminalGuid.get()));
		return results;
	}

}
