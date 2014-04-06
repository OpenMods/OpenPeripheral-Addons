package openperipheral.addons.narcissistic;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import openperipheral.adapter.PeripheralHandlers;
import openperipheral.addons.MetasGeneric;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.*;

public class TurtleUpgradeNarcissistic implements ITurtleUpgrade {

	@Override
	public int getUpgradeID() {
		return 181;
	}

	@Override
	public String getAdjective() {
		return StatCollector.translateToLocal("openperipheral.turtle.narcissistic.adjective");
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return MetasGeneric.duckAntenna.newItemStack();
	}

	@Override
	public boolean isSecret() {
		return false;
	}

	@Override
	public IHostedPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return PeripheralHandlers.createHostedPeripheralSafe(turtle);
	}

	@Override
	public boolean useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return false;
	}

	@Override
	public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Blocks.sensor.turtleIcon;
	}

}
