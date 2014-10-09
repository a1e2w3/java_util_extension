package hust.idc.util.matrix.concurrent;

import java.util.Set;

import hust.idc.util.matrix.AbstractSymmetricMatrix;
import hust.idc.util.matrix.Matrix;
import hust.idc.util.pair.Pair;

public abstract class AbstractConcurrentSymmetricMatrix<K, V> extends
		AbstractSymmetricMatrix<K, V> implements
		ConcurrentSymmetricMatrix<K, V> {
	
	@Override
	public abstract V putIfAbsent(K row, K column, V value);

	@Override
	public V putIfAbsent(Pair<? extends K, ? extends K> keyPair, V value) {
		// TODO Auto-generated method stub
		return putIfAbsent(keyPair.getFirst(), keyPair.getSecond(), value);
	}

	@Override
	public abstract boolean remove(Object row, Object column, Object value);

	@Override
	public boolean remove(Pair<?, ?> keyPair, Object value) {
		// TODO Auto-generated method stub
		return remove(keyPair.getFirst(), keyPair.getSecond(), value);
	}

	@Override
	public abstract boolean replace(K row, K column, V oldValue, V newValue);

	@Override
	public boolean replace(Pair<? extends K, ? extends K> keyPair,
			V oldValue, V newValue) {
		// TODO Auto-generated method stub
		return replace(keyPair.getFirst(), keyPair.getSecond(), oldValue, newValue);
	}

	@Override
	public abstract V replace(K row, K column, V value);

	@Override
	public V replace(Pair<? extends K, ? extends K> keyPair, V value) {
		// TODO Auto-generated method stub
		return replace(keyPair.getFirst(), keyPair.getSecond(), value);
	}

	@Override
	public abstract int dimension();

	@Override
	public abstract Set<Matrix.Entry<K, K, V>> entrySet();

}
