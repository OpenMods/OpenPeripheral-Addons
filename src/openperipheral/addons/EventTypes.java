package openperipheral.addons;

import openmods.network.EventIdRanges;
import openmods.network.EventPacket;
import openmods.network.IEventPacketType;
import openmods.network.PacketDirection;
import openperipheral.addons.glasses.TerminalDataEvent;

public enum EventTypes implements IEventPacketType {
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
	};

	@Override
	public boolean isCompressed() {
		return false;
	}

	@Override
	public int getId() {
		return EventIdRanges.OPEN_PERIPHERAL_ADDONS_ID_START + ordinal();
	}

	public static void registerTypes() {
		for (IEventPacketType type : values())
			EventPacket.registerType(type);
	}
}
