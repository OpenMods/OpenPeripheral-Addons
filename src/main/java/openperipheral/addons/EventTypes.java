package openperipheral.addons;

import openmods.network.*;
import openperipheral.addons.glasses.TerminalEvent.TerminalClearEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalDataEvent;
import openperipheral.addons.glasses.TerminalEvent.TerminalResetEvent;

public enum EventTypes implements IEventPacketType {
	TERMINAL_RESET {

		@Override
		public EventPacket createPacket() {
			return new TerminalResetEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.FROM_CLIENT;
		}

	},
	TERMINAL_CLEAR {

		@Override
		public EventPacket createPacket() {
			return new TerminalClearEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.TO_CLIENT;
		}

	},
	TERMINAL_DATA {
		@Override
		public EventPacket createPacket() {
			return new TerminalDataEvent();
		}

		@Override
		public PacketDirection getDirection() {
			return PacketDirection.TO_CLIENT;
		}

		@Override
		public boolean isCompressed() {
			return true;
		}

		@Override
		public boolean isChunked() {
			return true;
		}
	};

	@Override
	public boolean isCompressed() {
		return false;
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public int getId() {
		return EventIdRanges.OPEN_PERIPHERAL_ADDONS_ID_START + ordinal();
	}

	public static void registerTypes() {
		for (IEventPacketType type : values())
			EventPacketManager.registerType(type);
	}
}
