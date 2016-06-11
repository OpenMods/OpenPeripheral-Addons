package openperipheral.addons.glasses;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import openmods.network.event.NetworkEvent;
import openperipheral.addons.Config;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseButtonEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseWheelEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyDownEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyUpEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseButtonEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseDragEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseWheelEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSignalCaptureEvent;
import openperipheral.addons.glasses.client.TerminalManagerClient;
import openperipheral.addons.glasses.client.TerminalManagerClient.DrawableHitInfo;
import openperipheral.addons.utils.GuiUtils;
import openperipheral.addons.utils.GuiUtils.GuiElements;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiCapture extends GuiScreen {

	private int backgroundColor = 0x2A00FF00;
	private final long guid;

	private int mouseButtonsDown;

	private float lastDragX;
	private float lastDragY;
	private int dragInterval;

	private float dragThresholdSquared = Config.defaultDragThreshold * Config.defaultDragThreshold;
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

		final float scaleX = (float)this.width / this.mc.displayWidth;
		final float scaleY = (float)this.height / this.mc.displayHeight;

		final float x = mx * scaleX;
		final float y = this.height - my * scaleY;

		if (button != -1 || wheel != 0) {
			final ScaledResolution resolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
			final DrawableHitInfo hit = TerminalManagerClient.instance.findDrawableHit(guid, resolution, x, y);

			if (button != -1) {
				final boolean state = Mouse.getEventButtonState();
				createMouseButtonEvent(button, state, hit).sendToServer();
				final boolean draggingStarted = updateButtonCounter(state);
				if (draggingStarted) resetDraggingLimiter(x, y);
			}

			if (wheel != 0) createMouseWheelEvent(wheel, hit).sendToServer();
		}

		{
			final float dx = (x - lastDragX);
			final float dy = (y - lastDragY);

			if (canSendDragEvent(dx, dy)) {
				createDragEvent(dx, dy).sendToServer();
				resetDraggingLimiter(x, y);
			}
		}

	}

	private boolean updateButtonCounter(boolean isPressed) {
		if (isPressed) {
			return ++this.mouseButtonsDown == 1;
		} else {
			this.mouseButtonsDown = Math.max(this.mouseButtonsDown - 1, 0); // first event may be mouse_up, from keyboard click
		}

		return false;
	}

	private void resetDraggingLimiter(float x, float y) {
		this.lastDragX = x;
		this.lastDragY = y;
		this.dragInterval = dragPeriod;
	}

	private boolean canSendDragEvent(float dx, float dy) {
		if (mouseButtonsDown <= 0) return false;
		if (dragInterval >= 0) return false;

		final float d = dx * dx + dy * dy;
		return d >= dragThresholdSquared;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		--dragInterval;
	}

	private NetworkEvent createDragEvent(float dx, float dy) {
		return new GlassesMouseDragEvent(guid, dx, dy);
	}

	private NetworkEvent createMouseButtonEvent(int button, boolean state, DrawableHitInfo hit) {
		return hit != null? new GlassesComponentMouseButtonEvent(guid, hit.id, hit.surfaceType, hit.dx, hit.dy, button, state) : new GlassesMouseButtonEvent(guid, button, state);
	}

	private NetworkEvent createMouseWheelEvent(int wheel, DrawableHitInfo hit) {
		return hit != null? new GlassesComponentMouseWheelEvent(guid, hit.id, hit.surfaceType, hit.dx, hit.dy, wheel) : new GlassesMouseWheelEvent(guid, wheel);
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
