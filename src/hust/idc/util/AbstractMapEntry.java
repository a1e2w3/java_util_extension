package hust.idc.util;

import java.util.Map;

public abstract class AbstractMapEntry<K, V> implements Map.Entry<K, V> {

	protected AbstractMapEntry(){
	}
	
	@Override
	public abstract K getKey();

	@Override
	public abstract V getValue();

	@Override
	public V setValue(V value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
        return (getKey() == null ? 0 :   getKey().hashCode()) ^
                (getValue() == null ? 0 : getValue().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof Map.Entry))
			return false;
		Map.Entry<?, ?> e = (Map.Entry<?, ?>) obj;
		return eq(getKey(), e.getKey()) && eq(getValue(), e.getValue());
	}

	protected static boolean eq(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return (getKey() == this ? "this pair" : getKey()) + "=" + (getValue() == this ? "this pair" : getValue());
	}

}
