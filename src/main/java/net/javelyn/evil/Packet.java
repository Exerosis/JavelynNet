package net.javelyn.evil;

import java.nio.ByteBuffer;

public interface Packet {
	byte opcode();
	
	int length();
	
	void received(ByteBuffer buffer);
}