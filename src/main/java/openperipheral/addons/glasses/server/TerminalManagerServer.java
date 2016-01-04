package openperipheral.addons.glasses.server;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import openperipheral.addons.Config;
import openperipheral.addons.api.TerminalRegisterEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesClientEvent;
import openperipheral.addons.glasses.*;

import com.google.common.base.Optional;
import com.google.common.collect.MapMaker;

public class TerminalManagerServer {
	private TerminalManagerServer() {}

	public static final TerminalManagerServer instance = new TerminalManagerServer();

	private final Map<Long, TileEntityGlassesBridge> listeners = new MapMaker().weakValues().makeMap();

	private static final String EVENT_CHAT_COMMAND = "glasses_chat_command";

	private static final String EVENT_CHAT_MESSAGE = "glasses_chat_message";

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onServerChatEvent(ServerChatEvent event) {
		final EntityPlayerMP player = event.player;
		final Optional<Long> guid = TerminalIdAccess.instance.getIdFrom(player);
		if (guid.isPresent()) {
			if (event.message.startsWith("$$")) {
				sendChatEvent(EVENT_CHAT_COMMAND, player, guid.get(), event.message.substring(2).trim());
				event.setCanceled(true);
			} else if (Config.listenToAllChat) {
				sendChatEvent(EVENT_CHAT_MESSAGE, player, guid.get(), event.message);
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

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent evt) {
		if (evt.phase == Phase.START && evt.player instanceof EntityPlayerMP) {
			final Optional<Long> guid = TerminalIdAccess.instance.getIdFrom(evt.player);
			if (guid.isPresent()) {
				TileEntityGlassesBridge listener = listeners.get(guid.get());
				if (listener != null) listener.registerTerminal((EntityPlayerMP)evt.player);
			}
		}
	}

	public void registerBridge(long terminalId, TileEntityGlassesBridge bridge) {
		listeners.put(terminalId, bridge);
	}
}