package openperipheral.addons.peripheralproxy;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.Log;
import openmods.api.INeighbourAwareTile;
import openmods.reflection.*;
import openmods.reflection.MethodAccess.Function0;
import openmods.reflection.MethodAccess.Function5;
import openmods.tileentity.OpenTileEntity;
import openperipheral.api.architecture.cc.ICustomPeripheralProvider;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.peripheral.IPeripheral;

public class TileEntityPeripheralProxy extends OpenTileEntity implements ICustomPeripheralProvider, INeighbourAwareTile {

	private static class Access {
		public final Class<?> ccClass = ReflectionHelper.getClass("dan200.computercraft.ComputerCraft");
		public final Class<?> cableClass = ReflectionHelper.getClass("dan200.computercraft.shared.peripheral.modem.TileCable");

		public final FieldAccess<Boolean> isModemConnected = FieldAccess.create(cableClass, "m_peripheralAccessAllowed");
		public final Function0<Void> togglePeripheral = MethodAccess.create(void.class, cableClass, "togglePeripheralAccess");
		public final Function5<IPeripheral, World, Integer, Integer, Integer, Integer> getPeripheralAt =
				MethodAccess.create(IPeripheral.class, ccClass, World.class, int.class, int.class, int.class, int.class, "getPeripheralAt");

	}

	public static void initAccess() {
		// just dummy method to load and verify access
		Preconditions.checkNotNull(access);
	}

	private final static Access access = new Access();

	private Block attachedBlock;

	@Override
	public IPeripheral createPeripheral(int side) {
		final ForgeDirection rotation = getOrientation().up();
		if (rotation.getOpposite().ordinal() != side) return null;

		final int targetX = xCoord + rotation.offsetX;
		final int targetY = yCoord + rotation.offsetY;
		final int targetZ = zCoord + rotation.offsetZ;

		IPeripheral peripheral = access.getPeripheralAt.call(null, worldObj, targetX, targetY, targetZ, 0);
		if (peripheral != null) {
			attachedBlock = worldObj.getBlock(targetX, targetY, targetZ);
			return new WrappedPeripheral(peripheral);
		} else {
			attachedBlock = null;
			return null;
		}
	}

	@Override
	public void onNeighbourChanged(Block block) {
		if (!worldObj.isRemote && (block == null || block == attachedBlock)) {
			ForgeDirection targetDir = getOrientation().up();
			boolean isConnected = isProxyActive(targetDir);

			ForgeDirection modemDir = targetDir.getOpposite();
			updateModem(modemDir, isConnected);
		}
	}

	private boolean isProxyActive(ForgeDirection targetDir) {
		int targetX = xCoord + targetDir.offsetX;
		int targetY = yCoord + targetDir.offsetY;
		int targetZ = zCoord + targetDir.offsetZ;

		return worldObj.getTileEntity(targetX, targetY, targetZ) != null;
	}

	private void updateModem(ForgeDirection modemDir, boolean reactivate) {
		int attachedX = xCoord + modemDir.offsetX;
		int attachedY = yCoord + modemDir.offsetY;
		int attachedZ = zCoord + modemDir.offsetZ;

		TileEntity attachedModem = worldObj.getTileEntity(attachedX, attachedY, attachedZ);

		try {
			if (attachedModem != null && access.cableClass.isAssignableFrom(attachedModem.getClass())) {
				boolean isActive = access.isModemConnected.get(attachedModem);

				if (isActive) {
					access.togglePeripheral.call(attachedModem);
					if (reactivate) access.togglePeripheral.call(attachedModem);
				}
			}
		} catch (Throwable t) {
			Log.warn(t, "Failed to update modem %s", attachedModem);
		}

		worldObj.notifyBlockOfNeighborChange(attachedX, attachedY, attachedZ, getBlockType());
	}
}
