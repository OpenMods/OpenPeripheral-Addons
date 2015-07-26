package openperipheral.addons.tracker;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class TrackerManager {

	private final TLongObjectHashMap<WeakReference<ITrackerOwner>> owners = new TLongObjectHashMap<WeakReference<ITrackerOwner>>();

	private final TLongObjectHashMap<Tracker> trackers = new TLongObjectHashMap<Tracker>();

	public class ForgeListener {
		@SubscribeEvent
		public void onListenerRegister(TrackerListenerEvent.Register evt) {
			owners.put(evt.guid, new WeakReference<ITrackerOwner>(evt.listener));
		}

		@SubscribeEvent
		public void onListenerUnregister(TrackerListenerEvent.Unregister evt) {
			owners.remove(evt.guid);
		}

		@SubscribeEvent
		public void onTrackerTick(TrackerTickEvent evt) {
			final WeakReference<ITrackerOwner> ownerRef = owners.get(evt.ownerId);
			if (ownerRef == null) return;

			ITrackerOwner owner = ownerRef.get();
			if (owner == null) {
				owners.remove(evt.ownerId);
				return;
			}

			Tracker tracker = trackers.get(evt.trackerId);

			if (tracker == null) {
				tracker = evt.createTracker(owner);
				trackers.put(evt.trackerId, tracker);
				owner.registerTracker(evt.trackerId, tracker);
			} else if (!tracker.handleTick(evt)) {
				tracker = evt.createTracker(owner);
				trackers.put(evt.trackerId, tracker);
				owner.updateTracker(evt.trackerId, tracker);
			}
		}

		@SubscribeEvent
		public void onLivingUpdate(LivingUpdateEvent evt) {
			final EntityLivingBase entityLiving = evt.entityLiving;
			if (!entityLiving.worldObj.isRemote && entityLiving instanceof EntityPlayer) {
				final EntityPlayer player = (EntityPlayer)entityLiving;
				final ItemStack heldStack = player.inventory.getItemStack();
				if (heldStack != null) {
					final Item heldItem = heldStack.getItem();
					if (heldItem instanceof ITrackingItem) {
						final ITrackingItem trackingItem = (ITrackingItem)heldItem;
						final Long ownerGuid = trackingItem.getOwnerGuid(heldStack);
						if (ownerGuid != null) {
							final long trackerGuid = trackingItem.getTrackerGuid(heldStack);
							final TrackerTickEvent.InventoryHeldTick tickEvt = new TrackerTickEvent.InventoryHeldTick(ownerGuid, trackerGuid, player);
							onTrackerTick(tickEvt);
						}
					}
				}
			}
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void onEntityJoin(EntityJoinWorldEvent evt) {
			final Entity entity = evt.entity;
			if (!entity.worldObj.isRemote && entity instanceof EntityItem) {
				final EntityItem entityItem = (EntityItem)entity;
				final ItemStack entityStack = entityItem.getEntityItem();
				if (entityStack != null) {
					final Item item = entityStack.getItem();
					if (item instanceof ITrackingItem) {
						final ITrackingItem trackingItem = (ITrackingItem)item;
						final Long ownerGuid = trackingItem.getOwnerGuid(entityStack);
						if (ownerGuid != null) {
							final long trackerGuid = trackingItem.getTrackerGuid(entityStack);
							final TrackerTickEvent.EntityItemTick tickEvt = new TrackerTickEvent.EntityItemTick(ownerGuid, trackerGuid, entityItem);
							onTrackerTick(tickEvt);
						}
					}
				}
			}
		}

		@SubscribeEvent
		public void onItemExpire(ItemExpireEvent evt) {
			if (shouldPreventDespawn(evt.entityItem)) {
				evt.extraLife += 0x12F58BF;
				evt.setCanceled(true);
			}
		}
	}

	public class FmlListener {

		@SubscribeEvent
		public void onServerTick(ServerTickEvent evt) {
			if (evt.phase == Phase.END) {
				final TLongObjectIterator<Tracker> iterator = trackers.iterator();

				while (iterator.hasNext()) {
					iterator.advance();
					final Tracker value = iterator.value();
					if (!value.checkAlive()) {
						final ITrackerOwner owner = value.owner();
						if (owner != null) owner.unregisterTracker(iterator.key());
						iterator.remove();
					}
				}
			}
		}
	}

	public Object createForgeListener() {
		return new ForgeListener();
	}

	public Object createFmlListener() {
		return new FmlListener();
	}

	private static boolean shouldPreventDespawn(EntityItem entity) {
		ItemStack stack = entity.getEntityItem();

		if (stack == null) return false;
		return stack.getItem() instanceof ItemTracker;
	}

}
