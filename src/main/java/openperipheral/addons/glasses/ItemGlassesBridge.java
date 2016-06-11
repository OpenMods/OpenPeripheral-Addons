package openperipheral.addons.glasses;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import openmods.item.ItemOpenBlock;

public class ItemGlassesBridge extends ItemOpenBlock {

	public ItemGlassesBridge(Block block) {
		super(block);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List result, boolean extended) {
		NBTTagCompound tag = stack.getTagCompound();

		if (tag != null && tag.hasKey(TileEntityGlassesBridge.TAG_GUID)) {
			long guid = tag.getLong(TileEntityGlassesBridge.TAG_GUID);
			result.add(StatCollector.translateToLocalFormatted("openperipheral.misc.key", TerminalUtils.formatTerminalId(guid)));
		}
	}

}
