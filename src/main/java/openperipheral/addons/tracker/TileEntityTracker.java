package openperipheral.addons.tracker;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.Log;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomHarvestDrops;
import openmods.api.IPlaceAwareTile;
import openmods.utils.ItemUtils;
import openperipheral.addons.glasses.TerminalUtils;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class TileEntityTracker extends TileEntity implements ITrackerOwner, IAttachable, ICustomHarvestDrops, IPlaceAwareTile, IActivateAwareTile {

	public static final String TAG_GUID = "guid";

	private Optional<Long> guid = Optional.absent();

	private Set<IArchitectureAccess> computers = Sets.newIdentityHashSet();

	@Override
	public void invalidate() {
		super.invalidate();
		if (guid.isPresent()) unregisterOwner();
	}

	private void registerOwner() {
		MinecraftForge.EVENT_BUS.post(new TrackerListenerEvent.Register(guid.get(), this));
	}

	private void unregisterOwner() {
		MinecraftForge.EVENT_BUS.post(new TrackerListenerEvent.Unregister(guid.get(), this));
	}

	private static long readGuid(NBTTagCompound tag) {
		if (tag != null && tag.hasKey(TAG_GUID, Constants.NBT.TAG_ANY_NUMERIC)) return tag.getLong(TAG_GUID);
		return TerminalUtils.generateGuid();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (guid.isPresent()) tag.setLong(TAG_GUID, guid.get());
	}

	private void setGuid(long newGuid) {
		if (this.guid.isPresent()) unregisterOwner();

		this.guid = Optional.of(newGuid);
		registerOwner();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		setGuid(readGuid(tag));
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		NBTTagCompound tag = stack.getTagCompound();
		setGuid(readGuid(tag));
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops) {
		ItemStack result = new ItemStack(getBlockType());
		if (guid.isPresent()) {
			NBTTagCompound tag = ItemUtils.getItemTag(result);
			tag.setLong(TAG_GUID, guid.get());
		}
		drops.add(result);
	}

	@Override
	public boolean suppressNormalHarvestDrops() {
		return true;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (guid.isPresent()) {
			ItemStack heldStack = player.getHeldItem();
			if (heldStack != null) {
				Item heldItem = heldStack.getItem();
				if (heldItem instanceof ITrackingItem) {
					((ITrackingItem)heldItem).setOwnerGuid(heldStack, guid.get());
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void addComputer(IArchitectureAccess computer) {
		if (!computers.contains(computer)) {
			computers.add(computer);
		}
	}

	@Override
	public void removeComputer(IArchitectureAccess computer) {
		computers.remove(computer);
	}

	@Override
	public void registerTracker(long id, Tracker tracker) {
		Log.info("register: %d %s", id, tracker.getTrackerState());
	}

	@Override
	public void updateTracker(long id, Tracker tracker) {
		Log.info("update: %d %s", id, tracker.getTrackerState());
	}

	@Override
	public void unregisterTracker(long id) {
		Log.info("unregister: %s", id);
	}
}
