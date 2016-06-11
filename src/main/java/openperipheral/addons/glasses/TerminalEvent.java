package openperipheral.addons.glasses;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;
import openmods.structured.Command.CommandList;
import openperipheral.addons.glasses.client.SurfaceClient;

public abstract class TerminalEvent extends NetworkEvent {

	public long terminalId;

	public TerminalEvent() {}

	public TerminalEvent(long terminalId) {
		this.terminalId = terminalId;
	}

	@Override
	protected void readFromStream(DataInput input) throws IOException {
		terminalId = input.readLong();
	}

	@Override
	protected void writeToStream(DataOutput output) throws IOException {
		output.writeLong(terminalId);
	}

	@Override
	protected void appendLogInfo(List<String> info) {
		info.add(TerminalUtils.formatTerminalId(terminalId));
	}

	public abstract SurfaceType getSurfaceType();

	public abstract static class Reset extends TerminalEvent {
		public Reset() {
			super();
		}

		public Reset(long terminalId) {
			super(terminalId);
		}
	}

	public abstract static class DrawableReset extends Reset {
		public DrawableReset() {}

		public DrawableReset(long terminalId) {
			super(terminalId);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class PublicDrawableReset extends DrawableReset {
		public PublicDrawableReset() {}

		public PublicDrawableReset(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.GLOBAL;
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class PublicStructureReset extends StructureReset {
		public PublicStructureReset() {}

		public PublicStructureReset(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.GLOBAL;
		}
	}

	public abstract static class StructureReset extends Reset {
		public StructureReset() {}

		public StructureReset(long terminalId) {
			super(terminalId);
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class PrivateDrawableReset extends DrawableReset {
		public PrivateDrawableReset() {}

		public PrivateDrawableReset(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.PRIVATE;
		}
	}

	@NetworkEventMeta(direction = EventDirection.C2S)
	public static class PrivateStructureReset extends StructureReset {
		public PrivateStructureReset() {}

		public PrivateStructureReset(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.PRIVATE;
		}
	}

	public abstract static class Clear extends TerminalEvent {
		public Clear() {}

		public Clear(long terminalId) {
			super(terminalId);
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class PrivateClear extends Clear {
		public PrivateClear() {}

		public PrivateClear(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.PRIVATE;
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class PublicClear extends Clear {
		public PublicClear() {}

		public PublicClear(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.GLOBAL;
		}
	}

	public abstract static class Data extends TerminalEvent {
		public final CommandList commands = new CommandList();

		public Data() {}

		public Data(long terminalId) {
			super(terminalId);
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

		public SurfaceClient createSurface() {
			throw new AbstractMethodError();
		}
	}

	public abstract static class DrawableData extends Data {

		public DrawableData() {}

		public DrawableData(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceClient createSurface() {
			return SurfaceClient.createPrivateSurface(terminalId);
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C, chunked = true, compressed = true)
	public static class PrivateDrawableData extends DrawableData {

		public PrivateDrawableData() {}

		public PrivateDrawableData(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.PRIVATE;
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C, chunked = true, compressed = true)
	public static class PrivateStructureData extends StructureData {
		public PrivateStructureData() {}

		public PrivateStructureData(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.PRIVATE;
		}
	}

	public abstract static class StructureData extends Data {
		public StructureData() {}

		public StructureData(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceClient createSurface() {
			return SurfaceClient.createPublicSurface(terminalId);
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C, chunked = true, compressed = true)
	public static class PublicStructureData extends StructureData {
		public PublicStructureData() {}

		public PublicStructureData(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.GLOBAL;
		}
	}

	@NetworkEventMeta(direction = EventDirection.S2C, chunked = true, compressed = true)
	public static class PublicDrawableData extends DrawableData {

		public PublicDrawableData() {}

		public PublicDrawableData(long terminalId) {
			super(terminalId);
		}

		@Override
		public SurfaceType getSurfaceType() {
			return SurfaceType.GLOBAL;
		}
	}

}
