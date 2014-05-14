package openperipheral.addons.sensors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import openperipheral.adapter.PeripheralHandlers;
import openperipheral.addons.OpenPeripheralAddons.Blocks;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;

public class TurtleUpgradeSensor implements ITurtleUpgrade {

	private static class TurtleSensorEnvironment implements ISensorEnvironment {

		private ITurtleAccess turtle;

		public TurtleSensorEnvironment(ITurtleAccess turtle) {
			this.turtle = turtle;
		}

		@Override
		public boolean isTurtle() {
			return true;
		}

		@Override
		public ChunkCoordinates getLocation() {
			return turtle.getPosition();
		}

		@Override
		public World getWorld() {
			return turtle.getWorld();
		}

		@Override
		public int getSensorRange() {
			return 30;
		}

	}

	@Override
	public int getUpgradeID() {
		return 180;
	}

	@Override
	public String getAdjective() {
		return StatCollector.translateToLocal("openperipheral.turtle.sensor.adjective");
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(Blocks.sensor);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return PeripheralHandlers.createAdaptedPeripheralSafe(new TurtleSensorEnvironment(turtle));
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
