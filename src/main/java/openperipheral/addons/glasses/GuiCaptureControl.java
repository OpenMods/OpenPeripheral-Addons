package openperipheral.addons.glasses;

import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import openperipheral.addons.Config;
import openperipheral.addons.glasses.GlassesEvent.GlassesChangeBackgroundEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetDragParamsEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetGuiVisibilityEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesSetKeyRepeatEvent;
import openperipheral.addons.glasses.GlassesEvent.GlassesStopCaptureEvent;
import openperipheral.addons.utils.GuiUtils.GuiElements;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.Optionals;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.adapter.method.ScriptObject;

@ScriptObject
@Asynchronous
@AdapterSourceName("glasses_capture")
public class GuiCaptureControl {

	private final long guid;
	private final WeakReference<EntityPlayerMP> player;

	public GuiCaptureControl(long guid, WeakReference<EntityPlayerMP> player) {
		this.guid = guid;
		this.player = player;
	}

	protected EntityPlayer getPlayer() {
		EntityPlayer player = this.player.get();
		if (player == null) throw new IllegalStateException("Object is no longer valid");
		return player;
	}

	@ScriptCallable(description = "Stops capture for player")
	public void stopCapturing() {
		EntityPlayer player = getPlayer();
		new GlassesStopCaptureEvent(guid).sendToPlayer(player);
	}

	@ScriptCallable(description = "Set background on capture mode screen")
	public void setBackground(@Arg(name = "background") int background,
			@Optionals @Arg(name = "alpha") Integer alpha) {
		EntityPlayer player = getPlayer();
		final int a = alpha != null? (alpha << 24) : 0x2A000000;
		new GlassesChangeBackgroundEvent(guid, background & 0x00FFFFFF | a).sendToPlayer(player);
	}

	@ScriptCallable(description = "When enabled, holding key down for long time will generate multiple events")
	public void setKeyRepeat(@Arg(name = "isEnabled") boolean keyRepeat) {
		EntityPlayer player = getPlayer();
		new GlassesSetKeyRepeatEvent(guid, keyRepeat).sendToPlayer(player);
	}

	@ScriptCallable(description = "Set minimal distance and minimum period needed for ")
	public void setDragParameters(@Arg(name = "distance") int threshold, @Arg(name = "delay") int period) {
		Preconditions.checkArgument(threshold >= Config.minimalDragThreshold, "Distance must be not less than %s", Config.minimalDragThreshold);
		Preconditions.checkArgument(period >= Config.minimalDragPeriod, "Update period must be not less than %s", Config.minimalDragPeriod);
		EntityPlayer player = getPlayer();
		new GlassesSetDragParamsEvent(guid, period, threshold).sendToPlayer(player);
	}

	@ScriptCallable(description = "Sets visiblity state of various vanilla GUI elements")
	public void toggleGuiElements(@Arg(name = "visibility") Map<GuiElements, Boolean> visiblity) {
		EntityPlayer player = getPlayer();
		new GlassesSetGuiVisibilityEvent(guid, visiblity).sendToPlayer(player);
	}
}