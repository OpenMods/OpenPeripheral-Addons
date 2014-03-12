package openperipheral.addons.peripheralproxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openmods.api.INeighbourAwareTile;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ReflectionHelper;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.IPeripheralProvider;
import openperipheral.api.Volatile;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheral;

@Volatile
public class TileEntityPeripheralProxy extends OpenTileEntity implements IPeripheralProvider, INeighbourAwareTile {

	public static final Class<?> CABLE_CLASS = ReflectionHelper.getClass("dan200.computer.shared.TileEntityCable");
	public static final Class<?> BASECOMPUTER_CLASS = ReflectionHelper.getClass("dan200.computer.shared.BlockComputerBase");

	@Override
	public IHostedPeripheral providePeripheral(World worldObj) {
		Object peripheral = null;
		ForgeDirection rotation = getRotation();
		peripheral = ReflectionHelper.callStatic(
				BASECOMPUTER_CLASS,
				"getPeripheralAt",
				ReflectionHelper.typed(worldObj, World.class),
				ReflectionHelper.primitive(xCoord + rotation.offsetX),
				ReflectionHelper.primitive(yCoord + rotation.offsetY),
				ReflectionHelper.primitive(zCoord + rotation.offsetZ),
				ReflectionHelper.primitive(0));
		return (peripheral instanceof IPeripheral)? new WrappedPeripheral((IPeripheral)peripheral, rotation.getOpposite().ordinal()) : null;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		if (!worldObj.isRemote) {
			ForgeDirection rot = getRotation().getOpposite();
			int attachedX = xCoord + rot.offsetX;
			int attachedY = yCoord + rot.offsetY;
			int attachedZ = zCoord + rot.offsetZ;
			TileEntity attachedTE = worldObj.getBlockTileEntity(attachedX, attachedY, attachedZ);
			if (attachedTE != null && CABLE_CLASS.isAssignableFrom(attachedTE.getClass())) {
				ReflectionHelper.call(attachedTE, "networkChanged");
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
