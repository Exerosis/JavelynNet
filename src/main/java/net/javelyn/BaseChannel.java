package net.javelyn;

import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static net.javelyn.Buffer.make;

public class BaseChannel<Key> implements Channel<Key> {
	private final List<BiConsumer<Key, Buffer>> receiveListeners = new ArrayList<>();
	private BiConsumer<Buffer, Protocol<Key>> protocol;
	private final Buffer buffer;
	private final ByteChannel channel;
	
	public BaseChannel(ByteChannel channel) {
		this(2, channel);
	}
	
	public BaseChannel(Number initialCapacity, ByteChannel channel) {
		this(initialCapacity, channel, null);
	}
	
	public BaseChannel(Number initialCapacity, ByteChannel channel, BiConsumer<Buffer, Protocol<Key>> protocol) {
		this.protocol = protocol;
		buffer = make((buffer, depth) -> {
			try {
				buffer.compact().flip();
				while (buffer.remaining() < depth)
					channel.read(buffer);
				buffer.flip();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, initialCapacity.intValue());
		this.channel = channel;
		takeHead();
	}
	
	@Override
	public List<BiConsumer<Key, Buffer>> getReceiveListeners() {
		return receiveListeners;
	}
	
	@Override
	public Channel header(BiConsumer<Buffer, Protocol<Key>> protocol) {
		this.protocol = protocol;
		return this;
	}
	
	private void takeHead() {
		buffer.compact();
		
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
		buffer.dip(length);
		
		//Track the buffers position before letting listeners read.
		int position = buffer.position();
		for (BiConsumer<Key, Buffer> listener : getReceiveListeners())
			listener.accept(key, buffer);
		
		//Make sure the buffer is positioned correctly in case not all bytes are read.
		buffer.position(position + length - 1);
		takeHead();
	}
}
