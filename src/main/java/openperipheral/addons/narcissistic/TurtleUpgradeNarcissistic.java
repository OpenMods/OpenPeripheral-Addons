package openperipheral.addons.narcissistic;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import openperipheral.addons.MetasGeneric;
import openperipheral.addons.ModuleComputerCraft.Icons;
import openperipheral.api.ApiHolder;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;

public class TurtleUpgradeNarcissistic implements ITurtleUpgrade {

	@ApiHolder
	private static IComputerCraftObjectsFactory ccFactory;

	@Override
	public int getUpgradeID() {
		return 181;
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "openperipheral.turtle.narcissistic.adjective";
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
		return ccFactory.createPeripheral(new TurtleInventoryDelegate(turtle));
	}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		return null;
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		return Icons.narcissiticTurtle;
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {}

}
