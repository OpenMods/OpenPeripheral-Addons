package openperipheral.addons.tracker;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import openperipheral.addons.tracker.TrackerTickEvent.InventoryTick;

public class EntityInventoryTracker extends Tracker {

	private final WeakReference<Entity> entity;

	private final int slotId;

	private final boolean isSelected;

	public EntityInventoryTracker(long guid, ITrackerOwner owner, Entity entity, int slotId, boolean isSelected) {
		super(guid, owner);
		this.entity = new WeakReference<Entity>(entity);
		this.slotId = slotId;
		this.isSelected = isSelected;
	}

	@Override
	public boolean isSameState(TrackerTickEvent evt) {
		if (evt instanceof TrackerTickEvent.InventoryTick) {
			TrackerTickEvent.InventoryTick e = (InventoryTick)evt;
			final Entity entity = this.entity.get();
			return e.slotId == this.slotId && e.entity == entity && e.isSelected == this.isSelected;
		}

		return false;
	}

	@Override
	public String getTrackerState() {
		return isSelected? "in_inventory_selected" : "in_inventory";
	}

}
