package openperipheral.addons.glasses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import openperipheral.addons.glasses.GlassesEvent.GlassesChangeBackground;
import openperipheral.addons.glasses.GlassesEvent.GlassesStopCaptureEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalClearEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TerminalManagerClient {

	public static class DrawableHitInfo {
		public final int id;
		public final boolean isPrivate;
		public final int dx;
		public final int dy;

		public DrawableHitInfo(int id, boolean isPrivate, int dx, int dy) {
			this.id = id;
			this.isPrivate = isPrivate;
			this.dx = dx;
			this.dy = dy;
		}
	}

	public static final TerminalManagerClient instance = new TerminalManagerClient();

	private TerminalManagerClient() {}

	private final Table<Long, String, SurfaceClient> surfaces = HashBasedTable.create();

	private void tryDrawSurface(long guid, String player, float partialTicks, ScaledResolution resolution) {
		SurfaceClient surface = surfaces.get(guid, player);
		if (surface != null) {
			for (Drawable drawable : surface)
				drawable.draw(resolution, partialTicks);
		}
	}

	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent evt) {
		if (evt.type == ElementType.HELMET && evt instanceof RenderGameOverlayEvent.Post) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			Long guid = TerminalUtils.tryGetTerminalGuid(player);
			if (guid != null) {
				tryDrawSurface(guid, TerminalUtils.GLOBAL_MARKER, evt.partialTicks, evt.resolution);
				tryDrawSurface(guid, TerminalUtils.PRIVATE_MARKER, evt.partialTicks, evt.resolution);
			}
		}
	}

	private static String getSurfaceName(boolean isPrivate) {
		return isPrivate? TerminalUtils.PRIVATE_MARKER : TerminalUtils.GLOBAL_MARKER;
	}

	@SubscribeEvent
	public void onTerminalData(TerminalDataEvent evt) {
		String surfaceName = getSurfaceName(evt.isPrivate);
		SurfaceClient surface = surfaces.get(evt.terminalId, surfaceName);

		if (surface == null) {
			surface = new SurfaceClient(evt.terminalId, evt.isPrivate);
			surfaces.put(evt.terminalId, surfaceName, surface);
		}

		surface.interpretCommandList(evt.commands);
	}

	@SubscribeEvent
	public void onTerminalClear(TerminalClearEvent evt) {
		String surfaceName = getSurfaceName(evt.isPrivate);
		surfaces.remove(evt.terminalId, surfaceName);
	}

	@SubscribeEvent
	public void onBackgroundChange(GlassesChangeBackground evt) {
		GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;

		if (gui instanceof GuiCapture) {
			final GuiCapture capture = (GuiCapture)gui;
			long guid = capture.getGuid();
			if (guid == evt.guid) capture.setBackground(evt.backgroundColor);
		}
	}

	@SubscribeEvent
	public void onCaptureForce(GlassesStopCaptureEvent evt) {
		GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;

		if (gui instanceof GuiCapture) {
			long guid = ((GuiCapture)gui).getGuid();
			if (guid == evt.guid) FMLCommonHandler.instance().showGuiScreen(null);
		}
	}

	public DrawableHitInfo findDrawableHit(long guid, int x, int y) {
		DrawableHitInfo result = findDrawableHit(guid, x, y, false);
		if (result != null) return result;

		return findDrawableHit(guid, x, y, true);
	}

	private DrawableHitInfo findDrawableHit(long guid, int x, int y, boolean isPrivate) {
		final String surfaceName = getSurfaceName(isPrivate);
		SurfaceClient surface = surfaces.get(guid, surfaceName);

		if (surface == null) return null;

		for (Drawable d : surface) {
			final int dx = x - d.x;
			final int dy = y - d.y;

			if (0 <= dx && 0 <= dy &&
					dx < d.getWidth() &&
					dy < d.getHeight()) return new DrawableHitInfo(d.getId(), isPrivate, dx, dy);
		}

		return null;
	}
}