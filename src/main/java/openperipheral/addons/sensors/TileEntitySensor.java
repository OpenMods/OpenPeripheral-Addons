package openperipheral.addons.sensors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import openperipheral.addons.Config;
import openperipheral.util.PeripheralUtils;

public class TileEntitySensor extends TileEntity implements ISensorEnvironment {

	private static final int ROTATION_SPEED = 3;

	private int rotation;

	public TileEntitySensor() {}

	public int getRotation() {
		return rotation;
	}

	@Override
	public void updateEntity() {
		rotation = (rotation + ROTATION_SPEED) % 360;
	}

	@Override
	public boolean isTurtle() {
		return false;
	}

	@Override
	public ChunkCoordinates getLocation() {
		return new ChunkCoordinates(xCoord, yCoord, zCoord);
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public int getSensorRange() {
		return (getWorld().isRaining() && getWorld().isThundering())? Config.sensorRangeInStorm : Config.sensorRange;
	}

	@Override
	public boolean isValid() {
		return PeripheralUtils.isTileEntityValid(this);
	}

}
