package net.javelyn;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;

public interface Channel<Key> {
	List<BiConsumer<Key, ByteBuffer>> getReceiveListeners();
	
	default BiConsumer<Key, ByteBuffer> onReceived(BiConsumer<Key, ByteBuffer> listener) {
		getReceiveListeners().add(listener);
		return listener;
	}
	
	Channel header(Number headerLength, BiConsumer<ByteBuffer, Protocol<Key>> protocol);
}