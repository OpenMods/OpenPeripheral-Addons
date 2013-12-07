package openperipheral.addons.glasses;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import openmods.network.EventPacket;
import openmods.network.IEventPacketType;
import openmods.structured.Command.CommandList;
import openperipheral.addons.EventTypes;

public abstract class TerminalEvent extends EventPacket {

	public long terminalId;
	public boolean isPrivate;

	public TerminalEvent() {}

	public TerminalEvent(long terminalId, boolean isPrivate) {
		this.terminalId = terminalId;
		this.isPrivate = isPrivate;
	}

	@Override
	protected void readFromStream(DataInput input) throws IOException {
		terminalId = input.readLong();
		isPrivate = input.readBoolean();
	}

	@Override
	protected void writeToStream(DataOutput output) throws IOException {
		output.writeLong(terminalId);
		output.writeBoolean(isPrivate);
	}

	@Override
	protected void appendLogInfo(List<String> info) {
		super.appendLogInfo(info);
		info.add(TerminalUtils.formatTerminalId(terminalId));
		info.add(isPrivate? "private" : "public");
	}

	public static class TerminalResetEvent extends TerminalEvent {

		public TerminalResetEvent() {
			super();
		}

		public TerminalResetEvent(long terminalId, boolean isPrivate) {
			super(terminalId, isPrivate);
		}

		@Override
		public IEventPacketType getType() {
			return EventTypes.TERMINAL_RESET;
		}
	}

	public static class TerminalDataEvent extends TerminalEvent {
		public final CommandList commands = new CommandList();

		public TerminalDataEvent() {
			super();
		}

		public TerminalDataEvent(long terminalId, boolean isPrivate) {
			super(terminalId, isPrivate);
		}

		@Override
		public IEventPacketType getType() {
			return EventTypes.TERMINAL_DATA;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			super.readFromStream(input);
			commands.readFromStream(input);
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			super.writeToStream(output);
			commands.writeToStream(output);
		}

		@Override
		protected void appendLogInfo(List<String> info) {
			super.appendLogInfo(info);
			info.add(Integer.toString(commands.size()));
		}
	}
}
