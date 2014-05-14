package openperipheral.addons.glasses;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openmods.utils.ItemUtils;
import openmods.utils.MiscUtils;
import openperipheral.addons.Config;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.addons.api.ITerminalItem;
import openperipheral.addons.api.TerminalRegisterEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGlasses extends ItemArmor implements ITerminalItem {
	private static final String OPENP_TAG = "openp";

	public ItemGlasses() {
		super(Config.itemGlassesId, EnumArmorMaterial.CHAIN, 0, 0);
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
		setUnlocalizedName("openperipheral.glasses");
	}

	private static Long extractGuid(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		if (!tag.hasKey(OPENP_TAG)) return null;

		NBTTagCompound openp = tag.getCompoundTag(OPENP_TAG);
		return TerminalUtils.extractGuid(openp);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		Long guid = extractGuid(itemStack);
		if (guid != null) list.add(StatCollector.translateToLocalFormatted("openperipheral.misc.key", TerminalUtils.formatTerminalId(guid)));
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		switch (MiscUtils.getHoliday()) {
			case 1:
				return "openperipheraladdons:textures/models/glasses_valentines.png";
			case 2:
				return "openperipheraladdons:textures/models/glasses_halloween.png";
			case 3:
				return "openperipheraladdons:textures/models/glasses_christmas.png";
			default:
				return "openperipheraladdons:textures/models/glasses.png";
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("openperipheraladdons:glasses");
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack) {
		if (!world.isRemote) {
			Long guid = extractGuid(itemStack);
			if (guid != null) MinecraftForge.EVENT_BUS.post(new TerminalRegisterEvent(player, guid));
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
