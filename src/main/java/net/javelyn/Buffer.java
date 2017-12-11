package net.javelyn;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import static java.nio.ByteBuffer.allocateDirect;

public interface Buffer {
	
	static Buffer make(BiConsumer<ByteBuffer, Integer> filler, int initialCapacity) {
		return new Buffer() {
			private ByteBuffer buffer = allocateDirect(initialCapacity);
			
			@Override
			public void dip(int depth) {
				if (buffer().capacity() < depth)
					buffer = allocateDirect(depth);
				filler.accept(buffer, depth);
			}
			
			@Override
			public ByteBuffer buffer() {
				return buffer;
			}
		};
	}
	
	void dip(int depth);
	
	ByteBuffer buffer();
	
	default byte next() {
		dip(1);
		return buffer().get();
	}
	
	default byte[] next(int count) {
		dip(count);
		byte[] bytes = new byte[count];
		buffer().get(bytes);
		return bytes;
	}
	
	default short nextShort() {
		dip(2);
		return buffer().getShort();
	}
	
	default int nextInt() {
		dip(4);
		return buffer().getInt();
	}
	
	default long nextLong() {
		dip(8);
		return buffer().getLong();
	}
	
	default float nextFloat() {
		dip(4);
		return buffer().getFloat();
	}
	
	default double nextDouble() {
		dip(8);
		return buffer().getDouble();
	}
	
	default char nextChar() {
		dip(2);
		return buffer().getChar();
	}
	
	default int remaining() {
		return buffer().remaining();
	}
	
	default void compact() {
		buffer().compact();
	}
	
	default int position() {
		return buffer().position();
	}
	
	default int position(int position) {
		return buffer().position();
	}
}
