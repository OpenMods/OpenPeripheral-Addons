package openperipheral.addons.glasses;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import openmods.utils.ItemUtils;
import openperipheral.addons.glasses.TerminalIdAccess.HandSetterAdapter;
import openperipheral.addons.glasses.TerminalIdAccess.HelmetGetterAdapter;

import com.google.common.base.Optional;

public class NbtGuidProviders {

	private static final String MAIN_TAG = "OPA-Terminal";

	private static final String GUID_TAG = "Guid";

	public static class NbtGetter extends HelmetGetterAdapter {
		@Override
		public Optional<Long> getFor(ItemStack stack) {
			return getTerminalGuid(stack);
		}
	}

	public static class NbtSetter extends HandSetterAdapter {
		@Override
		public boolean setFor(ItemStack stack, long guid) {
			final NBTTagCompound itemTag = stack.getTagCompound();
			if (itemTag != null && itemTag.hasKey(MAIN_TAG, Constants.NBT.TAG_COMPOUND)) {
				final NBTTagCompound terminalTag = itemTag.getCompoundTag(MAIN_TAG);
				terminalTag.setLong(GUID_TAG, guid);
				return true;
			}

			return false;
		}
	}

	public static NBTTagCompound getOrCreateTerminalTag(ItemStack stack) {
		final NBTTagCompound itemTag = ItemUtils.getItemTag(stack);
		if (!itemTag.hasKey(MAIN_TAG, Constants.NBT.TAG_COMPOUND)) {
			final NBTTagCompound result = new NBTTagCompound();
			itemTag.setTag(MAIN_TAG, result);
			return result;
		} else {
			return itemTag.getCompoundTag(MAIN_TAG);
		}
	}

	public static boolean hasTerminalCapabilities(ItemStack stack) {
		final NBTTagCompound itemTag = stack.getTagCompound();
		if (itemTag == null) return false;
		return itemTag.hasKey(MAIN_TAG, Constants.NBT.TAG_COMPOUND);
	}

	public static Optional<Long> getTerminalGuid(ItemStack stack) {
		final NBTTagCompound itemTag = stack.getTagCompound();
		if (itemTag != null && itemTag.hasKey(MAIN_TAG, Constants.NBT.TAG_COMPOUND)) {
			final NBTTagCompound terminalTag = itemTag.getCompoundTag(MAIN_TAG);
			if (terminalTag.hasKey(GUID_TAG, Constants.NBT.TAG_ANY_NUMERIC)) return Optional.of(terminalTag.getLong(GUID_TAG));
		}

		return Optional.absent();
	}

	public static void setTerminalGuid(ItemStack stack, Optional<Long> guid) {
		final NBTTagCompound terminalTag = getOrCreateTerminalTag(stack);
		if (guid.isPresent()) terminalTag.setLong(GUID_TAG, guid.get());
	}

}
