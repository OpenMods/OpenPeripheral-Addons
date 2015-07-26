package openperipheral.addons.tracker;

import net.minecraft.item.ItemStack;

public interface ITrackingItem {

	public Long getOwnerGuid(ItemStack stack);

	public long getTrackerGuid(ItemStack stack);

	public void setOwnerGuid(ItemStack stack, long ownerGuid);

}
