package openperipheral.addons.glasses.server;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import openperipheral.addons.Config;
import openperipheral.addons.api.TerminalRegisterEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesClientEvent;
import openperipheral.addons.glasses.*;

import com.google.common.collect.MapMaker;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TerminalManagerServer {
	private TerminalManagerServer() {}

	public static final TerminalManagerServer instance = new TerminalManagerServer();

	private final Map<Long, TileEntityGlassesBridge> listeners = new MapMaker().weakValues().makeMap();

	private static final String EVENT_CHAT_COMMAND = "glasses_chat_command";

	private static final String EVENT_CHAT_MESSAGE = "glasses_chat_message";

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onServerChatEvent(ServerChatEvent event) {
		final EntityPlayerMP player = event.player;
		final Long guid = TerminalUtils.tryGetTerminalGuid(player);
		if (guid != null) {
			if (event.message.startsWith("$$")) {
				sendChatEvent(EVENT_CHAT_COMMAND, player, guid, event.message.substring(2).trim());
				event.setCanceled(true);
			} else if (Config.listenToAllChat) {
				sendChatEvent(EVENT_CHAT_MESSAGE, player, guid, event.message);
			}

		}
	}

	private void sendChatEvent(String event, EntityPlayerMP player, Long guid, String message) {
		final TileEntityGlassesBridge listener = listeners.get(guid);
		if (listener != null) listener.onChatCommand(event, message, player);
	}

	@SubscribeEvent
	public void onResetRequest(TerminalEvent.PrivateDrawableReset evt) {
		TileEntityGlassesBridge listener = listeners.get(evt.terminalId);
		if (listener != null) listener.handlePrivateDrawableResetRequest(evt);
	}

	@SubscribeEvent
	public void onResetRequest(TerminalEvent.PublicDrawableReset evt) {
		TileEntityGlassesBridge listener = listeners.get(evt.terminalId);
		if (listener != null) listener.handlePublicDrawableResetRequest(evt);
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