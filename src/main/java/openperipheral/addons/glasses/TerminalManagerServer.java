package openperipheral.addons.glasses;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import openperipheral.addons.Config;
import openperipheral.addons.api.TerminalRegisterEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesClientEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;

import com.google.common.collect.MapMaker;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TerminalManagerServer {
	private TerminalManagerServer() {}

	public static final TerminalManagerServer instance = new TerminalManagerServer();

	private final Map<Long, TileEntityGlassesBridge> listeners = new MapMaker().weakValues().makeMap();

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onServerChatEvent(ServerChatEvent event) {
		final EntityPlayerMP player = event.player;
		final Long guid = TerminalUtils.tryGetTerminalGuid(player);
		if (guid != null) {
			final String message;
			final boolean isHidden;
			if (event.message.startsWith("$$")) {
				message = event.message.substring(2).trim();
				isHidden = true;
				event.setCanceled(true);
			} else if (Config.listenToAllChat) {
				message = event.message;
				isHidden = false;
			} else return;

			final TileEntityGlassesBridge listener = listeners.get(guid);
			if (listener != null) listener.onChatCommand(message, player, isHidden);
		}
	}

	@SubscribeEvent
	public void onResetRequest(TerminalResetEvent evt) {
		TileEntityGlassesBridge listener = listeners.get(evt.terminalId);
		if (listener != null) listener.handleResetRequest(evt);
	}

	@SubscribeEvent
	public void onTerminalRegister(TerminalRegisterEvent evt) {
		TileEntityGlassesBridge listener = listeners.get(evt.terminalId);
		if (listener != null) listener.registerTerminal(evt.player);
	}

	@SubscribeEvent
	public void onGlassesEvent(GlassesClientEvent evt) {
		TileEntityGlassesBridge listener = listeners.get(evt.guid);
		if (listener != null) listener.handleUserEvent(evt);
	}

	public void registerBridge(long terminalId, TileEntityGlassesBridge bridge) {
		listeners.put(terminalId, bridge);
	}
}