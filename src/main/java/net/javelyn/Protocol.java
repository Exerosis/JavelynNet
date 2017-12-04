package net.javelyn;

public interface Protocol<Key> {
	Protocol<Key> key(Key key);
	
	Protocol<Key> length(Number length);
}