package openperipheral.addons.tracker;

import java.lang.ref.WeakReference;

import openmods.Log;

public abstract class Tracker {
	private static final int MISSED_TICKS_THRESHOLD = 5;

	private int missedTicks;

	private final long guid;

	private final WeakReference<ITrackerOwner> owner;

	public Tracker(long guid, ITrackerOwner owner) {
		this.guid = guid;
		this.owner = new WeakReference<ITrackerOwner>(owner);
	}

	public ITrackerOwner owner() {
		return owner.get();
	}

	public long guid() {
		return guid;
	}

	protected void markTicked() {
		this.missedTicks = 0;
	}

	public boolean checkAlive() {
		this.missedTicks++;
		if (missedTicks > 1) {
			Log.info("Missed ticks: %d", missedTicks);
		}
		return this.missedTicks < MISSED_TICKS_THRESHOLD;
	}

	public final boolean handleTick(TrackerTickEvent evt) {
		final boolean isSameState = isSameState(evt);
		this.missedTicks = 0;
		return isSameState;
	}

	protected abstract boolean isSameState(TrackerTickEvent evt);

	public abstract String getTrackerState();
}