package openperipheral.addons.glasses;

import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import openmods.network.event.NetworkEvent;
import openperipheral.addons.Config;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseButtonEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseDragEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseWheelEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyDownEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyUpEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseButtonEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseWheelEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSignalCaptureEvent;
import openperipheral.addons.glasses.client.TerminalManagerClient;
import openperipheral.addons.glasses.client.TerminalManagerClient.DrawableHitInfo;
import openperipheral.addons.utils.GuiUtils;
import openperipheral.addons.utils.GuiUtils.GuiElements;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Maps;

public class GuiCapture extends GuiScreen {

	private int backgroundColor = 0x2A00FF00;
	private final long guid;

	private int dragCount;

	private int lastDragX;
	private int lastDragY;
	private long dragInterval;

	private int dragThresholdSquared = Config.defaultDragThreshold * Config.defaultDragThreshold;
	private int dragPeriod = Config.defaultDragPeriod;

	private final Map<GuiElements, Boolean> originalState;

	private final Map<GuiElements, Boolean> updatedState;

	public GuiCapture(long guid) {
		this.guid = guid;
		this.originalState = GuiUtils.storeGuiElementsState();
		this.updatedState = Maps.newEnumMap(GuiElements.class);
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();

		final int button = Mouse.getEventButton();
		final int wheel = Mouse.getEventDWheel();
		final int mx = Mouse.getEventX();
		final int my = Mouse.getEventY();

		final boolean canSendDragEvent = canSendDragEvent(mx, my);

		if (button != -1 || wheel != 0 || canSendDragEvent) {
			final float scaleX = (float)this.width / this.mc.displayWidth;
			final float scaleY = (float)this.height / this.mc.displayHeight;

			float x = mx * scaleX;
			float y = this.height - my * scaleY;

			final ScaledResolution resolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
			final DrawableHitInfo hit = TerminalManagerClient.instance.findDrawableHit(guid, resolution, x, y);

			if (button != -1) {
				final boolean state = Mouse.getEventButtonState();
				createMouseButtonEvent(button, state, hit).sendToServer();
				updateDragging(state, mx, my);
			} else if (canSendDragEvent && hit != null) {
				createDragEvent(hit).sendToServer();
				resetDraggingLimiter(mx, my);
			}

			if (wheel != 0) createMouseWheelEvent(wheel, hit).sendToServer();
		}
	}

	private void updateDragging(boolean isPressed, int mx, int my) {
		if (isPressed) {
			this.dragCount++;
			resetDraggingLimiter(mx, my);
		} else {
			this.dragCount = Math.max(this.dragCount - 1, 0); // first event may be mouse_up, from keyboard click
		}
	}

	private void resetDraggingLimiter(int mx, int my) {
		this.lastDragX = mx;
		this.lastDragY = my;
		this.dragInterval = dragPeriod;
	}

	private boolean canSendDragEvent(int mx, int my) {
		if (dragCount <= 0) return false;
		if (dragInterval >= 0) return false;
		final int dx = (mx - lastDragX);
		final int dy = (my - lastDragY);

		final int d = dx * dx + dy * dy;
		return d >= dragThresholdSquared;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		--dragInterval;
	}

	private NetworkEvent createDragEvent(DrawableHitInfo hit) {
		return new GlassesComponentMouseDragEvent(guid, hit.id, hit.surfaceType, hit.dx, hit.dy);
	}

	private NetworkEvent createMouseButtonEvent(int button, boolean state, DrawableHitInfo hit) {
		return hit != null?
				new GlassesComponentMouseButtonEvent(guid, hit.id, hit.surfaceType, hit.dx, hit.dy, button, state) :
				new GlassesMouseButtonEvent(guid, button, state);
	}

	private NetworkEvent createMouseWheelEvent(int wheel, DrawableHitInfo hit) {
		return hit != null?
				new GlassesComponentMouseWheelEvent(guid, hit.id, hit.surfaceType, hit.dx, hit.dy, wheel) :
				new GlassesMouseWheelEvent(guid, wheel);
	}

	@Override
	public void handleKeyboardInput() {
		final int key = Keyboard.getEventKey();

		if (key == Keyboard.KEY_ESCAPE) {
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
		} else {
			final boolean state = Keyboard.getEventKeyState();
			if (state) {
				final char ch = Keyboard.getEventCharacter();

				final boolean isRepeat = Keyboard.isRepeatEvent();
				new GlassesKeyDownEvent(guid, ch, key, isRepeat).sendToServer();
			} else {
				new GlassesKeyUpEvent(guid, key).sendToServer();
			}
		}

		// looks like twitch controls
		super.handleKeyboardInput();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
		drawRect(0, 0, width, height, backgroundColor);
	}

	@Override
	public void initGui() {
		new GlassesSignalCaptureEvent(guid, true).sendToServer();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void onGuiClosed() {
		new GlassesSignalCaptureEvent(guid, false).sendToServer();
		Keyboard.enableRepeatEvents(false);
		GuiUtils.loadGuiElementsState(originalState);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public long getGuid() {
		return guid;
	}

	public void setBackground(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setKeyRepeat(boolean repeat) {
		Keyboard.enableRepeatEvents(repeat);
	}

	public void setDragParameters(int threshold, int period) {
		this.dragPeriod = period;
		this.dragThresholdSquared = threshold * threshold;
	}

	public void updateGuiElementsState(Map<GuiElements, Boolean> visibility) {
		updatedState.putAll(visibility);
	}

	public void forceGuiElementsState() {
		GuiUtils.loadGuiElementsState(updatedState);
	}
}
