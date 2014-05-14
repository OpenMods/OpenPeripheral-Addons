package openperipheral.addons.client;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoundLoader {

	private final String[] SOUNDS = new String[] {
			"ticketmachine.ogg",
			"robotstepping.ogg",
			"robotjump.ogg",
			"robothurt.ogg",
			"robotdead1.ogg", "robotdead2.ogg",
			"lazer1.ogg",
			"robotready.ogg"
	};

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void loadingSounds(SoundLoadEvent event) {
		for (String soundFile : SOUNDS)
			event.manager.addSound("openperipheraladdons:" + soundFile);
	}
}
