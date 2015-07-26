package openperipheral.addons.tracker;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import openperipheral.addons.tracker.TrackerTickEvent.EntityItemTick;

public class EntityItemTracker extends Tracker {

	private final WeakReference<EntityItem> entityItem;

	public EntityItemTracker(long trackerId, ITrackerOwner owner, EntityItem entityItem) {
		super(trackerId, owner);
		this.entityItem = new WeakReference<EntityItem>(entityItem);
	}

	@Override
	public boolean isSameState(TrackerTickEvent evt) {
		if (evt instanceof TrackerTickEvent.EntityItemTick) {
			TrackerTickEvent.EntityItemTick e = (EntityItemTick)evt;
			final Entity entity = this.entityItem.get();
			return e.entity == entity;
		}

		return false;
	}

	@Override
	public String getTrackerState() {
		return "on_ground";
	}

}
