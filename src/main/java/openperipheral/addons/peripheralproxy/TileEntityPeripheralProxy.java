package openperipheral.addons.peripheralproxy;

import static openmods.reflection.ReflectionHelper.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.Log;
import openmods.api.INeighbourAwareTile;
import openmods.reflection.SafeClassLoad;
import openmods.tileentity.OpenTileEntity;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.ICustomPeripheralProvider;
import openperipheral.api.Volatile;

import org.apache.commons.lang3.ArrayUtils;

import dan200.computercraft.api.peripheral.IPeripheral;

@Volatile
public class TileEntityPeripheralProxy extends OpenTileEntity implements ICustomPeripheralProvider, INeighbourAwareTile {

	public static final SafeClassLoad CC_CLASS = new SafeClassLoad("dan200.computercraft.ComputerCraft");
	public static final SafeClassLoad CABLE_CLASS = new SafeClassLoad("dan200.computercraft.shared.peripheral.modem.TileCable");

	private Block attachedBlock;

	@Override
	public IPeripheral createPeripheral(int side) {
		final ForgeDirection rotation = getRotation();
		if (rotation.getOpposite().ordinal() != side) return null;

		final int targetX = xCoord + rotation.offsetX;
		final int targetY = yCoord + rotation.offsetY;
		final int targetZ = zCoord + rotation.offsetZ;

		IPeripheral peripheral = callStatic(
				CC_CLASS.get(),
				"getPeripheralAt",
				typed(worldObj, World.class),
				primitive(targetX),
				primitive(targetY),
				primitive(targetZ),
				primitive(0));
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
			ForgeDirection targetDir = getRotation();
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
			final Class<?> cableCls = CABLE_CLASS.get();
			if (attachedModem != null && cableCls.isAssignableFrom(attachedModem.getClass())) {

				Field f = getField(cableCls, "m_peripheralAccessAllowed");
				f.setAccessible(true);
				boolean isActive = f.getBoolean(attachedModem);

				if (isActive) {
					Method m = getMethod(cableCls, ArrayUtils.toArray("togglePeripheralAccess"));
					m.setAccessible(true);
					m.invoke(attachedModem);
					if (reactivate) m.invoke(attachedModem);
				}
			}
		} catch (Throwable t) {
			Log.warn(t, "Failed to update modem %s", attachedModem);
		}

		worldObj.notifyBlockOfNeighborChange(
				attachedX,
				attachedY,
				attachedZ,
				OpenPeripheralAddons.Blocks.peripheralProxy
				);
	}

}
