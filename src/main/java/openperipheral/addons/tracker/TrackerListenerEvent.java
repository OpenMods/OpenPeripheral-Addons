package openperipheral.addons.tracker;

import cpw.mods.fml.common.eventhandler.Event;

public abstract class TrackerListenerEvent extends Event {

	public final long guid;

	public final ITrackerOwner listener;

	public TrackerListenerEvent(long guid, ITrackerOwner listener) {
		this.guid = guid;
		this.listener = listener;
	}

	public static class Register extends TrackerListenerEvent {
		public Register(long guid, ITrackerOwner listener) {
			super(guid, listener);
		}
	}

	public static class Unregister extends TrackerListenerEvent {
		public Unregister(long guid, ITrackerOwner listener) {
			super(guid, listener);
			// TODO Auto-generated constructor stub
		}
	}

}
