package hust.idc.util.matrix;

import hust.idc.util.pair.Pair;
import hust.idc.util.pair.UnorderedPair;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Matrixs {

	@SuppressWarnings("rawtypes")
	public static final Matrix EMPTY_MATRIX = new EmptyMatrix();
	@SuppressWarnings("rawtypes")
	public static final SymmetricMatrix EMPTY_SYMMETRIC_MATRIX = new EmptySymmetricMatrix();

	private Matrixs() {
		// cannot be instantiate
	}

	public static <RK, CK, V> Matrix<RK, CK, V> toMatrix(
			Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> map) {
		return null;
	}

	public static <RK, CK, V> Matrix<RK, CK, V> toMatrix1(
			Map<? extends RK, ? extends Map<? extends CK, ? extends V>> map) {
		return null;
	}

	public static <K, V> SymmetricMatrix<K, V> toSymmetricMatrix(
			Map<? extends UnorderedPair<? extends K>, ? extends V> map) {
		return null;
	}

	public static <RK, CK, V> Matrix<RK, CK, V> synchronizedMatrix(
			Matrix<RK, CK, V> m) {
		return new SynchronizedMatrix<RK, CK, V>(m);
	}

	static <RK, CK, V> Matrix<RK, CK, V> synchronizedMatrix(
			Matrix<RK, CK, V> m, Object mutex) {
		return new SynchronizedMatrix<RK, CK, V>(m, mutex);
	}

	/**
	 * @serial include
	 */
	private static class SynchronizedCollection<E> implements Collection<E>,
			Serializable {
		private static final long serialVersionUID = 3053995032091335093L;

		final Collection<E> c; // Backing Collection
		final Object mutex; // Object on which to synchronize

		SynchronizedCollection(Collection<E> c) {
			this.c = Objects.requireNonNull(c);
			mutex = this;
		}

		SynchronizedCollection(Collection<E> c, Object mutex) {
			this.c = Objects.requireNonNull(c);
			this.mutex = mutex;
		}

		public int size() {
			synchronized (mutex) {
				return c.size();
			}
		}

		public boolean isEmpty() {
			synchronized (mutex) {
				return c.isEmpty();
			}
		}

		public boolean contains(Object o) {
			synchronized (mutex) {
				return c.contains(o);
			}
		}

		public Object[] toArray() {
			synchronized (mutex) {
				return c.toArray();
			}
		}

		public <T> T[] toArray(T[] a) {
			synchronized (mutex) {
				return c.toArray(a);
			}
		}

		public Iterator<E> iterator() {
			return c.iterator(); // Must be manually synched by user!
		}

		public boolean add(E e) {
			synchronized (mutex) {
				return c.add(e);
			}
		}

		public boolean remove(Object o) {
			synchronized (mutex) {
				return c.remove(o);
			}
		}

		public boolean containsAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.containsAll(coll);
			}
		}

		public boolean addAll(Collection<? extends E> coll) {
			synchronized (mutex) {
				return c.addAll(coll);
			}
		}

		public boolean removeAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.removeAll(coll);
			}
		}

		public boolean retainAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.retainAll(coll);
			}
		}

		public void clear() {
			synchronized (mutex) {
				c.clear();
			}
		}

		@Override
		public String toString() {
			synchronized (mutex) {
				return c.toString();
			}
		}

		@Override
		public boolean equals(Object o) {
			synchronized (mutex) {
				return c.equals(o);
			}
		}

		@Override
		public int hashCode() {
			synchronized (mutex) {
				return c.hashCode();
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

	/**
	 * @serial include
	 */
	private static class SynchronizedSet<E> extends SynchronizedCollection<E>
			implements Set<E> {
		private static final long serialVersionUID = 487447009682186044L;

		SynchronizedSet(Set<E> s) {
			super(s);
		}

		SynchronizedSet(Set<E> s, Object mutex) {
			super(s, mutex);
		}
	}

	/**
	 * @serial include
	 */
	private static class SynchronizedMap<K, V> implements Map<K, V>,
			Serializable {
		private static final long serialVersionUID = 1978198479659022715L;

		private final Map<K, V> m; // Backing Map
		final Object mutex; // Object on which to synchronize

		SynchronizedMap(Map<K, V> m, Object mutex) {
			this.m = Objects.requireNonNull(m);
			this.mutex = Objects.requireNonNull(mutex);
		}

		public int size() {
			synchronized (mutex) {
				return m.size();
			}
		}

		public boolean isEmpty() {
			synchronized (mutex) {
				return m.isEmpty();
			}
		}

		public boolean containsKey(Object key) {
			synchronized (mutex) {
				return m.containsKey(key);
			}
		}

		public boolean containsValue(Object value) {
			synchronized (mutex) {
				return m.containsValue(value);
			}
		}

		public V get(Object key) {
			synchronized (mutex) {
				return m.get(key);
			}
		}

		public V put(K key, V value) {
			synchronized (mutex) {
				return m.put(key, value);
			}
		}

		public V remove(Object key) {
			synchronized (mutex) {
				return m.remove(key);
			}
		}

		public void putAll(Map<? extends K, ? extends V> map) {
			synchronized (mutex) {
				m.putAll(map);
			}
		}

		public void clear() {
			synchronized (mutex) {
				m.clear();
			}
		}

		private transient Set<K> keySet = null;
		private transient Set<Map.Entry<K, V>> entrySet = null;
		private transient Collection<V> values = null;

		public Set<K> keySet() {
			synchronized (mutex) {
				if (keySet == null)
					keySet = new SynchronizedSet<K>(m.keySet(), mutex);
				return keySet;
			}
		}

		public Set<Map.Entry<K, V>> entrySet() {
			synchronized (mutex) {
				if (entrySet == null)
					entrySet = new SynchronizedSet<Map.Entry<K, V>>(
							m.entrySet(), mutex);
				return entrySet;
			}
		}

		public Collection<V> values() {
			synchronized (mutex) {
				if (values == null)
					values = new SynchronizedCollection<V>(m.values(), mutex);
				return values;
			}
		}

		public boolean equals(Object o) {
			synchronized (mutex) {
				return m.equals(o);
			}
		}

		public int hashCode() {
			synchronized (mutex) {
				return m.hashCode();
			}
		}

		public String toString() {
			synchronized (mutex) {
				return m.toString();
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

	static abstract class AbstractSynchronizedMatrix<RK, CK, V> implements
			Matrix<RK, CK, V>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -909186674870532405L;
		final Matrix<RK, CK, V> m;
		final Object mutex;

		AbstractSynchronizedMatrix(Matrix<RK, CK, V> m) {
			this.mutex = this.m = Objects.requireNonNull(m);
		}

		AbstractSynchronizedMatrix(Matrix<RK, CK, V> m, Object mutex) {
			super();
			this.m = Objects.requireNonNull(m);
			this.mutex = Objects.requireNonNull(mutex);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.size();
			}
		}

		@Override
		public int rows() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.rows();
			}
		}

		@Override
		public int columns() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.columns();
			}
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.isEmpty();
			}
		}

		@Override
		public boolean containsRow(Object row) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.containsRow(row);
			}
		}

		@Override
		public boolean containsColumn(Object column) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.containsColumn(column);
			}
		}

		@Override
		public boolean containsKey(Object row, Object column) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.containsKey(row, column);
			}
		}

		@Override
		public boolean containsKey(Pair<?, ?> keyPair) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.containsKey(keyPair);
			}
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.containsValue(value);
			}
		}

		@Override
		public V get(Object row, Object column) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.get(row, column);
			}
		}

		@Override
		public V get(Pair<?, ?> keyPair) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.get(keyPair);
			}
		}

		@Override
		public V put(RK row, CK column, V value) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.put(row, column, value);
			}
		}

		@Override
		public V put(Pair<? extends RK, ? extends CK> keyPair, V value) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.put(keyPair, value);
			}
		}

		@Override
		public void putAll(
				Matrix<? extends RK, ? extends CK, ? extends V> matrix) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				m.putAll(matrix);
			}
		}

		@Override
		public void putAll(
				Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> map) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				m.putAll(map);
			}
		}

		@Override
		public V remove(Object row, Object column) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.remove(row, column);
			}
		}

		@Override
		public V remove(Pair<?, ?> keyPair) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return m.remove(keyPair);
			}
		}

		@Override
		public void removeRow(RK row) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				m.removeRow(row);
			}
		}

		@Override
		public void removeColumn(CK column) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				m.removeColumn(column);
			}
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				m.clear();
			}
		}

		private transient Set<RK> rowKeySet = null;
		private transient Set<CK> columnKeySet = null;
		private transient Set<Pair<RK, CK>> keyPairSet = null;
		private transient Set<Matrix.Entry<RK, CK, V>> entrySet = null;
		private transient Collection<V> values = null;

		@Override
		public Set<RK> rowKeySet() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				if (rowKeySet == null)
					rowKeySet = new SynchronizedSet<RK>(m.rowKeySet(), mutex);
				return rowKeySet;
			}
		}

		@Override
		public Set<CK> columnKeySet() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				if (columnKeySet == null)
					columnKeySet = new SynchronizedSet<CK>(m.columnKeySet(),
							mutex);
				return columnKeySet;
			}
		}

		@Override
		public Collection<V> values() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				if (values == null)
					values = new SynchronizedCollection<V>(m.values(), mutex);
				return values;
			}
		}

		@Override
		public Map<CK, V> rowMap(RK row) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return new SynchronizedMap<CK, V>(m.rowMap(row), mutex);
			}
		}

		@Override
		public Map<RK, V> columnMap(CK column) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return new SynchronizedMap<RK, V>(m.columnMap(column), mutex);
			}
		}

		@Override
		public Set<Matrix.Entry<RK, CK, V>> entrySet() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				if (entrySet == null)
					entrySet = new SynchronizedSet<Matrix.Entry<RK, CK, V>>(
							m.entrySet(), mutex);
				return entrySet;
			}
		}

		@Override
		public Set<Pair<RK, CK>> keyPairSet() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				if (keyPairSet == null)
					keyPairSet = new SynchronizedSet<Pair<RK, CK>>(
							m.keyPairSet(), mutex);
				return keyPairSet;
			}
		}

		public boolean equals(Object o) {
			synchronized (mutex) {
				return o == this || m.equals(o);
			}
		}

		public int hashCode() {
			synchronized (mutex) {
				return m.hashCode();
			}
		}

		public String toString() {
			synchronized (mutex) {
				return m.toString();
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

	static class SynchronizedMatrix<RK, CK, V> extends
			AbstractSynchronizedMatrix<RK, CK, V> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5805262262551455865L;

		SynchronizedMatrix(Matrix<RK, CK, V> m) {
			super(m);
		}

		SynchronizedMatrix(Matrix<RK, CK, V> m, Object mutex) {
			super(m, mutex);
		}

	}

	static class SynchronizedSymmetricMatrix<K, V> extends
			AbstractSynchronizedMatrix<K, K, V> implements
			SymmetricMatrix<K, V> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3079460802152770235L;
		final SymmetricMatrix<K, V> sm;

		SynchronizedSymmetricMatrix(SymmetricMatrix<K, V> m) {
			super(m);
			// TODO Auto-generated constructor stub
			this.sm = m;
		}

		SynchronizedSymmetricMatrix(SymmetricMatrix<K, V> m, Object mutex) {
			super(m, mutex);
			// TODO Auto-generated constructor stub
			this.sm = m;
		}

		@Override
		public int dimension() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return sm.dimension();
			}
		}

		@Override
		public boolean containsRowOrColumn(Object key) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return sm.containsRowOrColumn(key);
			}
		}

		@Override
		public void removeRowAndColumn(K key) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				sm.removeRowAndColumn(key);
			}
		}

		@Override
		public Set<K> keySet() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return new SynchronizedSet<K>(sm.keySet(), mutex);
			}
		}

		@Override
		public Map<K, V> keyMap(K key) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return new SynchronizedMap<K, V>(sm.keyMap(key), mutex);
			}
		}

	}

	public static <K, V> SymmetricMatrix<K, V> synchronizedSymmetricMatrix(
			SymmetricMatrix<K, V> m) {
		return new SynchronizedSymmetricMatrix<K, V>(m);
	}

	static <K, V> SymmetricMatrix<K, V> synchronizedSymmetricMatrix(
			SymmetricMatrix<K, V> m, Object mutex) {
		return new SynchronizedSymmetricMatrix<K, V>(m, mutex);
	}

	private static class EmptyMatrix<RK, CK, V> extends
			AbstractMatrix<RK, CK, V> implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6211573460625933629L;

		@Override
		public int size() {
			return 0;
		}

		@Override
		public int rows() {
			return 0;
		}

		@Override
		public int columns() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean containsRow(Object row) {
			return false;
		}

		@Override
		public boolean containsColumn(Object column) {
			return false;
		}

		@Override
		public boolean containsKey(Object row, Object column) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public V get(Object row, Object column) {
			return null;
		}

		@Override
		public Set<RK> rowKeySet() {
			return Collections.<RK> emptySet();
		}

		@Override
		public Set<CK> columnKeySet() {
			return Collections.<CK> emptySet();
		}

		@Override
		public Collection<V> values() {
			return Collections.<V> emptySet();
		}

		@Override
		public Map<CK, V> rowMap(final RK row) {
			return Collections.<CK, V> emptyMap();
		}

		@Override
		public Map<RK, V> columnMap(final CK column) {
			return Collections.<RK, V> emptyMap();
		}

		@Override
		public Set<Entry<RK, CK, V>> entrySet() {
			return Collections.<Entry<RK, CK, V>> emptySet();
		}

		@Override
		public Set<Pair<RK, CK>> keyPairSet() {
			return Collections.<Pair<RK, CK>> emptySet();
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof Matrix) && ((Matrix<?, ?, ?>) o).isEmpty();
		}

		@Override
		public int hashCode() {
			return 0;
		}

		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_MATRIX;
		}
	}

	@SuppressWarnings("unchecked")
	public static final <RK, CK, V> Matrix<RK, CK, V> emptyMatrix() {
		return (Matrix<RK, CK, V>) EMPTY_MATRIX;
	}

	private static class EmptySymmetricMatrix<K, V> extends
			AbstractSymmetricMatrix<K, V> implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3509428096714164354L;

		@Override
		public int size() {
			return 0;
		}

		@Override
		public int dimension() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean containsRowOrColumn(Object key) {
			return false;
		}

		@Override
		public boolean containsKey(Object row, Object column) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public V get(Object row, Object column) {
			return null;
		}

		@Override
		public Set<K> keySet() {
			return Collections.<K> emptySet();
		}

		@Override
		public Set<Matrix.Entry<K, K, V>> entrySet() {
			// TODO Auto-generated method stub
			return Collections.<Entry<K, K, V>> emptySet();
		}

		@Override
		public Collection<V> values() {
			return Collections.<V> emptySet();
		}

		@Override
		public Map<K, V> keyMap(K key) {
			return Collections.<K, V> emptyMap();
		}

		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_SYMMETRIC_MATRIX;
		}
	}

	@SuppressWarnings("unchecked")
	public static final <K, V> SymmetricMatrix<K, V> emptySymmetricMatrix() {
		return (SymmetricMatrix<K, V>) EMPTY_SYMMETRIC_MATRIX;
	}

	static class UnmodifiableMatrix<RK, CK, V> implements Matrix<RK, CK, V>,
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -461622454328532429L;
		final Matrix<RK, CK, V> m;

		UnmodifiableMatrix(Matrix<RK, CK, V> m) {
			this.m = Objects.requireNonNull(m);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return m.size();
		}

		@Override
		public int rows() {
			// TODO Auto-generated method stub
			return m.rows();
		}

		@Override
		public int columns() {
			// TODO Auto-generated method stub
			return m.columns();
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return m.isEmpty();
		}

		@Override
		public boolean containsRow(Object row) {
			// TODO Auto-generated method stub
			return m.containsRow(row);
		}

		@Override
		public boolean containsColumn(Object column) {
			// TODO Auto-generated method stub
			return m.containsColumn(column);
		}

		@Override
		public boolean containsKey(Object row, Object column) {
			// TODO Auto-generated method stub
			return m.containsKey(row, column);
		}

		@Override
		public boolean containsKey(Pair<?, ?> keyPair) {
			// TODO Auto-generated method stub
			return m.containsKey(keyPair);
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return m.containsValue(value);
		}

		@Override
		public V get(Object row, Object column) {
			// TODO Auto-generated method stub
			return m.get(row, column);
		}

		@Override
		public V get(Pair<?, ?> keyPair) {
			// TODO Auto-generated method stub
			return m.get(keyPair);
		}

		@Override
		public V put(RK row, CK column, V value) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public V put(Pair<? extends RK, ? extends CK> keyPair, V value) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(
				Matrix<? extends RK, ? extends CK, ? extends V> matrix) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(
				Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> map) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public V remove(Object row, Object column) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public V remove(Pair<?, ?> keyPair) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeRow(RK row) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeColumn(CK column) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<RK> rowKeySet() {
			// TODO Auto-generated method stub
			return Collections.unmodifiableSet(m.rowKeySet());
		}

		@Override
		public Set<CK> columnKeySet() {
			// TODO Auto-generated method stub
			return Collections.unmodifiableSet(m.columnKeySet());
		}

		@Override
		public Collection<V> values() {
			// TODO Auto-generated method stub
			return Collections.unmodifiableCollection(m.values());
		}

		@Override
		public Map<CK, V> rowMap(RK row) {
			// TODO Auto-generated method stub
			return Collections.unmodifiableMap(m.rowMap(row));
		}

		@Override
		public Map<RK, V> columnMap(CK column) {
			// TODO Auto-generated method stub
			return Collections.unmodifiableMap(m.columnMap(column));
		}

		@Override
		public Set<hust.idc.util.matrix.Matrix.Entry<RK, CK, V>> entrySet() {
			// TODO Auto-generated method stub
			return Collections.unmodifiableSet(m.entrySet());
		}

		@Override
		public Set<Pair<RK, CK>> keyPairSet() {
			// TODO Auto-generated method stub
			return Collections.unmodifiableSet(m.keyPairSet());
		}

		public boolean equals(Object o) {
			return o == this || m.equals(o);
		}

		public int hashCode() {
			return m.hashCode();
		}

		public String toString() {
			return m.toString();
		}

	}

	public static <RK, CK, V> Matrix<RK, CK, V> unmodifiableMatrix(
			Matrix<RK, CK, V> m) {
		return new UnmodifiableMatrix<RK, CK, V>(m);
	}

	static class UnmodifiableSymmetricMatrix<K, V> extends
			UnmodifiableMatrix<K, K, V> implements SymmetricMatrix<K, V>,
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5285545044039804574L;
		final SymmetricMatrix<K, V> sm;

		UnmodifiableSymmetricMatrix(SymmetricMatrix<K, V> m) {
			super(m);
			this.sm = m;
		}

		@Override
		public int dimension() {
			// TODO Auto-generated method stub
			return sm.dimension();
		}

		@Override
		public boolean containsRowOrColumn(Object key) {
			// TODO Auto-generated method stub
			return sm.containsRowOrColumn(key);
		}

		@Override
		public void removeRowAndColumn(K key) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<K> keySet() {
			// TODO Auto-generated method stub
			return Collections.unmodifiableSet(sm.keySet());
		}

		@Override
		public Map<K, V> keyMap(K key) {
			// TODO Auto-generated method stub
			return Collections.unmodifiableMap(sm.keyMap(key));
		}
	}

	public static <K, V> SymmetricMatrix<K, V> unmodifiableMatrix(
			SymmetricMatrix<K, V> m) {
		return new UnmodifiableSymmetricMatrix<K, V>(m);
	}

}
