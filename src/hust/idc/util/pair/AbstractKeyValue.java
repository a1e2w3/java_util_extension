package hust.idc.util.pair;

public abstract class AbstractKeyValue<K, V> extends AbstractPair<K, V> implements KeyValue<K, V> {
	
	protected AbstractKeyValue(){
		
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
	public final K getFirst() {
		// TODO Auto-generated method stub
		return this.getKey();
	}

	@Override
	public final V getSecond() {
		// TODO Auto-generated method stub
		return this.getValue();
	}

	@Override
	public final K setFirst(K first) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public final V setSecond(V second) {
		// TODO Auto-generated method stub
		return this.setValue(second);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return (getKey() == this ? "this pair" : getKey()) + "=" + (getValue() == this ? "this pair" : getValue());
	}

}
