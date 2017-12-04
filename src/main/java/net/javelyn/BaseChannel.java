package net.javelyn;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.nio.ByteBuffer.*;

public class BaseChannel<Key> implements Channel<Key> {
	private final List<BiConsumer<Key, ByteBuffer>> receiveListeners = new ArrayList<>();
	private BiConsumer<ByteBuffer, Protocol<Key>> protocol;
	private ByteBuffer buffer;
	
	public BaseChannel() {
		this(2);
	}
	
	public BaseChannel(Number initialCapacity) {
		buffer = allocate(initialCapacity.intValue());
		takeHead();
	}
	
	@Override
	public List<BiConsumer<Key, ByteBuffer>> getReceiveListeners() {
		return receiveListeners;
	}
	
	@Override
	public Channel header(Number headerLength, BiConsumer<ByteBuffer, Protocol<Key>> protocol) {
		if (buffer.capacity() < headerLength.intValue())
			buffer = allocate(headerLength.intValue());
		this.protocol = protocol;
		return null;
	}
	
	private void dip(ByteBuffer buffer) {
		//Fill the packet from the stream;
	}
	
	private void takeHead() {
		buffer.compact();
		//Dip till we have one headers worth of bytes.
		while (buffer.remaining() < buffer.capacity())
			dip(buffer);
		//TODO add some anti corruption measures of some kind.
		
		//Use protocol to find the next packets size.
		protocol.accept(buffer, new Protocol<>() {
			private int length;
			private Key key;
			
			@Override
			public Protocol<Key> key(Key key) {
				if (this.length != 0)
					takeBody(length, key);
				else
					this.key = key;
				return this;
			}
			
			@Override
			public Protocol<Key> length(Number length) {
				if (this.key != null)
					takeBody(length.intValue(), key);
				else
					this.length = length.intValue();
				return this;
			}
		});
	}
	
	private void takeBody(int length, Key key) {
		buffer.compact();
		//Ensure we have a long enough packet buffer.
		if (buffer.capacity() < length)
			buffer = allocate(length);
		//Dip till we have a full packet.
		while (buffer.remaining() < length)
			dip(buffer);
		
		//Track the buffers position before letting listeners read.
		int position = buffer.position();
		for (BiConsumer<Key, ByteBuffer> listener : getReceiveListeners())
			listener.accept(key, buffer);
		
		//Make sure the buffer is positioned correctly in case not all bytes are read.
		buffer.position(position + length - 1);
		takeHead();
	}
}
