package openperipheral.addons.glasses;

import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.addons.api.ITerminalItem;
import openperipheral.api.helpers.ItemStackMetaProviderSimple;

import com.google.common.collect.Maps;

public class ItemTerminalMetaProvider extends ItemStackMetaProviderSimple<ITerminalItem> {

	@Override
	public String getKey() {
		return "op-terminal";
	}

	@Override
	public Object getMeta(ITerminalItem target, ItemStack stack) {
		Map<String, Object> results = Maps.newHashMap();
		final Long terminalGuid = target.getTerminalGuid(stack);
		if (terminalGuid != null) results.put("bridge-name", TerminalUtils.formatTerminalId(terminalGuid));
		return results;
	}
}
