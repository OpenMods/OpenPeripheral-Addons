package openperipheral.addons.api;

import com.google.common.base.Optional;
import net.minecraft.entity.player.EntityPlayer;

public interface ITerminalIdGetter {
	public Optional<Long> getFor(EntityPlayer player);

	public int getPriority();
}
