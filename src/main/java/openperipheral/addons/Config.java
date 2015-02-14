package openperipheral.addons;

import openmods.config.properties.ConfigProperty;
import openmods.config.properties.OnLineModifiable;

public class Config {
	@OnLineModifiable
	@ConfigProperty(category = "sensor", name = "rangeInStorm")
	public static int sensorRangeInStorm = 5;

	@OnLineModifiable
	@ConfigProperty(category = "sensor", name = "normalRange")
	public static int sensorRange = 5;

	@OnLineModifiable
	@ConfigProperty(category = "misc", comment = "Should turtles with OPA updates be visible in creative")
	public static boolean addTurtlesToCreative = true;

}
