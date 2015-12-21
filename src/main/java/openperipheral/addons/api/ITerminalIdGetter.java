package openperipheral.addons.api;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.base.Optional;

public interface ITerminalIdGetter {
	public Optional<Long> getFor(EntityPlayer player);

	public int getPriority();
}
