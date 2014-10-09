package hust.idc.util.matrix.concurrent;

import java.io.Serializable;
import java.util.Set;

public class ConcurrentHashSymmetricMatrix<K, V> extends
		AbstractConcurrentSymmetricMatrix<K, V> implements
		ConcurrentSymmetricMatrix<K, V>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7045550870290116363L;

	/**
	 * The default initial capacity for this table, used when not otherwise
	 * specified in a constructor.
	 */
	static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The default load factor for this table, used when not otherwise specified
	 * in a constructor.
	 */
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * The default concurrency level for this table, used when not otherwise
	 * specified in a constructor.
	 */
	static final int DEFAULT_CONCURRENCY_LEVEL = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30 to ensure that entries are indexable using ints.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The minimum capacity for per-segment tables. Must be a power of two, at
	 * least two to avoid immediate resizing on next use after lazy
	 * construction.
	 */
	static final int MIN_SEGMENT_TABLE_CAPACITY = 2;

	/**
	 * The maximum number of segments to allow; used to bound constructor
	 * arguments. Must be power of two less than 1 << 24.
	 */
	static final int MAX_SEGMENTS = 1 << 16; // slightly conservative

	/**
	 * Number of unsynchronized retries in size and containsValue methods before
	 * resorting to locking. This is used to avoid unbounded retries if tables
	 * undergo continuous modification which would make it impossible to obtain
	 * an accurate result.
	 */
	static final int RETRIES_BEFORE_LOCK = 2;

	@Override
	public V putIfAbsent(K row, K column, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object row, Object column, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean replace(K row, K column, V oldValue, V newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V replace(K row, K column, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int dimension() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<hust.idc.util.matrix.Matrix.Entry<K, K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

}
