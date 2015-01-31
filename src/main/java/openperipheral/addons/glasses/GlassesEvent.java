package openperipheral.addons.glasses;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;

public class GlassesEvent extends NetworkEvent {

	private static final Object[] NO_EXTRAS = new Object[0];

	private static Object[] wrap(Object... args) {
		return args;
	}

	public long guid;

	public GlassesEvent(long guid) {
		this.guid = guid;
	}

	@Override
	protected void readFromStream(DataInput input) throws IOException {
		this.guid = input.readLong();
	}

	@Override
	protected void writeToStream(DataOutput output) throws IOException {
		output.writeLong(this.guid);
	}

	public static class GlassesClientEvent extends GlassesEvent {
		public GlassesClientEvent(long guid) {
			super(guid);
		}

		public String getEventName() {
			throw new UnsupportedOperationException(getClass().getName() + " should not be used directly");
		}

		public Object[] getEventArgs() {
			throw new UnsupportedOperationException(getClass().getName() + " should not be used directly");
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesKeyEvent extends GlassesClientEvent {
		public char ch;

		public int code;

		public boolean pressed;

		public boolean isRepeat;

		public GlassesKeyEvent(long guid, char ch, int code, boolean pressed, boolean isRepeat) {
			super(guid);
			this.ch = ch;
			this.code = code;
			this.pressed = pressed;
			this.isRepeat = isRepeat;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			super.readFromStream(input);
			this.ch = input.readChar();
			this.code = input.readInt();
			final byte flags = input.readByte();
			this.pressed = (flags & 1) != 0;
			this.isRepeat = (flags & 2) != 0;
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			super.writeToStream(output);
			output.writeChar(this.ch);
			output.writeInt(this.code);
			output.writeByte((this.pressed? 1 : 0) | (this.isRepeat? 2 : 0));
		}

		@Override
		public String getEventName() {
			return pressed? "glasses_key_down" : "glasses_key_up";
		}

		@Override
		public Object[] getEventArgs() {
			return wrap(code, ch, isRepeat);
		}
	}

	public static class GlassesMouseEvent extends GlassesClientEvent {
		public int button;
		public int wheel;
		public boolean pressed;

		public GlassesMouseEvent(long guid, int button, int wheel, boolean pressed) {
			super(guid);
			this.button = button;
			this.wheel = wheel;
			this.pressed = pressed;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			super.readFromStream(input);
			this.button = input.readInt();
			this.wheel = input.readInt();
			this.pressed = input.readBoolean();
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			super.writeToStream(output);
			output.writeInt(this.button);
			output.writeInt(this.wheel);
			output.writeBoolean(this.pressed);
		}

		@Override
		public String getEventName() {
			return "glasses_general_mouse";
		}

		@Override
		public Object[] getEventArgs() {
			return wrap(button, wheel, pressed);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesComponentMouseEvent extends GlassesMouseEvent {
		public int componentId;
		public boolean isPrivate;
		public int x;
		public int y;

		public GlassesComponentMouseEvent(long guid, int button, int wheel, boolean pressed, int componentId, boolean isPrivate, int x, int y) {
			super(guid, button, wheel, pressed);
			this.componentId = componentId;
			this.isPrivate = isPrivate;
			this.x = x;
			this.y = y;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			super.readFromStream(input);
			this.componentId = input.readInt();
			this.isPrivate = input.readBoolean();
			this.x = input.readInt();
			this.y = input.readInt();
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			super.writeToStream(output);
			output.writeInt(this.componentId);
			output.writeBoolean(this.isPrivate);
			output.writeInt(this.x);
			output.writeInt(this.y);
		}

		@Override
		public String getEventName() {
			return "glasses_component_mouse";
		}

		@Override
		public Object[] getEventArgs() {
			return wrap(button, wheel, pressed, componentId, isPrivate, x, y);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesSignalCaptureEvent extends GlassesClientEvent {
		public boolean captureState;

		public GlassesSignalCaptureEvent(long guid, boolean captureState) {
			super(guid);
			this.captureState = captureState;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			super.readFromStream(input);
			this.captureState = input.readBoolean();
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			super.writeToStream(output);
			output.writeBoolean(this.captureState);
		}

		@Override
		public String getEventName() {
			return captureState? "glasses_capture" : "glasses_release";
		}

		@Override
		public Object[] getEventArgs() {
			return NO_EXTRAS;
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class GlassesStopCaptureEvent extends GlassesEvent {

		public GlassesStopCaptureEvent(long guid) {
			super(guid);
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class GlassesChangeBackground extends GlassesEvent {

		public int backgroundColor;

		public GlassesChangeBackground(long guid, int backgroundColor) {
			super(guid);
			this.backgroundColor = backgroundColor;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			super.readFromStream(input);
			this.backgroundColor = input.readInt();
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			super.writeToStream(output);
			output.writeInt(this.backgroundColor);
		}
	}
}
