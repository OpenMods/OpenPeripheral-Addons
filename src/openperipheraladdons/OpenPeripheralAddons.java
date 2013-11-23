package openperipheraladdons;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraft.creativetab.CreativeTabs;
import openmods.Log;
import openmods.api.IOpenMod;

@Mod(modid = "OpenPeripheralAddons", name = "OpenPeripheralAddons", version = "@VERSION@", dependencies = "required-after:OpenPeripheral")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenPeripheralAddons implements IOpenMod {

	@Override
	public Log getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
