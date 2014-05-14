package openperipheral.addons.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event;

public class TerminalRegisterEvent extends Event {

	public final EntityPlayer player;
	public final long terminalId;

	public TerminalRegisterEvent(EntityPlayer player, long terminalId) {
		this.player = player;
		this.terminalId = terminalId;
	}
}
