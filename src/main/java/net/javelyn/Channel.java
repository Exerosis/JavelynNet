package net.javelyn;

import java.util.List;
import java.util.function.BiConsumer;

public interface Channel<Key> {
	List<BiConsumer<Key, Buffer>> getReceiveListeners();
	
	default BiConsumer<Key, Buffer> onReceived(BiConsumer<Key, Buffer> listener) {
		getReceiveListeners().add(listener);
		return listener;
	}
	
	Channel header(BiConsumer<Buffer, Protocol<Key>> protocol);
}