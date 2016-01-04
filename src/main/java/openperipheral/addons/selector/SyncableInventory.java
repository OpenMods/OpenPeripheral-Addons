package openperipheral.addons.selector;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import openmods.inventory.GenericInventory;
import openmods.sync.ISyncableObject;

public class SyncableInventory extends GenericInventory implements ISyncableObject {

	private boolean dirty = false;

	public SyncableInventory(String name, boolean isInvNameLocalized, int size) {
		super(name, isInvNameLocalized, size);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void markClean() {
		dirty = false;
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	@Override
	public void readFromStream(PacketBuffer stream) throws IOException {
		NBTTagCompound tag = stream.readNBTTagCompoundFromBuffer();
		readFromNBT(tag);
	}

	@Override
	public void writeToStream(PacketBuffer stream) {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		stream.writeNBTTagCompoundToBuffer(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String name) {
		this.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String name) {
		this.readFromNBT(nbt);
	}

}
