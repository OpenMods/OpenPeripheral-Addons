package openperipheral.addons.tracker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import cpw.mods.fml.common.eventhandler.Event;

public class TrackerTickEvent extends Event {

	public final long ownerId;

	public final long trackerId;

	public TrackerTickEvent(long ownerId, long trackerId) {
		this.ownerId = ownerId;
		this.trackerId = trackerId;
	}

	public static class InventoryTick extends TrackerTickEvent {

		public final Entity entity;

		public final int slotId;

		public final boolean isSelected;

		public InventoryTick(long ownerId, long trackerId, Entity entity, int slotId, boolean isSelected) {
			super(ownerId, trackerId);
			this.entity = entity;
			this.slotId = slotId;
			this.isSelected = isSelected;
		}

		@Override
		public Tracker createTracker(ITrackerOwner owner) {
			return new EntityInventoryTracker(trackerId, owner, entity, slotId, isSelected);
		}
	}

	public static class InventoryHeldTick extends TrackerTickEvent {

		public final Entity entity;

		public InventoryHeldTick(long ownerId, long trackerId, Entity entity) {
			super(ownerId, trackerId);
			this.entity = entity;
		}

		@Override
		public Tracker createTracker(ITrackerOwner owner) {
			return new EntityHeldTracker(trackerId, owner, entity);
		}
	}

	public static class EntityItemTick extends TrackerTickEvent {

		public final EntityItem entity;

		public EntityItemTick(long ownerId, long trackerId, EntityItem entity) {
			super(ownerId, trackerId);
			this.entity = entity;
		}

		@Override
		public Tracker createTracker(ITrackerOwner owner) {
			return new EntityItemTracker(trackerId, owner, entity);
		}

	}

	public Tracker createTracker(ITrackerOwner owner) {
		// This event shouldn't be used, only sub-classes should implement this method.
		// But I can't make it abstract since EventBus must be able to instantiate at least one instance.
		throw new UnsupportedOperationException();
	}

}
