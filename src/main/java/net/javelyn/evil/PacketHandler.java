package net.javelyn.evil;

import net.javelyn.Channel;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

public class PacketHandler implements BiConsumer<Byte, ByteBuffer> {
	private final Packet[] packets = new Packet[255];
	
	//To support evil ways of doing things >:|
	public PacketHandler(Channel<Byte> parent) {
		parent.header(5, (header, protocol) -> {
			byte key = header.get();
			Packet packet = packets[key];
			if (packet == null)
				throw new RuntimeException("No packet with opcode " + key + " was registered!");
			protocol.key(key).length(packet.length());
		});
		parent.onReceived(this);
	}
	
	public PacketHandler register(Packet packet) {
		packets[packet.opcode()] = packet;
		return this;
	}
	
	@Override
	public void accept(Byte key, ByteBuffer buffer) {
		packets[key].received(buffer);
	}
}
