package openperipheral.addons.api;

import net.minecraft.entity.player.EntityPlayer;

public interface ITerminalIdSetter {
	public boolean setFor(EntityPlayer player, long terminalId);

	public int getPriority();
}
