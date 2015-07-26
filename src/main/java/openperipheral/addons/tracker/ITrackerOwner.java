package openperipheral.addons.tracker;

public interface ITrackerOwner {
	public void registerTracker(long id, Tracker tracker);

	public void updateTracker(long id, Tracker tracker);

	public void unregisterTracker(long id);

}
