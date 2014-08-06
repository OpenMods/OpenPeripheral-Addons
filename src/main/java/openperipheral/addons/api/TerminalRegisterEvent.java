package openperipheral.addons.api;

import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.eventhandler.Event;

public class TerminalRegisterEvent extends Event {

	public final EntityPlayerMP player;
	public final long terminalId;

	public TerminalRegisterEvent(EntityPlayerMP player, long terminalId) {
		this.player = player;
		this.terminalId = terminalId;
	}
}
