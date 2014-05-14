package openperipheral.addons.glasses;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;
import openperipheral.addons.glasses.TerminalEvent.TerminalClearEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class TerminalManagerClient {

	private final Table<Long, String, SurfaceClient> surfaces = HashBasedTable.create();

	private void tryDrawSurface(long guid, String player, float partialTicks) {
		SurfaceClient surface = surfaces.get(guid, player);
		if (surface != null) for (Drawable drawable : surface)
			drawable.draw(partialTicks);
	}

	@ForgeSubscribe
	public void onRenderGameOverlay(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET && evt instanceof RenderGameOverlayEvent.Post) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			Long guid = TerminalUtils.tryGetTerminalGuid(player);
			if (guid != null) {
				tryDrawSurface(guid, TerminalUtils.GLOBAL_MARKER, evt.partialTicks);
				tryDrawSurface(guid, TerminalUtils.PRIVATE_MARKER, evt.partialTicks);
			}

		}
	}

	private static String getSurfaceName(TerminalEvent evt) {
		return evt.isPrivate? TerminalUtils.PRIVATE_MARKER : TerminalUtils.GLOBAL_MARKER;
	}

	@ForgeSubscribe
	public void onTerminalData(TerminalDataEvent evt) {
		String surfaceName = getSurfaceName(evt);
		SurfaceClient surface = surfaces.get(evt.terminalId, surfaceName);

		if (surface == null) {
			surface = new SurfaceClient(evt.terminalId, evt.isPrivate);
			surfaces.put(evt.terminalId, surfaceName, surface);
		}

		surface.interpretCommandList(evt.commands);
	}

	@ForgeSubscribe
	public void onTerminalClear(TerminalClearEvent evt) {
		String surfaceName = getSurfaceName(evt);
		surfaces.remove(evt.terminalId, surfaceName);
	}
}