package openperipheral.addons.api;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.base.Optional;

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
