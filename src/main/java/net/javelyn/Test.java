package net.javelyn;

import net.javelyn.evil.Packet;
import net.javelyn.evil.PacketHandler;

import java.nio.ByteBuffer;

public class Test {
	
	public static void main(String[] args) {
		
		BaseChannel<Short> test = new BaseChannel<>();
		test.header(6, (header, protocol) -> {
			protocol.key(header.getShort());
			protocol.length(header.getInt());
		});
		
		test.onReceived((opcode, buffer) -> {
			//test
		});
		
		BaseChannel<Byte> handledChannel = new BaseChannel<>();
		PacketHandler handler = new PacketHandler(handledChannel);
		handler.register(new Packet() {
			@Override
			public byte opcode() {
				return 0;
			}
			
			@Override
			public int length() {
				return 3;
			}
			
			@Override
			public void received(ByteBuffer buffer) {
				byte first = buffer.get();
				byte second = buffer.get();
				byte third = buffer.get();
				//END
			}
		});
	}
	
	
}
