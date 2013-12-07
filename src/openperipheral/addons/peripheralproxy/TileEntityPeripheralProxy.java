package openperipheral.addons.peripheralproxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openmods.api.INeighbourAwareTile;
import openmods.tileentity.OpenTileEntity;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.IPeripheralProvider;
import openperipheral.util.ReflectionHelper;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheral;

public class TileEntityPeripheralProxy extends OpenTileEntity implements IPeripheralProvider, INeighbourAwareTile {

	public static final String CABLE_CLASS_NAME = "dan200.computer.shared.TileEntityCable";
	public static final String BASECOMPUTER_CLASS_NAME = "dan200.computer.shared.BlockComputerBase";

	@Override
	public IHostedPeripheral providePeripheral(World worldObj) {
		Object peripheral = null;
		ForgeDirection rotation = getRotation();
		try {
			peripheral = ReflectionHelper.callMethod(
					BASECOMPUTER_CLASS_NAME,
					null,
					new String[] { "getPeripheralAt" },
					worldObj,
					xCoord + rotation.offsetX,
					yCoord + rotation.offsetY,
					zCoord + rotation.offsetZ,
					0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (peripheral instanceof IPeripheral) { return new WrappedPeripheral((IPeripheral)peripheral, rotation.getOpposite().ordinal()); }
		return null;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		if (!worldObj.isRemote) {
			ForgeDirection rot = getRotation().getOpposite();
			int attachedX = xCoord + rot.offsetX;
			int attachedY = yCoord + rot.offsetY;
			int attachedZ = zCoord + rot.offsetZ;
			TileEntity attachedTE = worldObj.getBlockTileEntity(attachedX, attachedY, attachedZ);
			if (attachedTE != null && attachedTE.getClass().getName().equals(CABLE_CLASS_NAME)) {
				try {
					ReflectionHelper.callMethod("", attachedTE, new String[] { "networkChanged" });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			worldObj.notifyBlockOfNeighborChange(
					attachedX,
					attachedY,
					attachedZ,
					OpenPeripheralAddons.Blocks.peripheralProxy.blockID
					);
		}
	}

}
