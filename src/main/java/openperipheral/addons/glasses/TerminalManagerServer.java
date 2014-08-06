package openperipheral.addons.glasses;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
import openmods.Log;
import openperipheral.addons.api.TerminalRegisterEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
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
			if (listener != null) listener.onChatCommand(event.message.substring(2).trim(), event.username);
		}

		event.setCanceled(true);
	}

	private SurfaceServer getSurface(long terminalId, UUID playerId) {
		TileEntityGlassesBridge bridge = listeners.get(terminalId);
		if (bridge == null) return null;
		return bridge.getSurface(playerId);
	}

	@SubscribeEvent
	public void onResetRequest(TerminalResetEvent evt) {
		EntityPlayer player = evt.sender;
		UUID playerUUID = evt.isPrivate? player.getGameProfile().getId() : TerminalUtils.GLOBAL_SURFACE_UUID;
		SurfaceServer surface = getSurface(evt.terminalId, playerUUID);

		if (surface != null) {
			TerminalDataEvent resetEvt = createFullDataEvent(surface, evt.terminalId, evt.isPrivate);
			evt.reply(resetEvt);
		} else {
			Log.warn("Player %s requested invalid surface (%s,%b)", player, TerminalUtils.formatTerminalId(evt.terminalId), evt.isPrivate);
		}
	}

	public static TerminalDataEvent createFullDataEvent(SurfaceServer surface, long terminalId, boolean isPrivate) {
		TerminalDataEvent result = new TerminalDataEvent(terminalId, isPrivate);
		surface.appendFullCommands(result.commands);
		return result;
	}

	public static TerminalDataEvent createUpdateDataEvent(SurfaceServer surface, long terminalId, boolean isPrivate) {
		TerminalDataEvent result = new TerminalDataEvent(terminalId, isPrivate);
		surface.appendUpdateCommands(result.commands);
		return result;
	}

	@SubscribeEvent
	public void onTerminalRegister(TerminalRegisterEvent evt) {
		TileEntityGlassesBridge listener = listeners.get(evt.terminalId);
		if (listener != null) listener.registerTerminal(evt.player);
	}

	public void registerBridge(long terminalId, TileEntityGlassesBridge bridge) {
		listeners.put(terminalId, bridge);
	}
}