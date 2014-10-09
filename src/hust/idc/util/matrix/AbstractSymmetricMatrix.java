package hust.idc.util.matrix;

import hust.idc.util.pair.AbstractImmutableUnorderedPair;
import hust.idc.util.pair.UnorderedPair;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class AbstractSymmetricMatrix<K, V> extends
		AbstractMatrix<K, K, V> implements SymmetricMatrix<K, V>, Matrix<K, K, V> {

	protected AbstractSymmetricMatrix() {
		super();
	}

	@Override
	public abstract int dimension();

	@Override
	public final int rows() {
		// TODO Auto-generated method stub
		return dimension();
	}

	@Override
	public final int columns(){
		return dimension();
	}
	
	@Override
	public boolean containsRowOrColumn(Object key) {
		// TODO Auto-generated method stub
		Iterator<Entry<K, K, V>> entryIt = entrySet().iterator();
		if (key == null) {
			while (entryIt.hasNext()) {
				Entry<K, K, V> entry = entryIt.next();
				if (null == entry.getRowKey() || null == entry.getColumnKey())
					return true;
			}
		} else {
			while (entryIt.hasNext()) {
				Entry<K, K, V> entry = entryIt.next();
				if (key.equals(entry.getRowKey()) || key.equals(entry.getColumnKey()))
					return true;
			}
		}
		return false;
	}
	
	@Override
	public final boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		return containsRowOrColumn(row);
	}

	@Override
	public final boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		return containsRowOrColumn(column);
	}
	
	@Override
	public void removeRowAndColumn(K key){
		keyMap(key).clear();
	}
	
	@Override
	public final void removeRow(K row) {
		// TODO Auto-generated method stub
		removeRowAndColumn(row);
	}

	@Override
	public final void removeColumn(K column) {
		// TODO Auto-generated method stub
		removeRowAndColumn(column);
	}
	
	protected int valueCount(Object key){
		int count = 0;
		Iterator<Entry<K, K, V>> entryIt = entrySet().iterator();
		if (null == key) {
			while (entryIt.hasNext()) {
				Entry<K, K, V> entry = entryIt.next();
				if (null == entry.getRowKey() || null == entry.getColumnKey()) {
					++count;
				}
			}
		} else {
			while (entryIt.hasNext()) {
				Entry<K, K, V> entry = entryIt.next();
				if (key.equals(entry.getRowKey()) || key.equals(entry.getColumnKey())) {
					++count;
				}
			}
		}
		return count;
	}
	
	abstract class AbstractKeyMapView extends AbstractMap<K, V> {
		final K viewKey;
		
		AbstractKeyMapView(K viewKey){
			this.viewKey = viewKey;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return AbstractSymmetricMatrix.this.valueCount(viewKey);
		}

		@Override
		public V put(K innerKey, V value) {
			// TODO Auto-generated method stub
			return AbstractSymmetricMatrix.this.put(viewKey, innerKey, value);
		}
		
		@Override
		public boolean containsKey(Object key) {
			return AbstractSymmetricMatrix.this.containsKey(key, viewKey);
		}
		
		@Override
		public V get(Object key) {
			return AbstractSymmetricMatrix.this.get(key, viewKey);
		}

		@Override
		public V remove(Object key) {
			// TODO Auto-generated method stub
			return AbstractSymmetricMatrix.this.remove(key, viewKey);
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			AbstractSymmetricMatrix.this.removeRowAndColumn(viewKey);
		}

		transient volatile Set<Map.Entry<K, V>> entrySet = null;
		@Override
		public Set<Map.Entry<K, V>> entrySet(){
			if(entrySet == null){
				entrySet = new AbstractSet<Map.Entry<K, V>>(){

					@Override
					public Iterator<java.util.Map.Entry<K, V>> iterator() {
						// TODO Auto-generated method stub
						return entryIterator();
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return AbstractKeyMapView.this.size();
					}
					
				};
			}
			return entrySet;
		}
		
		abstract Iterator<Map.Entry<K, V>> entryIterator();
	}

	@Override
	public Map<K, V> keyMap(K key){
		return new AbstractKeyMapView(key) {

			@Override
			Iterator<Map.Entry<K, V>> entryIterator() {
				// TODO Auto-generated method stub
				return new Iterator<Map.Entry<K, V>>() {
					Iterator<Matrix.Entry<K, K, V>> iterator = AbstractSymmetricMatrix.this
							.entrySet().iterator();
					Matrix.Entry<K, K, V> next = null;
					boolean keyInRow = false;

					private void getNext() {
						next = null;
						if (null == viewKey) {
							while (iterator.hasNext()) {
								Matrix.Entry<K, K, V> entry = iterator
										.next();
								if (null == entry.getRowKey()) {
									next = entry;
									keyInRow = true;
									break;
								} else if(null == entry.getColumnKey()){
									next = entry;
									keyInRow = false;
									break;
								}
							}
						} else {
							while (iterator.hasNext()) {
								Matrix.Entry<K, K, V> entry = iterator
										.next();
								if (viewKey.equals(entry.getRowKey())) {
									next = entry;
									keyInRow = true;
									break;
								} else if (viewKey.equals(entry.getColumnKey())) {
									next = entry;
									keyInRow = false;
									break;
								}
							}
						}
					}

					@Override
					public boolean hasNext() {
						// TODO Auto-generated method stub
						if (next == null && iterator.hasNext())
							this.getNext();
						return next != null;
					}

					@Override
					public java.util.Map.Entry<K, V> next() {
						// TODO Auto-generated method stub
						if (next == null && iterator.hasNext())
							this.getNext();
						if (next == null)
							throw new NoSuchElementException();
						Map.Entry<K, V> entry = keyInRow ? next.rowMapEntry() : next.columnMapEntry();
						next = null;
						return entry;
					}

					@Override
					public void remove() {
						// TODO Auto-generated method stub
						iterator.remove();
					}

				};
			}

		};
	}
	
	@Override
	public final Map<K, V> rowMap(K row) {
		// TODO Auto-generated method stub
		return keyMap(row);
	}

	@Override
	protected final int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		return valueCount(row);
	}
	
	@Override
	public final Map<K, V> columnMap(K column) {
		// TODO Auto-generated method stub
		return keyMap(column);
	}

	@Override
	protected final int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		return valueCount(column);
	}
	
	@Override 
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final Set<K> rowKeySet() {
		// TODO Auto-generated method stub
		return keySet();
	}

	@Override
	public final Set<K> columnKeySet() {
		// TODO Auto-generated method stub
		return keySet();
	}

	@Override
	public abstract Set<Entry<K, K, V>> entrySet();

	public static abstract class AbstractSymmetricMatrixEntry<K, V> extends
			AbstractEntry<K, K, V> implements
			SymmetricMatrixEntry<K, V>, Entry<K, K, V> {

		protected AbstractSymmetricMatrixEntry() {
			super();
		}

		@Override
		public abstract K getRowKey();

		@Override
		public abstract K getColumnKey();

		@Override
		public abstract V getValue();
		
		@Override
		public UnorderedPair<K> getKeyPair() {
			// TODO Auto-generated method stub
			if (keyPair == null) {
				keyPair = new AbstractImmutableUnorderedPair<K>() {

					@Override
					public K getFirst() {
						// TODO Auto-generated method stub
						return AbstractSymmetricMatrixEntry.this.getRowKey();
					}

					@Override
					public K getSecond() {
						// TODO Auto-generated method stub
						return AbstractSymmetricMatrixEntry.this.getColumnKey();
					}

				};
			}
			return (UnorderedPair<K>) keyPair;
		}

		@Override
		public boolean match(Object row, Object column) {
			// TODO Auto-generated method stub
			return super.match(row, column) || super.match(column, row);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result += ((getRowKey() == null) ? 0 : getRowKey().hashCode());
			result += ((getColumnKey() == null) ? 0 : getColumnKey().hashCode());
			result += ((getValue() == null) ? 0 : getValue().hashCode());
			return result * prime;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Matrix.Entry))
				return false;
			if (!(obj instanceof SymmetricMatrix.SymmetricMatrixEntry)) {
				return ((Matrix.Entry<?, ?, ?>) obj).equals(this);
			}
			SymmetricMatrix.SymmetricMatrixEntry<?, ?> other = (SymmetricMatrix.SymmetricMatrixEntry<?, ?>) obj;
			return other.match(getRowKey(), getColumnKey())
					&& eq(this.getValue(), other.getValue());
		}

	}

	public static abstract class AbstractImmutableSymmetricMatrixEntry<K, V>
			extends AbstractSymmetricMatrixEntry<K, V> implements
			SymmetricMatrixEntry<K, V>, Entry<K, K, V> {

		protected AbstractImmutableSymmetricMatrixEntry() {
			super();
		}

		@Override
		public abstract K getRowKey();

		@Override
		public abstract K getColumnKey();

		@Override
		public abstract V getValue();

		@Override
		public final V setValue(V value) {
			throw new UnsupportedOperationException();
		}

	}

}
