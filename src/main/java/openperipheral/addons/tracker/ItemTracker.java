package openperipheral.addons.tracker;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import openmods.utils.ItemUtils;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.addons.glasses.TerminalUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTracker extends Item implements ITrackingItem {

	private static final String OWNER_GUID_TAG = "ownerGuid";

	private static final String TRACKER_GUID_TAG = "trackerGuid";

	public ItemTracker() {
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
		setUnlocalizedName("openperipheral.tracker");
	}

	private static Long getGuid(ItemStack stack, String tag) {
		NBTTagCompound itemTag = ItemUtils.getItemTag(stack);
		if (!itemTag.hasKey(tag, Constants.NBT.TAG_ANY_NUMERIC)) return null;
		return itemTag.getLong(tag);
	}

	@Override
	public Long getOwnerGuid(ItemStack stack) {
		return getGuid(stack, OWNER_GUID_TAG);
	}

	@Override
	public long getTrackerGuid(ItemStack stack) {
		NBTTagCompound itemTag = ItemUtils.getItemTag(stack);
		if (!itemTag.hasKey(TRACKER_GUID_TAG, Constants.NBT.TAG_ANY_NUMERIC)) {
			final long id = TerminalUtils.generateGuid();
			itemTag.setLong(TRACKER_GUID_TAG, id);
			return id;
		} else {
			return itemTag.getLong(TRACKER_GUID_TAG);
		}
	}

	@Override
	public void setOwnerGuid(ItemStack stack, long ownerGuid) {
		NBTTagCompound itemTag = ItemUtils.getItemTag(stack);
		itemTag.setLong(OWNER_GUID_TAG, ownerGuid);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		{
			Long guid = getOwnerGuid(itemStack);
			if (guid != null) list.add(StatCollector.translateToLocalFormatted("openperipheral.misc.item_id", TerminalUtils.formatTerminalId(guid)));
		}

		{
			Long guid = getTrackerGuid(itemStack);
			if (guid != null) list.add(StatCollector.translateToLocalFormatted("openperipheral.misc.tracker_id", TerminalUtils.formatTerminalId(guid)));
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotId, boolean isSelected) {
		if (!world.isRemote) {
			Long ownerId = getOwnerGuid(stack);
			if (ownerId != null) {
				final long trackerId = getTrackerGuid(stack);
				MinecraftForge.EVENT_BUS.post(new TrackerTickEvent.InventoryTick(ownerId, trackerId, entity, slotId, isSelected));
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		final World world = entityItem.worldObj;
		if (world != null && !world.isRemote) {
			ItemStack stack = entityItem.getEntityItem();
			Long ownerId = getOwnerGuid(stack);
			if (ownerId != null) {
				final long trackerId = getTrackerGuid(stack);
				MinecraftForge.EVENT_BUS.post(new TrackerTickEvent.EntityItemTick(ownerId, trackerId, entityItem));
			}
		}

		return false;
	}

}
