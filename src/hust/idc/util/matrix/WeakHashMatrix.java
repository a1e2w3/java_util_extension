package hust.idc.util.matrix;

import hust.idc.util.AbstractMapEntry;
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
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load fast used when none specified in constructor.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private Entry table[][];
//	private int rowSizes[], columnSizes[];
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
        rowThreshold = columnThreshold = (int)(DEFAULT_INITIAL_CAPACITY);
        table = new WeakHashMatrix.Entry[DEFAULT_INITIAL_CAPACITY][DEFAULT_INITIAL_CAPACITY];
//        rowSizes = new int[DEFAULT_INITIAL_CAPACITY];
//        columnSizes = new int[DEFAULT_INITIAL_CAPACITY];
    }
	
	public WeakHashMatrix(int initialRows, int initialColumns){
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
		
		int rowCapacity = ensureCapacity(Math.min(MAXIMUM_CAPACITY, initialRows));
		int columnCapacity = ensureCapacity(Math.min(MAXIMUM_CAPACITY, initialColumns));
		table = new WeakHashMatrix.Entry[rowCapacity][columnCapacity];
//		rowSizes = new int[rowCapacity];
//        columnSizes = new int[columnCapacity];
        this.loadFactor = loadFactor;
        rowThreshold = (int)(rowCapacity * loadFactor);
        columnThreshold = (int)(columnCapacity * loadFactor);
	}
	
	public WeakHashMatrix(Matrix<? extends RK, ? extends CK, ? extends V> otherMatrix){
		this(Math.max((int) (otherMatrix.rows() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), 
				Math.max((int) (otherMatrix.columns() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), 
				DEFAULT_LOAD_FACTOR);
		this.putAll(otherMatrix);
	}
	
	public WeakHashMatrix(Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> otherMatrix){
		this();
		this.putAll(otherMatrix);
	}
	
	private static int ensureCapacity(int minCapacity){
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
	private static <K> K unmaskNull(Object key) {
		return (K) (key == NULL_KEY ? null : key);
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
	public Set<hust.idc.util.matrix.Matrix.Entry<RK, CK, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	private class Entry extends WeakReference<Pair<RK, CK>> implements
			Matrix.Entry<RK, CK, V> {
		private Entry next = null;
		private final int hash;
		private V value;

		public Entry(Pair<RK, CK> referent, int hash, V value) {
			super(referent, queue);
			// TODO Auto-generated constructor stub
			this.hash = hash;
			this.value = value;
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
			int result = hash;
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
			if(obj instanceof WeakHashMatrix.Entry){
				WeakHashMatrix<?, ?, ?>.Entry other = (WeakHashMatrix<?, ?, ?>.Entry) obj;
				if(other.hash != this.hash)
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
			next = null;
			value = null;
			rowMapEntry = null;
			columnMapEntry = null;
		}

	}

}
