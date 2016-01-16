package openperipheral.addons.glasses.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import openmods.geometry.Box2d;
import openperipheral.addons.glasses.GlassesEvent.GlassesChangeBackgroundEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetDragParamsEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetGuiVisibilityEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetKeyRepeatEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesStopCaptureEvent;
import openperipheral.addons.glasses.*;
import openperipheral.addons.glasses.drawable.Drawable;
import openperipheral.addons.glasses.utils.Point2d;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class TerminalManagerClient {

	public static class DrawableHitInfo {
		public final int id;
		public final SurfaceType surfaceType;
		public final float dx;
		public final float dy;
		public final int z;

		public DrawableHitInfo(int id, SurfaceType surfaceType, float dx, float dy, int z) {
			this.id = id;
			this.surfaceType = surfaceType;
			this.dx = dx;
			this.dy = dy;
			this.z = z;
		}
	}

	public static final TerminalManagerClient instance = new TerminalManagerClient();

	private TerminalManagerClient() {}

	private final Table<Long, SurfaceType, SurfaceClient> surfaces = HashBasedTable.create();

	private Optional<Long> terminalGuid = Optional.absent();

	private void tryDrawSurface(long guid, SurfaceType type, float partialTicks, ScaledResolution resolution) {
		SurfaceClient surface = surfaces.get(guid, type);
		if (surface != null) {
			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);

			for (Drawable drawable : surface.getSortedDrawables())
				if (drawable.shouldRender()) drawable.draw(resolution, partialTicks);
			GL11.glPopAttrib();
		}
	}

	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Pre evt) {
		if (evt.type == ElementType.ALL) {
			GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;

			if (gui instanceof GuiCapture) {
				final GuiCapture capture = (GuiCapture)gui;
				// this must be here, since there are some elements (like food bar) that are overriden every tick
				capture.forceGuiElementsState();
			}
		}
	}

	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Post evt) {
		if (evt.type == ElementType.HELMET) {
			if (terminalGuid.isPresent()) {
				final long guid = terminalGuid.get();
				tryDrawSurface(guid, SurfaceType.PRIVATE, evt.partialTicks, evt.resolution);
				tryDrawSurface(guid, SurfaceType.GLOBAL, evt.partialTicks, evt.resolution);
			}
		}
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent evt) {
		if (evt.itemStack != null && NbtGuidProviders.hasTerminalCapabilities(evt.itemStack)) {
			final Optional<Long> guid = NbtGuidProviders.getTerminalGuid(evt.itemStack);
			if (guid.isPresent()) {
				evt.toolTip.add(StatCollector.translateToLocalFormatted("openperipheral.terminal.key", TerminalUtils.formatTerminalId(guid.get())));
			} else {
				evt.toolTip.add(StatCollector.translateToLocal("openperipheral.terminal.unbound"));
			}
		}
	}

	private SurfaceClient getOrCreateSurface(TerminalEvent.Data evt) {
		final SurfaceType surfaceType = evt.getSurfaceType();
		SurfaceClient surface = surfaces.get(evt.terminalId, surfaceType);

		if (surface == null) {
			surface = evt.createSurface();
			surfaces.put(evt.terminalId, surfaceType, surface);
		}
		return surface;
	}

	@SubscribeEvent
	public void onTerminalData(TerminalEvent.PrivateDrawableData evt) {
		updateTerminalDrawables(evt);
	}

	@SubscribeEvent
	public void onTerminalData(TerminalEvent.PublicDrawableData evt) {
		updateTerminalDrawables(evt);
	}

	private void updateTerminalDrawables(TerminalEvent.DrawableData evt) {
		final SurfaceClient surface = getOrCreateSurface(evt);
		surface.drawablesContainer.interpretCommandList(evt.commands);
	}

	@SubscribeEvent
	public void onTerminalClear(TerminalEvent.PrivateClear evt) {
		clearTerminal(evt);
	}

	@SubscribeEvent
	public void onTerminalClear(TerminalEvent.PublicClear evt) {
		clearTerminal(evt);
	}

	private void clearTerminal(TerminalEvent.Clear evt) {
		final SurfaceType surfaceType = evt.getSurfaceType();
		surfaces.remove(evt.terminalId, surfaceType);
	}

	@SubscribeEvent
	public void onBackgroundChange(GlassesChangeBackgroundEvent evt) {
		GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;

		if (gui instanceof GuiCapture) {
			final GuiCapture capture = (GuiCapture)gui;
			long guid = capture.getGuid();
			if (guid == evt.guid) capture.setBackground(evt.backgroundColor);
		}
	}

	@SubscribeEvent
	public void onKeyRepeatSet(GlassesSetKeyRepeatEvent evt) {
		GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;

		if (gui instanceof GuiCapture) {
			final GuiCapture capture = (GuiCapture)gui;
			long guid = capture.getGuid();
			if (guid == evt.guid) capture.setKeyRepeat(evt.repeat);
		}
	}

	@SubscribeEvent
	public void onDragParamsSet(GlassesSetDragParamsEvent evt) {
		GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;

		if (gui instanceof GuiCapture) {
			final GuiCapture capture = (GuiCapture)gui;
			long guid = capture.getGuid();
			if (guid == evt.guid) capture.setDragParameters(evt.threshold, evt.period);
		}
	}

	@SubscribeEvent
	public void onGuiVisibilitySet(GlassesSetGuiVisibilityEvent evt) {
		GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;

		if (gui instanceof GuiCapture) {
			final GuiCapture capture = (GuiCapture)gui;
			long guid = capture.getGuid();
			if (guid == evt.guid) capture.updateGuiElementsState(evt.visibility);
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

	@SubscribeEvent
	public void onDisconnect(ClientDisconnectionFromServerEvent evt) {
		surfaces.clear();
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent evt) {
		if (evt.phase == Phase.END) {
			final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			terminalGuid = player != null? TerminalIdAccess.instance.getIdFrom(player) : Optional.<Long> absent();
		}
	}

	public DrawableHitInfo findDrawableHit(long guid, ScaledResolution resolution, float x, float y) {
		DrawableHitInfo result = findDrawableHit(guid, resolution, x, y, SurfaceType.PRIVATE);
		if (result != null) return result;

		return findDrawableHit(guid, resolution, x, y, SurfaceType.GLOBAL);
	}

	private DrawableHitInfo findDrawableHit(long guid, ScaledResolution resolution, float clickX, float clickY, SurfaceType surfaceType) {
		SurfaceClient surface = surfaces.get(guid, surfaceType);

		if (surface == null) return null;

		final int screenWidth = resolution.getScaledWidth();
		final int screenHeight = resolution.getScaledHeight();

		for (Drawable d : Lists.reverse(surface.getSortedDrawables())) {
			if (!d.isClickable()) continue;

			final Box2d bb = d.getBoundingBox();
			final Point2d localClick = d.transformToLocal(clickX, clickY, screenWidth, screenHeight);

			final float localX = localClick.x;
			final float localY = localClick.y;

			if (0 <= localX && 0 <= localY &&
					localX < bb.width && localY < bb.height) { return new DrawableHitInfo(d.getId(), surfaceType, localX, localY, d.z); }
		}

		return null;
	}
}