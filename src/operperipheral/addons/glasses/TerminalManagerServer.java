package operperipheral.addons.glasses;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

public class TerminalManagerServer {

	@ForgeSubscribe
	public void onServerChatEvent(ServerChatEvent event) {
		EntityPlayerMP player = event.player;
		if (player == null) return;

		if (!event.message.startsWith("$$")) return;

		ItemStack headSlot = player.inventory.armorItemInSlot(3);
		if (headSlot == null || !(headSlot.getItem() instanceof ItemGlasses)) return;

		TileEntityGlassesBridge te = TileEntityGlassesBridge.getGlassesBridgeFromStack(headSlot);

		if (te != null) te.onChatCommand(event.message.substring(2).trim(), event.username);

		event.setCanceled(true);
	}
}
