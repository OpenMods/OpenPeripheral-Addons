package openperipheral.addons.glasses;

import net.minecraft.client.gui.GuiScreen;
import openperipheral.addons.glasses.GlassesEvent.GlassesComponentMouseEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesKeyEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesMouseEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSignalCaptureEvent;
import openperipheral.addons.glasses.TerminalManagerClient.DrawableHitInfo;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiCapture extends GuiScreen {

	private int backgroundColor = 0x2A00FF00;
	private final long guid;

	public GuiCapture(long guid) {
		this.guid = guid;
		new GlassesSignalCaptureEvent(guid, true).sendToServer();
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();

		int button = Mouse.getEventButton();
		boolean state = Mouse.getEventButtonState();
		int wheel = Mouse.getEventDWheel();

		if (button != -1 || state || wheel != 0) {
			int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
			int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

			DrawableHitInfo hit = TerminalManagerClient.instance.findDrawableHit(guid, x, y);

			if (hit != null) {
				new GlassesComponentMouseEvent(guid, button, wheel, state, hit.id, hit.isPrivate, hit.dx, hit.dy).sendToServer();
			} else {
				new GlassesMouseEvent(guid, button, wheel, state).sendToServer();
			}
		}
	}

	@Override
	public void handleKeyboardInput() {
		final int key = Keyboard.getEventKey();

		if (key == Keyboard.KEY_ESCAPE) {
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
		} else {
			final char ch = Keyboard.getEventCharacter();
			final boolean state = Keyboard.getEventKeyState();
			final boolean isRepeat = Keyboard.isRepeatEvent();
			new GlassesKeyEvent(guid, ch, key, state, isRepeat).sendToServer();
		}

		// looks like twitch controls
		this.mc.func_152348_aa();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
		drawRect(0, 0, width, height, backgroundColor);
	}

	@Override
	public void onGuiClosed() {
		new GlassesSignalCaptureEvent(guid, false).sendToServer();
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
}
