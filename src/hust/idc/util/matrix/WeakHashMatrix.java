package hust.idc.util.matrix;

import hust.idc.util.AbstractMapEntry;
import hust.idc.util.matrix.Matrix.Entry;
import hust.idc.util.pair.AbstractImmutablePair;
import hust.idc.util.pair.Pair;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

public class WeakHashMatrix<RK, CK, V> extends AbstractMatrix<RK, CK, V>
		implements Matrix<RK, CK, V> {
	/**
	 * The default initial capacity -- MUST be a power of two.
	 */
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30.
	 */
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load fast used when none specified in constructor.
	 */
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private Entry table[][];
	private Head<RK> rowKeys[];
	private Head<CK> columnKeys[];
	private final ReferenceQueue<Pair<RK, CK>> queue = new ReferenceQueue<Pair<RK, CK>>();

	private int size, rows, columns;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 */
	private int rowThreshold, columnThreshold;

	/**
	 * The load factor for the hash table.
	 */
	private final float loadFactor;

	private volatile int modCount = 0;

	/**
	 * Constructs a new, empty <tt>WeakHashMap</tt> with the default initial
	 * capacity (16) and load factor (0.75).
	 */
	@SuppressWarnings("unchecked")
	public WeakHashMatrix() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		rowThreshold = columnThreshold = (int) (DEFAULT_INITIAL_CAPACITY);
		table = new WeakHashMatrix.Entry[DEFAULT_INITIAL_CAPACITY][DEFAULT_INITIAL_CAPACITY];
		// rowSizes = new int[DEFAULT_INITIAL_CAPACITY];
		// columnSizes = new int[DEFAULT_INITIAL_CAPACITY];
		init();
	}

	public WeakHashMatrix(int initialRows, int initialColumns) {
		this(initialRows, initialColumns, DEFAULT_LOAD_FACTOR);
	}

	@SuppressWarnings("unchecked")
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
		table = new WeakHashMatrix.Entry[rowCapacity][columnCapacity];
		// rowSizes = new int[rowCapacity];
		// columnSizes = new int[columnCapacity];
		this.loadFactor = loadFactor;
		rowThreshold = (int) (rowCapacity * loadFactor);
		columnThreshold = (int) (columnCapacity * loadFactor);
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
	
	/**
     * Initialization hook for subclasses. This method is called
     * in all constructors and pseudo-constructors (clone, readObject)
     * after HashMap has been initialized but before any entries have
     * been inserted.  (In the absence of this method, readObject would
     * require explicit knowledge of subclasses.)
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
	private void clean() {
		Entry keys;
		while ((keys = (Entry) queue.poll()) != null) {
			// remove entry;
			int row = indexFor(keys.rowHash, rowKeys.length);
			int column = indexFor(keys.columnHash, columnKeys.length);
			Entry entry = table[row][column], prev = null;
			while (entry != null && entry != keys) {
				prev = entry;
				entry = entry.next;
			}
			if (entry != null) {
				if (prev == null) {
					table[row][column] = keys.next;
				} else {
					prev.next = keys.next;
				}
			}
			keys.dispose();
		}
	}

	/**
	 * Applies a supplemental hash function to a given hashCode, which defends
	 * against poor quality hash functions. This is critical because HashMap
	 * uses power-of-two length hash tables, that otherwise encounter collisions
	 * for hashCodes that do not differ in lower bits. Note: Null keys always
	 * map to hash 0, thus index 0.
	 */
	static int hash(int h) {
		// This function ensures that hashCodes that differ only by
		// constant multiples at each bit position have a bounded
		// number of collisions (approximately 8 at default load factor).
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/**
	 * Returns index for hash code h.
	 */
	static int indexFor(int h, int length) {
		return h & (length - 1);
	}

	private Pair<RK, CK> keyPairAt(final int row, final int column) {
		return new AbstractImmutablePair<RK, CK>() {

			@Override
			public RK getFirst() {
				// TODO Auto-generated method stub
				if (row < 0 || row >= WeakHashMatrix.this.rowKeys.length)
					return null;
				return WeakHashMatrix.this.rowKeys[row].getKey();
			}

			@Override
			public CK getSecond() {
				// TODO Auto-generated method stub
				if (column < 0
						|| column >= WeakHashMatrix.this.columnKeys.length)
					return null;
				return WeakHashMatrix.this.columnKeys[column].getKey();
			}

		};
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public int rows() {
		// TODO Auto-generated method stub
		return rows;
	}

	@Override
	public int columns() {
		// TODO Auto-generated method stub
		return columns;
	}
	
	
	
	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		return super.containsRow(row);
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		return super.containsColumn(column);
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		return super.containsKey(row, column);
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		return super.get(row, column);
	}

	@Override
	public V put(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		return super.put(row, column, value);
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		return super.remove(row, column);
	}

	@Override
	public void removeRow(RK row) {
		// TODO Auto-generated method stub
		super.removeRow(row);
	}

	@Override
	public void removeColumn(CK column) {
		// TODO Auto-generated method stub
		super.removeColumn(column);
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
		return super.rowValueCount(row);
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		return super.columnValueCount(column);
	}

	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		return super.rowKeySet();
	}

	@Override
	public Set<CK> columnKeySet() {
		// TODO Auto-generated method stub
		return super.columnKeySet();
	}

	// View
	protected transient volatile Set<RK> rowKeySet = null;
	protected transient volatile Set<CK> columnKeySet = null;
	protected transient volatile Set<Matrix.Entry<RK, CK, V>> entrySet = null;

	@Override
	public Set<hust.idc.util.matrix.Matrix.Entry<RK, CK, V>> entrySet() {
		// TODO Auto-generated method stub
		if(entrySet == null){
			
		}
		return entrySet;
	}

	private class Entry extends WeakReference<Pair<RK, CK>> implements
			Matrix.Entry<RK, CK, V> {
		private final int rowHash, columnHash;
		private V value;
		private Entry next;

		public Entry(Pair<RK, CK> reference, int rowHash, int columnHash,
				V value) {
			super(reference, queue);
			// TODO Auto-generated constructor stub
			this.rowHash = rowHash;
			this.columnHash = columnHash;
			this.value = value;
			this.next = null;
		}

		@Override
		public RK getRowKey() {
			// TODO Auto-generated method stub
			Pair<RK, CK> pair = getKeyPair();
			return pair == null ? null : pair.getFirst();
		}

		@Override
		public CK getColumnKey() {
			// TODO Auto-generated method stub
			Pair<RK, CK> pair = getKeyPair();
			return pair == null ? null : pair.getSecond();
		}

		@Override
		public Pair<RK, CK> getKeyPair() {
			// TODO Auto-generated method stub
			return WeakHashMatrix.<Pair<RK, CK>> unmaskNull(get());
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

		@Override
		public boolean match(Object row, Object column) {
			// TODO Auto-generated method stub
			return eq(row, this.getRowKey()) && eq(column, this.getColumnKey());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = rowHash + columnHash;
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
				if (other.rowHash != this.rowHash
						|| other.columnHash != this.columnHash)
					return false;
				return other.match(getRowKey(), getColumnKey())
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

		protected void dispose() {
			this.clear();
			value = null;
			rowMapEntry = null;
			columnMapEntry = null;
			next = null;
		}

	}

	private static class Head<K> {
		private K key;
		private int size;
		private Head<K> next;

		private Head(K key) {
			this.key = key;
			this.size = 0;
		}

		private K getKey() {
			return key;
		}

		protected void dispose() {
			this.key = null;
			this.next = null;
			this.size = 0;
		}
	}
}
