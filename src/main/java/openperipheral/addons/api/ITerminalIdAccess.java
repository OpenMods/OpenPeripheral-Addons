package openperipheral.addons.api;

import com.google.common.base.Optional;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Registry of id storage providers for terminal glasses.
 *
 * @see ApiHolder
 */
public interface ITerminalIdAccess extends IApiInterface {
	public Optional<Long> getIdFrom(EntityPlayer player);

	public boolean setIdFor(EntityPlayer player, long guid);

	public void register(ITerminalIdGetter getter);

	public void register(ITerminalIdSetter setter);
}
