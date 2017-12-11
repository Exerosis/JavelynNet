package net.javelyn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class Test {
	
	private static int getVarInt(Buffer buffer) {
		int numRead = 0;
		int result = 0;
		
		byte read;
		do {
			read = buffer.next();
			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));
			if (++numRead > 5)
				throw new RuntimeException("VarInt is too big");
		} while ((read & 0b10000000) != 0);
		return result;
	}
	
	public static int getUnsignedShort(Buffer buffer) {
		return buffer.nextShort() & 0xFFFF;
	}
	
	public static String getString(Buffer buffer) {
		return new String(buffer.next(getVarInt(buffer)));
	}
	
	
	public static void main(String[] args) {
		try {
			ServerSocketChannel channel = ServerSocketChannel.open();
			channel.bind(new InetSocketAddress(25565));
			while (channel.isOpen()) {
				BaseChannel<Integer> test = new BaseChannel<>(64, channel.accept(), (header, protocol) -> {
					int length = getVarInt(header);
					int startingPos = header.position();
					int temp = getVarInt(header);
					protocol.key(temp);
					int temp2 = length - (startingPos - header.position());
					protocol.length(temp2);
				});
				
				test.onReceived((opcode, buffer) -> {
					switch (opcode) {
						case 1: {
							int version = getVarInt(buffer);
							String address = getString(buffer);
							int port = getUnsignedShort(buffer);
							int state = getVarInt(buffer);
							System.out.println("Version: " + version);
							System.out.println("Address: " + address);
							System.out.println("Port: " + port);
							System.out.println("State: " + state);
							break;
						}
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
