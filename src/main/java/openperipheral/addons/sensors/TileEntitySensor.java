package openperipheral.addons.sensors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openmods.utils.WorldUtils;
import openperipheral.addons.Config;

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
	public Vec3 getLocation() {
		return Vec3.createVectorHelper(xCoord, yCoord, zCoord);
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public int getSensorRange() {
		final World world = getWorld();
		return (world.isRaining() && world.isThundering())? Config.sensorRangeInStorm : Config.sensorRange;
	}

	@Override
	public boolean isValid() {
		return WorldUtils.isTileEntityValid(this);
	}

}
