package openperipheral.addons.glasses;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import openmods.Log;
import openperipheral.addons.api.TerminalRegisterEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;

import com.google.common.collect.MapMaker;

public class TerminalManagerServer {
	private TerminalManagerServer() {}

	public static final TerminalManagerServer instance = new TerminalManagerServer();

	private final Map<Long, TileEntityGlassesBridge> listeners = new MapMaker().weakValues().makeMap();

	@ForgeSubscribe
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

	private SurfaceServer getSurface(long terminalId, String playerName) {
		TileEntityGlassesBridge bridge = listeners.get(terminalId);
		if (bridge == null) return null;
		return bridge.getSurface(playerName);
	}

	@ForgeSubscribe
	public void onResetRequest(TerminalResetEvent evt) {
		EntityPlayer player = (EntityPlayer)evt.player;
		String playerName = evt.isPrivate? player.getEntityName() : TerminalUtils.GLOBAL_MARKER;
		SurfaceServer surface = getSurface(evt.terminalId, playerName);

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

	@ForgeSubscribe
	public void onTerminalRegister(TerminalRegisterEvent evt) {
		TileEntityGlassesBridge listener = listeners.get(evt.terminalId);
		if (listener != null) listener.registerTerminal(evt.player);
	}

	public void registerBridge(long terminalId, TileEntityGlassesBridge bridge) {
		listeners.put(terminalId, bridge);
	}
}