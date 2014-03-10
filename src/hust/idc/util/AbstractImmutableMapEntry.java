package hust.idc.util;

import java.util.Map;

public abstract class AbstractImmutableMapEntry<K, V> extends AbstractMapEntry<K, V> implements Map.Entry<K, V> {

	protected AbstractImmutableMapEntry(){
		super();
	}
	
	@Override
	public abstract K getKey();

	@Override
	public abstract V getValue();

	@Override
	public final V setValue(V value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
