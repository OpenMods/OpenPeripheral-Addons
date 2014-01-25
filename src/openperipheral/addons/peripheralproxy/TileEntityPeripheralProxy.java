package openperipheral.addons.peripheralproxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openmods.api.INeighbourAwareTile;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ReflectionHelper;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.IPeripheralProvider;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheral;

public class TileEntityPeripheralProxy extends OpenTileEntity implements IPeripheralProvider, INeighbourAwareTile {

	public static final Class<?> CABLE_CLASS = ReflectionHelper.getClass("dan200.computer.shared.TileEntityCable");
	public static final Class<?> BASECOMPUTER_CLASS = ReflectionHelper.getClass("dan200.computer.shared.BlockComputerBase");

	@Override
	public IHostedPeripheral providePeripheral(World worldObj) {
		Object peripheral = null;
		ForgeDirection rotation = getRotation();
		try {
			peripheral = ReflectionHelper.callStatic(
					BASECOMPUTER_CLASS,
					"getPeripheralAt",
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
			if (attachedTE != null && attachedTE.getClass().getName().equals(CABLE_CLASS)) {
				try {
					ReflectionHelper.call(attachedTE, "networkChanged");
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
