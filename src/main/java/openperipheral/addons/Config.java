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

	@OnLineModifiable
	@ConfigProperty(category = "glasses", comment = "Should glasses listen to all chat (not just prefixed with $$)")
	public static boolean listenToAllChat = true;

	@OnLineModifiable
	@ConfigProperty(category = "glasses", comment = "Minimal difference in mouse position (in pixels) needed before drag event is sent to server")
	public static int minimalDragThreshold = 5;

	@OnLineModifiable
	@ConfigProperty(category = "glasses", comment = "Minimal time (in ticks) between two drag events")
	public static int minimalDragPeriod = 10;

	@OnLineModifiable
	@ConfigProperty(category = "glasses", comment = "Default difference in mouse position (in pixels) needed before drag event is sent to server")
	public static int defaultDragThreshold = 5;

	@OnLineModifiable
	@ConfigProperty(category = "glasses", comment = "Default time (in ticks) between two drag events")
	public static int defaultDragPeriod = 10;

}
