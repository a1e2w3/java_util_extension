package hust.idc.util.matrix.concurrent;

import hust.idc.util.matrix.AbstractMatrix;
import hust.idc.util.matrix.Matrix;
import hust.idc.util.pair.Pair;

import java.util.Set;

public abstract class AbstractConcurrentMatrix<RK, CK, V> extends
		AbstractMatrix<RK, CK, V> implements ConcurrentMatrix<RK, CK, V> {

	@Override
	public abstract V putIfAbsent(RK row, CK column, V value);

	@Override
	public V putIfAbsent(Pair<? extends RK, ? extends CK> keyPair, V value) {
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
	public abstract boolean replace(RK row, CK column, V oldValue, V newValue);

	@Override
	public boolean replace(Pair<? extends RK, ? extends CK> keyPair,
			V oldValue, V newValue) {
		// TODO Auto-generated method stub
		return replace(keyPair.getFirst(), keyPair.getSecond(), oldValue, newValue);
	}

	@Override
	public abstract V replace(RK row, CK column, V value);

	@Override
	public V replace(Pair<? extends RK, ? extends CK> keyPair, V value) {
		// TODO Auto-generated method stub
		return replace(keyPair.getFirst(), keyPair.getSecond(), value);
	}

	@Override
	public abstract int rows();

	@Override
	public abstract int columns();

	@Override
	public abstract Set<Matrix.Entry<RK, CK, V>> entrySet();

}
