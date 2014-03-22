package openperipheral.addons.peripheralproxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openmods.Log;
import openmods.api.INeighbourAwareTile;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ReflectionHelper;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.ICustomPeripheralProvider;
import openperipheral.api.Volatile;

import org.apache.commons.lang3.ArrayUtils;

import dan200.computercraft.api.peripheral.IPeripheral;

@Volatile
public class TileEntityPeripheralProxy extends OpenTileEntity implements ICustomPeripheralProvider, INeighbourAwareTile {

	public static final Class<?> CC_CLASS = ReflectionHelper.getClass("dan200.computercraft.ComputerCraft");
	public static final Class<?> CABLE_CLASS = ReflectionHelper.getClass("dan200.computercraft.shared.peripheral.modem.TileCable");

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

		return worldObj.getBlockTileEntity(targetX, targetY, targetZ) != null;
	}

	private void updateModem(ForgeDirection modemDir, boolean reactivate) {
		int attachedX = xCoord + modemDir.offsetX;
		int attachedY = yCoord + modemDir.offsetY;
		int attachedZ = zCoord + modemDir.offsetZ;

		TileEntity attachedModem = worldObj.getBlockTileEntity(attachedX, attachedY, attachedZ);
		if (attachedModem != null && CABLE_CLASS.isAssignableFrom(attachedModem.getClass())) {
			try {
				Field f = ReflectionHelper.getField(CABLE_CLASS, "m_peripheralAccessAllowed");
				f.setAccessible(true);
				boolean isActive = f.getBoolean(attachedModem);

				if (isActive) {
					Method m = ReflectionHelper.getMethod(CABLE_CLASS, ArrayUtils.toArray("togglePeripheralAccess"));
					m.setAccessible(true);
					m.invoke(attachedModem);
					if (reactivate) m.invoke(attachedModem);
				}

			} catch (Throwable t) {
				Log.warn("Failed to update modem %s", attachedModem);
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
