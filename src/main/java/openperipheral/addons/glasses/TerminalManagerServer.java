package openperipheral.addons.glasses;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import openperipheral.addons.api.TerminalRegisterEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesClientEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;

import com.google.common.collect.MapMaker;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TerminalManagerServer {
	private TerminalManagerServer() {}

	public static final TerminalManagerServer instance = new TerminalManagerServer();

	private final Map<Long, TileEntityGlassesBridge> listeners = new MapMaker().weakValues().makeMap();

	@SubscribeEvent
	public void onServerChatEvent(ServerChatEvent event) {
		EntityPlayerMP player = event.player;

		if (!event.message.startsWith("$$")) return;

		Long guid = TerminalUtils.tryGetTerminalGuid(player);
		if (guid != null) {
			TileEntityGlassesBridge listener = listeners.get(guid);
			if (listener != null) listener.onChatCommand(event.message.substring(2).trim(), player);
		}

		event.setCanceled(true);
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