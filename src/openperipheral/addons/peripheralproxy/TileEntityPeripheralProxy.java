package openperipheral.addons.peripheralproxy;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openmods.api.INeighbourAwareTile;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ReflectionHelper;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.ICustomPeripheralProvider;
import openperipheral.api.Volatile;
import dan200.computercraft.api.peripheral.IPeripheral;

@Volatile
public class TileEntityPeripheralProxy extends OpenTileEntity implements ICustomPeripheralProvider, INeighbourAwareTile {

	public static final Class<?> CC_CLASS = ReflectionHelper.getClass("dan200.computercraft.ComputerCraft");

	@Override
	public IPeripheral createPeripheral(int side) {
		final ForgeDirection rotation = getRotation();
		if (rotation.getOpposite().ordinal() != side) return null;

		Object peripheral = null;

		peripheral = ReflectionHelper.callStatic(
				CC_CLASS,
				"getPeripheralAt",
				ReflectionHelper.typed(worldObj, World.class),
				ReflectionHelper.primitive(xCoord + rotation.offsetX),
				ReflectionHelper.primitive(yCoord + rotation.offsetY),
				ReflectionHelper.primitive(zCoord + rotation.offsetZ),
				ReflectionHelper.primitive(0));
		return (peripheral instanceof IPeripheral)? new WrappedPeripheral((IPeripheral)peripheral) : null;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		if (!worldObj.isRemote) {
			ForgeDirection rot = getRotation().getOpposite();
			int attachedX = xCoord + rot.offsetX;
			int attachedY = yCoord + rot.offsetY;
			int attachedZ = zCoord + rot.offsetZ;
			/*
			 * TileEntity attachedTE = worldObj.getBlockTileEntity(attachedX,
			 * attachedY, attachedZ);
			 * if (attachedTE != null &&
			 * CABLE_CLASS.isAssignableFrom(attachedTE.getClass())) {
			 * ReflectionHelper.call(attachedTE, "networkChanged");
			 * }
			 */
			worldObj.notifyBlockOfNeighborChange(
					attachedX,
					attachedY,
					attachedZ,
					OpenPeripheralAddons.Blocks.peripheralProxy.blockID
					);
		}
	}

}
