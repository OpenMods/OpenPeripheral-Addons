package openperipheral.addons.narcissistic;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import openperipheral.adapter.PeripheralHandlers;
import openperipheral.addons.MetasGeneric;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;

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
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return PeripheralHandlers.createPeripheral(turtle);
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return null;
	}

	@Override
	public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Blocks.sensor.turtleIcon;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {}

}
