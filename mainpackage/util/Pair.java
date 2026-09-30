package util;

import java.util.Objects;

public class Pair<K, V> {

	private K key;
	private V value;
	
	public Pair() {
		key = null;
		value = null;
	}
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}
	
	public void set(K key, V value) {
		setKey(key);
		setValue(value);
	}
	
	public void setKey(K key) {
		this.key = key;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

}
