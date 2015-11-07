package openperipheral.addons.glasses;

import java.util.Map;

import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEventMeta;
import openmods.network.event.SerializableNetworkEvent;
import openmods.serializable.cls.Serialize;
import openperipheral.addons.utils.GuiUtils.GuiElements;
import openperipheral.api.architecture.IArchitecture;

public class GlassesEvent extends SerializableNetworkEvent {

	private static final Object[] NO_EXTRAS = new Object[0];

	private static Object[] wrap(Object... args) {
		return args;
	}

	@Serialize
	public long guid;

	public GlassesEvent(long guid) {
		this.guid = guid;
	}

	public static class GlassesClientEvent extends GlassesEvent {
		public GlassesClientEvent(long guid) {
			super(guid);
		}

		public String getEventName() {
			throw new UnsupportedOperationException(getClass().getName() + " should not be used directly");
		}

		public Object[] getEventArgs(IArchitecture access) {
			throw new UnsupportedOperationException(getClass().getName() + " should not be used directly");
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesKeyDownEvent extends GlassesClientEvent {
		@Serialize
		public char ch;

		@Serialize
		public int code;

		@Serialize
		public boolean isRepeat;

		public GlassesKeyDownEvent(long guid, char ch, int code, boolean isRepeat) {
			super(guid);
			this.ch = ch;
			this.code = code;
			this.isRepeat = isRepeat;
		}

		@Override
		public String getEventName() {
			return "glasses_key_down";
		}

		@Override
		public Object[] getEventArgs(IArchitecture access) {
			// CC has some problems with chars
			return wrap(code, Character.toString(ch), isRepeat);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesKeyUpEvent extends GlassesClientEvent {

		@Serialize
		public int code;

		public GlassesKeyUpEvent(long guid, int code) {
			super(guid);
			this.code = code;
		}

		@Override
		public String getEventName() {
			return "glasses_key_up";
		}

		@Override
		public Object[] getEventArgs(IArchitecture access) {
			return wrap(code);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesMouseWheelEvent extends GlassesClientEvent {
		@Serialize
		public int wheel;

		public GlassesMouseWheelEvent(long guid, int wheel) {
			super(guid);
			this.wheel = wheel;
		}

		@Override
		public String getEventName() {
			return "glasses_mouse_scroll";
		}

		@Override
		public Object[] getEventArgs(IArchitecture access) {
			return wrap(wheel);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesMouseButtonEvent extends GlassesClientEvent {
		@Serialize
		public int button;

		@Serialize
		public boolean pressed;

		public GlassesMouseButtonEvent(long guid, int button, boolean pressed) {
			super(guid);
			this.button = button;
			this.pressed = pressed;
		}

		@Override
		public String getEventName() {
			return pressed? "glasses_mouse_down" : "glasses_mouse_up";
		}

		@Override
		public Object[] getEventArgs(IArchitecture access) {
			return wrap(button);
		}
	}

	public abstract static class GlassesComponentMouseEvent extends GlassesClientEvent {
		@Serialize
		public int componentId;

		@Serialize
		public SurfaceType surfaceType;

		@Serialize
		public float x;

		@Serialize
		public float y;

		public GlassesComponentMouseEvent(long guid, int componentId, SurfaceType surfaceType, float x, float y) {
			super(guid);
			this.componentId = componentId;
			this.surfaceType = surfaceType;
			this.x = x;
			this.y = y;
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesComponentMouseWheelEvent extends GlassesComponentMouseEvent {
		@Serialize
		public int wheel;

		public GlassesComponentMouseWheelEvent(long guid, int componentId, SurfaceType surfaceType, float x, float y, int wheel) {
			super(guid, componentId, surfaceType, x, y);
			this.wheel = wheel;
		}

		@Override
		public String getEventName() {
			return "glasses_component_mouse_wheel";
		}

		@Override
		public Object[] getEventArgs(IArchitecture access) {
			return wrap(access.createIndex(componentId), surfaceType.scriptValue, x, y, wheel);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesComponentMouseButtonEvent extends GlassesComponentMouseEvent {
		@Serialize
		public int button;

		@Serialize
		public boolean pressed;

		public GlassesComponentMouseButtonEvent(long guid, int componentId, SurfaceType surfaceType, float x, float y, int button, boolean pressed) {
			super(guid, componentId, surfaceType, x, y);
			this.button = button;
			this.pressed = pressed;
		}

		@Override
		public String getEventName() {
			return pressed? "glasses_component_mouse_down" : "glasses_component_mouse_up";
		}

		@Override
		public Object[] getEventArgs(IArchitecture access) {
			return wrap(access.createIndex(componentId), surfaceType.scriptValue, x, y, button);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesComponentMouseDragEvent extends GlassesComponentMouseEvent {

		public GlassesComponentMouseDragEvent(long guid, int componentId, SurfaceType surfaceType, float x, float y) {
			super(guid, componentId, surfaceType, x, y);
		}

		@Override
		public String getEventName() {
			return "glasses_component_mouse_drag";
		}

		@Override
		public Object[] getEventArgs(IArchitecture access) {
			return wrap(access.createIndex(componentId), surfaceType.scriptValue, x, y);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class GlassesSignalCaptureEvent extends GlassesClientEvent {
		@Serialize
		public boolean captureState;

		public GlassesSignalCaptureEvent(long guid, boolean captureState) {
			super(guid);
			this.captureState = captureState;
		}

		@Override
		public String getEventName() {
			return captureState? "glasses_capture" : "glasses_release";
		}

		@Override
		public Object[] getEventArgs(IArchitecture access) {
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
	public static class GlassesChangeBackgroundEvent extends GlassesEvent {

		@Serialize
		public int backgroundColor;

		public GlassesChangeBackgroundEvent(long guid, int backgroundColor) {
			super(guid);
			this.backgroundColor = backgroundColor;
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class GlassesSetKeyRepeatEvent extends GlassesEvent {

		@Serialize
		public boolean repeat;

		public GlassesSetKeyRepeatEvent(long guid, boolean repeat) {
			super(guid);
			this.repeat = repeat;
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class GlassesSetDragParamsEvent extends GlassesEvent {

		@Serialize
		public int period;

		@Serialize
		public int threshold;

		public GlassesSetDragParamsEvent(long guid, int period, int threshold) {
			super(guid);
			this.period = period;
			this.threshold = threshold;
		}

	}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class GlassesSetGuiVisibilityEvent extends GlassesEvent {

		@Serialize
		public Map<GuiElements, Boolean> visibility;

		public GlassesSetGuiVisibilityEvent(long guid, Map<GuiElements, Boolean> visibility) {
			super(guid);
			this.visibility = visibility;
		}
	}
}
