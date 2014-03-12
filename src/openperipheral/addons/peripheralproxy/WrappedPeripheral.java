package openperipheral.addons.peripheralproxy;

import net.minecraft.nbt.NBTTagCompound;
import dan200.computer.api.*;

public class WrappedPeripheral implements IHostedPeripheral {

	private final IPeripheral peripheral;
	private final int backSide;

	public WrappedPeripheral(IPeripheral peripheral, int backSide) {
		this.peripheral = peripheral;
		this.backSide = backSide;
	}

	@Override
	public String getType() {
		return (peripheral != null)? peripheral.getType() : null;
	}

	@Override
	public String[] getMethodNames() {
		return (peripheral != null)? peripheral.getMethodNames() : null;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		return (peripheral != null)? peripheral.callMethod(computer, context, method, arguments) : null;
	}

	@Override
	public boolean canAttachToSide(int side) {
		return side == backSide;
	}

	@Override
	public void attach(IComputerAccess computer) {
		if (peripheral != null) peripheral.attach(computer);
	}

	@Override
	public void detach(IComputerAccess computer) {
		if (peripheral != null) peripheral.detach(computer);
	}

	@Override
	public void update() {
		if (peripheral instanceof IHostedPeripheral) ((IHostedPeripheral)peripheral).update();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (peripheral instanceof IHostedPeripheral) ((IHostedPeripheral)peripheral).readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (peripheral instanceof IHostedPeripheral) ((IHostedPeripheral)peripheral).writeToNBT(nbttagcompound);
	}

}
