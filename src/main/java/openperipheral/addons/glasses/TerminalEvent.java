package openperipheral.addons.glasses;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;
import openmods.structured.Command.CommandList;

public abstract class TerminalEvent extends NetworkEvent {

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
		info.add(TerminalUtils.formatTerminalId(terminalId));
		info.add(isPrivate? "private" : "public");
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class TerminalResetEvent extends TerminalEvent {
		public TerminalResetEvent() {
			super();
		}

		public TerminalResetEvent(long terminalId, boolean isPrivate) {
			super(terminalId, isPrivate);
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class TerminalClearEvent extends TerminalEvent {
		public TerminalClearEvent() {}

		public TerminalClearEvent(long terminalId, boolean isPrivate) {
			this.terminalId = terminalId;
			this.isPrivate = isPrivate;
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C, chunked = true, compressed = true)
	public static class TerminalDataEvent extends TerminalEvent {
		public final CommandList commands = new CommandList();

		public TerminalDataEvent() {
			super();
		}

		public TerminalDataEvent(long terminalId, boolean isPrivate) {
			super(terminalId, isPrivate);
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
