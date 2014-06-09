package hust.idc.util;

public interface Filter<K> {
	
	boolean accept(K key);

}
