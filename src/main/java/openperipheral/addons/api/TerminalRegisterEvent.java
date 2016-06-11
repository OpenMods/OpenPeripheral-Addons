package openperipheral.addons.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;

public class TerminalRegisterEvent extends Event {

	public final EntityPlayerMP player;
	public final long terminalId;

	public TerminalRegisterEvent(EntityPlayerMP player, long terminalId) {
		this.player = player;
		this.terminalId = terminalId;
	}
}
