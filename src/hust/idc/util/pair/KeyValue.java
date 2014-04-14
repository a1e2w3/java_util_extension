package hust.idc.util.pair;

public interface KeyValue<K, V> extends Pair<K, V> {
	K getKey();
	
	V getValue();
	
	V setValue(V value);

}
