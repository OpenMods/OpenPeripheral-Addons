package openperipheral.addons.sensors;

import net.minecraft.util.ChunkCoordinates;
import openperipheral.api.IWorldProvider;

public interface ISensorEnvironment extends IWorldProvider {
	public boolean isTurtle();

	public ChunkCoordinates getLocation();

	public int getSensorRange();
}
