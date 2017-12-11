package net.javelyn.evil;

import net.javelyn.Buffer;

public interface Packet {
	byte opcode();
	
	int length();
	
	void received(Buffer buffer);
}