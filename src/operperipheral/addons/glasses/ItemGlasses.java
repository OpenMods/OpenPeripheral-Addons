package operperipheral.addons.glasses;

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
import openmods.utils.ItemUtils;
import openmods.utils.MiscUtils;
import openperipheral.addons.Config;
import openperipheral.addons.OpenPeripheralAddons;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGlasses extends ItemArmor {
	public ItemGlasses() {
		super(Config.itemGlassesId, EnumArmorMaterial.CHAIN, 0, 0);
		setMaxDamage(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
		setUnlocalizedName("openperipheral.glasses");
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		NBTTagCompound tag = ItemUtils.getItemTag(itemStack);
		if (tag.hasKey("openp")) {
			String key = tag.getCompoundTag("openp").getString("guid");
			list.add(StatCollector.translateToLocalFormatted("openperipheral.misc.key", key));
		}
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
			TileEntityGlassesBridge bridge = TileEntityGlassesBridge.getGlassesBridgeFromStack(itemStack);
			if (bridge != null) bridge.registerPlayer(player);
		}
	}

}
