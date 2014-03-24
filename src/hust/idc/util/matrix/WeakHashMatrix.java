package hust.idc.util.matrix;

import hust.idc.util.AbstractMapEntry;
import hust.idc.util.matrix.AbstractMatrix.AbstractEntry;
import hust.idc.util.pair.AbstractImmutablePair;
import hust.idc.util.pair.ObjectPair;
import hust.idc.util.pair.Pair;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;

public class WeakHashMatrix<RK, CK, V> extends AbstractMatrix<RK, CK, V>
		implements Matrix<RK, CK, V> {
	/**
	 * The default initial capacity -- MUST be a power of two.
	 */
	static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load fast used when none specified in constructor.
	 */
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	transient Entry table[][];
	transient final ReferenceQueue<Pair<RK, CK>> queue = new ReferenceQueue<Pair<RK, CK>>();

	transient int size;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 */
	int threshold;

	/**
	 * The load factor for the hash table.
	 */
	final float loadFactor;

	transient volatile int modCount = 0;

	/**
	 * Constructs a new, empty <tt>WeakHashMap</tt> with the default initial
	 * capacity (16) and load factor (0.75).
	 */
	public WeakHashMatrix() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_INITIAL_CAPACITY);
		initBuckets(DEFAULT_INITIAL_CAPACITY, DEFAULT_INITIAL_CAPACITY);
		init();
	}

	public WeakHashMatrix(int initialRows, int initialColumns) {
		this(initialRows, initialColumns, DEFAULT_LOAD_FACTOR);
	}

	public WeakHashMatrix(int initialRows, int initialColumns, float loadFactor) {
		super();
		if (initialRows < 0)
			throw new IllegalArgumentException("Illegal Rows: " + initialRows);
		if (initialColumns < 0)
			throw new IllegalArgumentException("Illegal Columns: "
					+ initialColumns);

		int rowCapacity = ensureCapacity(Math
				.min(MAXIMUM_CAPACITY, initialRows));
		int columnCapacity = ensureCapacity(Math.min(MAXIMUM_CAPACITY,
				initialColumns));
		this.loadFactor = loadFactor;
		threshold = (int) (rowCapacity * columnCapacity * loadFactor);
		initBuckets(rowCapacity, columnCapacity);
		init();
	}

	public WeakHashMatrix(
			Matrix<? extends RK, ? extends CK, ? extends V> otherMatrix) {
		this(Math.max((int) (otherMatrix.rows() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), Math.max(
				(int) (otherMatrix.columns() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
		this.putAll(otherMatrix);
	}

	public WeakHashMatrix(
			Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> otherMatrix) {
		this();
		this.putAll(otherMatrix);
	}

	@SuppressWarnings("unchecked")
	void initBuckets(int rowCapacity, int columnCapacity) {
		table = new WeakHashMatrix.Entry[rowCapacity][columnCapacity];
	}

	/**
	 * Initialization hook for subclasses. This method is called in all
	 * constructors and pseudo-constructors (clone, readObject) after HashMatrix
	 * has been initialized but before any entries have been inserted. (In the
	 * absence of this method, readObject would require explicit knowledge of
	 * subclasses.)
	 */
	void init() {
	}

	private static int ensureCapacity(int minCapacity) {
		int capacity = 1;
		while (capacity < minCapacity)
			capacity <<= 1;
		return capacity;
	}

	/**
	 * Value representing null keys inside tables.
	 */
	private static final Object NULL_KEY = new Object();

	/**
	 * Use NULL_KEY for key if it is null.
	 */
	private static Object maskNull(Object key) {
		return (key == null ? NULL_KEY : key);
	}

	/**
	 * Returns internal representation of null key back to caller as null.
	 */
	@SuppressWarnings("unchecked")
	private static <K> K unmaskNull(Object key) {
		return (K) (key == NULL_KEY ? null : key);
	}

	@SuppressWarnings("unchecked")
	private void expungeStaleEntries() {
		Entry e;
		while ((e = (Entry) queue.poll()) != null) {
			// remove entry;
			int row = HashMatrix.indexFor(e.rowHash(), rowCapacity());
			int column = HashMatrix.indexFor(e.columnHash(), columnCapacity());
			Entry entry = table[row][column], prev = null;
			while (entry != null && entry != e) {
				prev = entry;
				entry = entry.next;
			}
			if (entry == e) {
				if (prev == null) {
					table[row][column] = e.next;
				} else {
					prev.next = e.next;
				}
			}
			e.dispose();
			--size;
		}
	}

	/**
	 * Returns the table after first expunging stale entries.
	 */
	private Entry[][] getTable() {
		expungeStaleEntries();
		return table;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public int rows() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int columns() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		Object k = maskNull(row);
		int hash = HashMatrix.hash(k.hashCode());
		int index = HashMatrix.indexFor(hash, rowCapacity());

		Entry[][] tab = getTable();
		for (int j = 0; j < columnCapacity(); ++j) {
			Entry entry = tab[index][j];
			while (entry != null) {
				if (entry.rowHash() == hash && k.equals(entry.maskedRowKey()))
					return true;
				entry = entry.next;
			}
		}
		return false;
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		Object k = maskNull(column);
		int hash = HashMatrix.hash(k.hashCode());
		int index = HashMatrix.indexFor(hash, columnCapacity());

		Entry[][] tab = getTable();
		for (int i = 0; i < rowCapacity(); ++i) {
			Entry entry = tab[i][index];
			while (entry != null) {
				if (entry.columnHash() == hash
						&& k.equals(entry.maskedColumnKey()))
					return true;
				entry = entry.next;
			}
		}
		return false;
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		Object rowMask = maskNull(row), columnMask = maskNull(column);
		int rowHash = HashMatrix.hash(rowMask.hashCode());
		int rowIndex = HashMatrix.indexFor(rowHash, rowCapacity());
		int columnHash = HashMatrix.hash(columnMask.hashCode());
		int columnIndex = HashMatrix.indexFor(columnHash, columnCapacity());

		Entry[][] tab = getTable();
		Entry entry = tab[rowIndex][columnIndex];
		while (entry != null) {
			if (entry.matchHash(rowHash, columnHash)
					&& entry.matchMasked(rowMask, columnMask))
				return true;
			entry = entry.next;
		}
		return false;
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		Object rowMask = maskNull(row), columnMask = maskNull(column);
		int rowHash = HashMatrix.hash(rowMask.hashCode());
		int rowIndex = HashMatrix.indexFor(rowHash, rowCapacity());
		int columnHash = HashMatrix.hash(columnMask.hashCode());
		int columnIndex = HashMatrix.indexFor(columnHash, columnCapacity());

		Entry[][] tab = getTable();
		Entry entry = tab[rowIndex][columnIndex];
		while (entry != null) {
			if (entry.matchHash(rowHash, columnHash)
					&& entry.matchMasked(rowMask, columnMask))
				return entry.getValue();
			entry = entry.next;
		}
		return null;
	}

	// @SuppressWarnings("unchecked")
	// void resizeRow() {
	// // TODO Auto-generated method stub
	// if (this.rowCapacity() == MAXIMUM_CAPACITY) {
	// threshold = Integer.MAX_VALUE;
	// return;
	// }
	// ++modCount;
	// int newCapacity = this.rowCapacity() << 1;
	//
	// threshold = (int) (newCapacity * columnCapacity() * loadFactor);
	// Entry[][] newTable = new WeakHashMatrix.Entry[newCapacity][this
	// .columnCapacity()];
	// transferEntrys(newTable);
	// table = newTable;
	// }
	//
	// @SuppressWarnings("unchecked")
	// void resizeColumn() {
	// // TODO Auto-generated method stub
	// if (this.columnCapacity() == MAXIMUM_CAPACITY) {
	// threshold = Integer.MAX_VALUE;
	// return;
	// }
	//
	// ++modCount;
	// int newCapacity = this.columnCapacity() << 1;
	// threshold = (int) (rowCapacity() * newCapacity * loadFactor);
	//
	// Entry[][] newTable = new
	// WeakHashMatrix.Entry[this.rowCapacity()][newCapacity];
	// transferEntrys(newTable);
	// table = newTable;
	// }

	@SuppressWarnings("unchecked")
	void resize() {
		// TODO Auto-generated method stub
		boolean canResize = false;

		int newRowCapacity = this.rowCapacity(), newColumnCapacity = this
				.columnCapacity();
		if (this.rowCapacity() < MAXIMUM_CAPACITY) {
			newRowCapacity = this.rowCapacity() << 1;
			canResize = true;
		}

		if (this.columnCapacity() < MAXIMUM_CAPACITY) {
			newColumnCapacity = this.columnCapacity() << 1;
			canResize = true;
		}

		if (!canResize) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		++modCount;
		threshold = (int) (newRowCapacity * newColumnCapacity * loadFactor);

		Entry[][] newTable = new WeakHashMatrix.Entry[newRowCapacity][newColumnCapacity];
		transferEntrys(newTable);
		table = newTable;
	}

	void transferEntrys(Entry[][] newTable) {
		// TODO Auto-generated method stub
		int rowCapacity = newTable.length;
		int columnCapacity = newTable[0].length;

		for (int i = 0; i < this.rowCapacity(); ++i) {
			for (int j = 0; j < this.columnCapacity(); ++j) {
				Entry entry = table[i][j];
				while (entry != null) {
					Entry next = entry.next;
					if (entry.get() == null) {
						entry.dispose();
						--size;
					} else {
						int rowIndex = HashMatrix.indexFor(entry.rowHash(),
								rowCapacity);
						int columnIndex = HashMatrix.indexFor(
								entry.columnHash(), columnCapacity);
						entry.next = newTable[rowIndex][columnIndex];
						newTable[rowIndex][columnIndex] = entry;
					}
					entry = next;
				}
				table[i][j] = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		RK rowMask = (RK) maskNull(row);
		int rowHash = HashMatrix.hash(rowMask.hashCode());
		int rowIndex = HashMatrix.indexFor(rowHash, rowCapacity());
		CK columnMask = (CK) maskNull(column);
		int columnHash = HashMatrix.hash(columnMask.hashCode());
		int columnIndex = HashMatrix.indexFor(columnHash, columnCapacity());

		Entry[][] tab = getTable();
		for (Entry e = tab[rowIndex][columnIndex]; e != null; e = e.next) {
			if (e.matchHash(rowHash, columnHash)
					&& e.matchMasked(rowMask, columnMask)) {
				return e.setValue(value);
			}
		}

		modCount++;
		Entry e = tab[rowIndex][columnIndex];
		tab[rowIndex][columnIndex] = new Entry(new ObjectPair<RK, CK>(rowMask,
				columnMask), value, rowHash, columnHash, e);
		if (++size >= threshold)
			resize();
		return null;
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		Object rowMask = maskNull(row), columnMask = maskNull(column);
		int rowHash = HashMatrix.hash(rowMask.hashCode());
		int rowIndex = HashMatrix.indexFor(rowHash, rowCapacity());
		int columnHash = HashMatrix.hash(columnMask.hashCode());
		int columnIndex = HashMatrix.indexFor(columnHash, columnCapacity());
		Entry[][] tab = getTable();

		Entry prev = tab[rowIndex][columnIndex];
		Entry e = prev;

		while (e != null) {
			Entry next = e.next;
			if (e.matchHash(rowHash, columnHash)
					&& e.matchMasked(rowMask, columnMask)) {
				modCount++;
				size--;
				if (prev == e)
					tab[rowIndex][columnIndex] = next;
				else
					prev.next = next;
				V oldValue = e.getValue();
				e.dispose();
				return oldValue;
			}
			prev = e;
			e = next;
		}

		return null;
	}

	@Override
	public void removeRow(RK row) {
		// TODO Auto-generated method stub
		Object rowMask = maskNull(row);
		int rowHash = HashMatrix.hash(rowMask.hashCode());
		int rowIndex = HashMatrix.indexFor(rowHash, rowCapacity());

		boolean removed = false;
		Entry[][] tab = getTable();
		for (int j = 0; j < columnCapacity(); ++j) {
			Entry prev = tab[rowIndex][j];
			Entry e = prev;

			while (e != null) {
				Entry next = e.next;
				if (e.rowHash() == rowHash && rowMask.equals(e.maskedRowKey())) {
					removed = true;
					size--;
					if (prev == e)
						tab[rowIndex][j] = next;
					else
						prev.next = next;
					e.dispose();
				}
				prev = e;
				e = next;
			}
		}
		if (removed)
			++modCount;
	}

	@Override
	public void removeColumn(CK column) {
		// TODO Auto-generated method stub
		Object columnMask = maskNull(column);
		int columnHash = HashMatrix.hash(columnMask.hashCode());
		int columnIndex = HashMatrix.indexFor(columnHash, columnCapacity());

		boolean removed = false;
		Entry[][] tab = getTable();
		for (int i = 0; i < rowCapacity(); ++i) {
			Entry prev = tab[i][columnIndex];
			Entry e = prev;

			while (e != null) {
				Entry next = e.next;
				if (e.columnHash() == columnHash
						&& columnMask.equals(e.maskedColumnKey())) {
					removed = true;
					size--;
					if (prev == e)
						tab[i][columnIndex] = next;
					else
						prev.next = next;
					e.dispose();
				}
				prev = e;
				e = next;
			}
		}
		if (removed)
			++modCount;
	}

	/**
	 * Removes all of the mappings from this matrix. The matrix will be empty
	 * after this call returns.
	 */
	public void clear() {
		// clear out ref queue. We don't need to expunge entries
		// since table is getting cleared.
		while (queue.poll() != null)
			;

		modCount++;
		Entry[][] tab = table;
		for (int i = 0; i < tab.length; ++i) {
			for (int j = 0; j < tab[i].length; ++i) {
				tab[i][j].dispose();
				tab[i][j] = null;
			}
		}
		size = 0;

		// Allocation of array may have caused GC, which may have caused
		// additional entries to go stale. Removing these entries from the
		// reference queue will make them eligible for reclamation.
		while (queue.poll() != null)
			;
	}

	@Override
	public Map<CK, V> rowMap(RK row) {
		// TODO Auto-generated method stub
		return super.rowMap(row);
	}

	@Override
	public Map<RK, V> columnMap(CK column) {
		// TODO Auto-generated method stub
		return super.columnMap(column);
	}

	@Override
	protected int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		Object rowMask = maskNull(row);
		int rowHash = HashMatrix.hash(rowMask.hashCode());
		int rowIndex = HashMatrix.indexFor(rowHash, rowCapacity());

		int count = 0;
		Entry[][] tab = getTable();

		for (int j = 0; j < columnCapacity(); ++j) {
			Entry prev = tab[rowIndex][j];
			Entry e = prev;

			while (e != null) {
				Entry next = e.next;
				if (e.rowHash() == rowHash && rowMask.equals(e.maskedRowKey())) {
					++count;
				}
				prev = e;
				e = next;
			}
		}
		return count;
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		Object columnMask = maskNull(column);
		int columnHash = HashMatrix.hash(columnMask.hashCode());
		int columnIndex = HashMatrix.indexFor(columnHash, columnCapacity());

		int count = 0;
		Entry[][] tab = getTable();
		for (int i = 0; i < rowCapacity(); ++i) {
			Entry prev = tab[i][columnIndex];
			Entry e = prev;

			while (e != null) {
				Entry next = e.next;
				if (e.columnHash() == columnHash
						&& columnMask.equals(e.maskedColumnKey())) {
					++count;
				}
				prev = e;
				e = next;
			}
		}
		return count;
	}

	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<CK> columnKeySet() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	// View
	// protected transient volatile Set<RK> rowKeySet = null;
	// protected transient volatile Set<CK> columnKeySet = null;
	protected transient volatile Set<Matrix.Entry<RK, CK, V>> entrySet = null;

	@Override
	public Set<Matrix.Entry<RK, CK, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {
			entrySet = new AbstractSet<Matrix.Entry<RK, CK, V>>() {

				@Override
				public Iterator<Matrix.Entry<RK, CK, V>> iterator() {
					// TODO Auto-generated method stub
					return new EntryIterator();
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return WeakHashMatrix.this.size;
				}

			};
		}
		return entrySet;
	}

	private abstract class FastFailedIterator<E> implements Iterator<E> {
		int expectedModCount;

		FastFailedIterator() {
			expectedModCount = WeakHashMatrix.this.modCount;
		}

		void checkModCount() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}

	private final class EntryIterator extends
			FastFailedIterator<Matrix.Entry<RK, CK, V>> {
		int row, column;
		Entry entry = null;
		Entry lastReturned = null;

		/**
		 * Strong reference needed to avoid disappearance of key between hasNext
		 * and next
		 */
		Pair<RK, CK> nextKey = null;

		/**
		 * Strong reference needed to avoid disappearance of key between
		 * nextEntry() and any use of the entry
		 */
		Pair<RK, CK> currentKey = null;

		private EntryIterator() {
			super();
			row = (size() == 0 ? 0 : table.length);
			column = (row == 0 ? 0 : table[0].length);
		}

		public boolean hasNext() {
			Entry[][] t = table;

			while (nextKey == null) {
				Entry e = entry;
				int rowIndex = row;
				int columnIndex = column;
				while (e == null && rowIndex > 0) {
					while (e == null && columnIndex > 0)
						e = t[rowIndex][--columnIndex];
					if(e == null)
						--rowIndex;
				}
				entry = e;
				row = rowIndex;
				column = columnIndex;
				if (e == null) {
					currentKey = null;
					return false;
				}
				nextKey = e.get(); // hold on to key in strong ref
				if (nextKey == null)
					entry = entry.next;
			}
			return true;
		}

		/** The common parts of next() across different types of iterators */
		protected Entry nextEntry() {
			checkModCount();
			if (nextKey == null && !hasNext())
				throw new NoSuchElementException();

			lastReturned = entry;
			entry = entry.next;
			currentKey = nextKey;
			nextKey = null;
			return lastReturned;
		}

		@Override
		public Matrix.Entry<RK, CK, V> next() {
			// TODO Auto-generated method stub
			return nextEntry();
		}

		public void remove() {
			if (lastReturned == null)
				throw new IllegalStateException();
			checkModCount();

			WeakHashMatrix.this.remove(currentKey);
			expectedModCount = modCount;
			lastReturned = null;
			currentKey = null;
		}

	}

	private class Entry extends WeakReference<Pair<RK, CK>> implements
			Matrix.Entry<RK, CK, V> {
		V value;
		Entry next;
		final int rowHash, columnHash;

		public Entry(Pair<RK, CK> reference, V value, int rowHash,
				int columnHash, Entry next) {
			super(reference, queue);
			// TODO Auto-generated constructor stub
			this.rowHash = rowHash;
			this.columnHash = rowHash;
			this.value = value;
			this.next = next;
		}

		@Override
		public RK getRowKey() {
			// TODO Auto-generated method stub
			return WeakHashMatrix.<RK> unmaskNull(maskedRowKey());
		}

		@Override
		public CK getColumnKey() {
			// TODO Auto-generated method stub
			return WeakHashMatrix.<CK> unmaskNull(maskedColumnKey());
		}
		
		RK maskedRowKey() {
			Pair<RK, CK> pair = get();
			return pair == null ? null : pair.getFirst();
		}
		
		CK maskedColumnKey() {
			Pair<RK, CK> pair = get();
			return pair == null ? null : pair.getSecond();
		}
		
		protected transient volatile Pair<RK, CK> keyPair = null;

		@Override
		public Pair<RK, CK> getKeyPair() {
			// TODO Auto-generated method stub
			if (keyPair == null) {
				keyPair = new AbstractImmutablePair<RK, CK>() {

					@Override
					public RK getFirst() {
						// TODO Auto-generated method stub
						return Entry.this.getRowKey();
					}

					@Override
					public CK getSecond() {
						// TODO Auto-generated method stub
						return Entry.this.getColumnKey();
					}

				};
			}
			return keyPair;
		}

		@Override
		public V getValue() {
			// TODO Auto-generated method stub
			return value;
		}

		@Override
		public V setValue(V value) {
			// TODO Auto-generated method stub
			V oldValue = this.value;
			if (oldValue != value)
				this.value = value;
			return oldValue;
		}

		// View
		protected transient volatile Map.Entry<CK, V> rowMapEntry = null;
		protected transient volatile Map.Entry<RK, V> columnMapEntry = null;

		@Override
		public Map.Entry<CK, V> rowMapEntry() {
			// TODO Auto-generated method stub
			if (rowMapEntry == null) {
				rowMapEntry = new AbstractMapEntry<CK, V>() {

					@Override
					public CK getKey() {
						// TODO Auto-generated method stub
						return Entry.this.getColumnKey();
					}

					@Override
					public V getValue() {
						// TODO Auto-generated method stub
						return Entry.this.getValue();
					}

					@Override
					public V setValue(V value) {
						// TODO Auto-generated method stub
						return Entry.this.setValue(value);
					}
				};
			}
			return rowMapEntry;
		}

		@Override
		public Map.Entry<RK, V> columnMapEntry() {
			// TODO Auto-generated method stub
			if (columnMapEntry == null) {
				columnMapEntry = new AbstractMapEntry<RK, V>() {

					@Override
					public RK getKey() {
						// TODO Auto-generated method stub
						return Entry.this.getRowKey();
					}

					@Override
					public V getValue() {
						// TODO Auto-generated method stub
						return Entry.this.getValue();
					}

					@Override
					public V setValue(V value) {
						// TODO Auto-generated method stub
						return Entry.this.setValue(value);
					}
				};
			}
			return columnMapEntry;
		}

		int rowHash() {
			return rowHash;
		}

		int columnHash() {
			return columnHash;
		}

		boolean matchHash(int rowHash, int columnHash) {
			return rowHash == rowHash() && columnHash == columnHash();
		}

		@Override
		public boolean match(Object row, Object column) {
			// TODO Auto-generated method stub
			return matchMasked(maskNull(row), maskNull(column));
		}

		boolean matchMasked(Object row, Object column) {
			if (this.get() == null)
				return false;
			return eq(row, this.maskedRowKey()) && eq(column, this.maskedColumnKey());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = rowHash() + 1 + (columnHash() + 1) * prime;
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
			if (obj instanceof WeakHashMatrix.Entry) {
				WeakHashMatrix<?, ?, ?>.Entry other = (WeakHashMatrix<?, ?, ?>.Entry) obj;
				if (!other.matchHash(this.rowHash(), this.columnHash()))
					return false;
				return other.matchMasked(maskedRowKey(), maskedColumnKey())
						&& eq(this.getValue(), other.getValue());
			} else {
				Matrix.Entry<?, ?, ?> other = (Matrix.Entry<?, ?, ?>) obj;
				return other.match(getRowKey(), getColumnKey())
						&& eq(this.getValue(), other.getValue());
			}
		}

		@Override
		public String toString() {
			return getKeyPair() + "=" + getValue();
		}

		void dispose() {
			this.clear();
			value = null;
			keyPair = null;
			rowMapEntry = null;
			columnMapEntry = null;
			next = null;
		}

	}

	int rowCapacity() {
		return table.length;
	}

	int columnCapacity() {
		if (table.length == 0)
			return 0;
		return table[0].length;
	}

	float loadFactor() {
		return loadFactor;
	}

}
