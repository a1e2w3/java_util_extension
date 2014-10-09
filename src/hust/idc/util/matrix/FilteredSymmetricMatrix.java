package hust.idc.util.matrix;

import hust.idc.util.Filter;
import hust.idc.util.FilteredMap;
import hust.idc.util.FilteredSet;
import hust.idc.util.pair.Pair;
import hust.idc.util.pair.Pairs;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FilteredSymmetricMatrix<K, V> extends
		AbstractSymmetricMatrix<K, V> implements Matrix<K, K, V>,
		SymmetricMatrix<K, V> {

	final SymmetricMatrix<K, V> matrix;
	final Filter<? super K> keyFilter;
	final Filter<? super Pair<K, K>> elementFilter;

	final Filter<Matrix.Entry<K, K, V>> entryFilter;

	public FilteredSymmetricMatrix(SymmetricMatrix<K, V> matrix,
			Filter<K> keyFilter, Filter<? super Pair<K, K>> elementFilter) {
		super();
		this.matrix = Objects.requireNonNull(matrix);
		this.keyFilter = keyFilter;
		this.elementFilter = elementFilter;

		this.entryFilter = new Filter<Matrix.Entry<K, K, V>>() {

			@Override
			public boolean accept(Matrix.Entry<K, K, V> entry) {
				// TODO Auto-generated method stub
				return FilteredSymmetricMatrix.this.accept(entry);
			}

		};
	}

	boolean hasFilter() {
		return keyFilter != null || elementFilter != null;
	}

	@SuppressWarnings("unchecked")
	boolean acceptKey(final Object key) {
		try {
			return keyFilter == null ? true : keyFilter.accept((K) key);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	@SuppressWarnings({ "unchecked" })
	boolean accept(final Object row, final Object column) {
		if(!this.acceptKey(row)
				|| !this.acceptKey(column))
			return false;
		if (elementFilter == null)
			return true;
		try {
			Pair<K, K> pair = Pairs.<K>makeUnorderedPair((K) row, (K) column);
			return elementFilter.accept(pair) && elementFilter.accept(pair.convertPair());
		} catch (Throwable e) {
			return false;
		}
	}

	boolean accept(Entry<K, K, V> entry) {
		return this.acceptKey(entry.getRowKey())
				&& this.acceptKey(entry.getColumnKey())
				&& (elementFilter == null ? true : (elementFilter.accept(entry
						.getKeyPair()) && elementFilter.accept(entry
						.getKeyPair().convertPair())));
	}

	@Override
	public int dimension() {
		// TODO Auto-generated method stub
		return this.keyFilter == null ? this.matrix.dimension() : this.keySet()
				.size();
	}

	@Override
	public boolean containsRowOrColumn(Object key) {
		// TODO Auto-generated method stub
		return this.acceptKey(key) && this.matrix.containsRowOrColumn(key);
	}

	@Override
	public void removeRowAndColumn(K key) {
		// TODO Auto-generated method stub
		if(this.acceptKey(key))
			this.keyMap(key).clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected int valueCount(Object key) {
		// TODO Auto-generated method stub
		return this.acceptKey(key) ? this.keyMap((K) key).size() : 0;
	}

	@Override
	public Map<K, V> keyMap(final K key) {
		// TODO Auto-generated method stub
		return new FilteredMap<K, V>(this.matrix.keyMap(key), new Filter<K>(){

			@Override
			public boolean accept(K otherKey) {
				// TODO Auto-generated method stub
				return FilteredSymmetricMatrix.this.accept(key, otherKey);
			}
			
		});
	}

	transient volatile Set<K> keySet = null;
	transient volatile Set<Matrix.Entry<K, K, V>> entrySet = null;

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		if (keySet == null) {
			keySet = this.keyFilter == null ? this.matrix.keySet() : 
				new FilteredSet<K>(this.matrix.keySet(), this.keyFilter);
		}
		return keySet;
	}

	@Override
	public Set<Matrix.Entry<K, K, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {
			entrySet = this.hasFilter() ? new FilteredSet<Matrix.Entry<K, K, V>>(
					this.matrix.entrySet(), this.entryFilter) : this.matrix
					.entrySet();
		}
		return entrySet;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.hasFilter() ? entrySet().size() : this.matrix.size();
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		return this.accept(row, column) && this.matrix.containsKey(row, column);
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		return this.accept(row, column) ? this.matrix.get(row, column) : null;
	}

	@Override
	public V put(K row, K column, V value) {
		// TODO Auto-generated method stub
		return this.accept(row, column) ? this.matrix.put(row, column, value) : null;
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		return this.accept(row, column) ? this.matrix.remove(row, column) : null;
	}
	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		if (!hasFilter())
			return matrix.isEmpty();
		Iterator<Entry<K, K, V>> entryIt = matrix.entrySet().iterator();
		while (entryIt.hasNext()) {
			if (this.accept(entryIt.next()))
				return false;
		}
		return true;
	}

	@Override
	void clearViews() {
		// TODO Auto-generated method stub
		super.clearViews();
		this.keyPairSet = null;
		this.entrySet = null;
	}
}
