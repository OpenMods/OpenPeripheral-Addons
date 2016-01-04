package openperipheral.addons.glasses;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.utils.ItemUtils;
import openmods.utils.MiscUtils;
import openperipheral.addons.api.ITerminalItem;

public class ItemGlasses extends ItemArmor implements ITerminalItem {
	private static final String OPENP_TAG = "openp";

	public ItemGlasses() {
		super(ArmorMaterial.CHAIN, 0, 0);
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
	}

	private static Long extractGuid(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		if (!tag.hasKey(OPENP_TAG)) return null;

		NBTTagCompound openp = tag.getCompoundTag(OPENP_TAG);
		return TerminalUtils.extractGuid(openp);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
		Long guid = extractGuid(itemStack);
		if (guid != null) list.add(StatCollector.translateToLocalFormatted("openperipheral.misc.key", TerminalUtils.formatTerminalId(guid)));
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		switch (MiscUtils.getHoliday()) {
			case 1:
				return "openperipheral:textures/models/glasses_valentines.png";
			case 2:
				return "openperipheral:textures/models/glasses_halloween.png";
			case 3:
				return "openperipheral:textures/models/glasses_christmas.png";
			default:
				return "openperipheral:textures/models/glasses.png";
		}
	}

	@Override
	public Long getTerminalGuid(ItemStack stack) {
		return extractGuid(stack);
	}

	@Override
	public void bindToTerminal(ItemStack stack, long guid) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		NBTTagCompound openPTag = (NBTTagCompound)tag.getTag(OPENP_TAG);
		if (openPTag == null) {
			openPTag = new NBTTagCompound();
			tag.setTag(OPENP_TAG, openPTag);
		}

		openPTag.setLong("guid", guid);
	}

}
