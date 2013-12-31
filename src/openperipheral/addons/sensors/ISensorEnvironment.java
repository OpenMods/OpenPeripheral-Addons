package openperipheral.addons.sensors;

import net.minecraft.util.Vec3;
import openperipheral.api.IWorldProvider;

public interface ISensorEnvironment extends IWorldProvider {
	public boolean isTurtle();

	public Vec3 getLocation();

	public int getSensorRange();
}
