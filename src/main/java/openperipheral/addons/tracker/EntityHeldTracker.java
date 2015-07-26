package openperipheral.addons.tracker;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import openperipheral.addons.tracker.TrackerTickEvent.InventoryHeldTick;

public class EntityHeldTracker extends Tracker {

	private final WeakReference<Entity> entity;

	public EntityHeldTracker(long trackerId, ITrackerOwner owner, Entity entity) {
		super(trackerId, owner);
		this.entity = new WeakReference<Entity>(entity);
	}

	@Override
	public boolean isSameState(TrackerTickEvent evt) {
		if (evt instanceof TrackerTickEvent.InventoryHeldTick) {
			TrackerTickEvent.InventoryHeldTick e = (InventoryHeldTick)evt;
			final Entity entity = this.entity.get();
			return e.entity == entity;
		}

		return false;
	}

	@Override
	public String getTrackerState() {
		return "moved_around";
	}

}
