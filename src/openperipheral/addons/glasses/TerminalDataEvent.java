package openperipheral.addons.glasses;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import openmods.network.EventPacket;
import openmods.network.IEventPacketType;
import openmods.utils.ByteUtils;
import openperipheral.addons.EventTypes;

public class TerminalDataEvent extends EventPacket {

	// just for start, will move logic here
	public byte[] payload;

	@Override
	public IEventPacketType getType() {
		return EventTypes.TERMINAL_DATA;
	}

	@Override
	protected void readFromStream(DataInput input) throws IOException {
		int length = ByteUtils.readVLI(input);
		payload = new byte[length];
		input.readFully(payload);
	}

	@Override
	protected void writeToStream(DataOutput output) throws IOException {
		ByteUtils.writeVLI(output, payload.length);
		output.write(payload);
	}

}
