package openperipheral.addons.api;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface ISensorEnvironment {
	public Vec3 getLocation();

	public int getSensorRange();

	public World getSensorWorld();
}
