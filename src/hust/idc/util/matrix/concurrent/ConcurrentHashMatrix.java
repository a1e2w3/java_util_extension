package hust.idc.util.matrix.concurrent;

import hust.idc.util.matrix.AbstractMatrix;
import hust.idc.util.matrix.Matrix;
import hust.idc.util.pair.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentHashMatrix<RK, CK, V> extends
		AbstractConcurrentMatrix<RK, CK, V> implements
		ConcurrentMatrix<RK, CK, V>, Serializable {

	/* ---------------- Constants -------------- */

	/**
	 * 
	 */
	private static final long serialVersionUID = -808427047803627130L;

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

	/* ---------------- Fields -------------- */

	/**
	 * Mask value for indexing into segments. The upper bits of a key's hash
	 * code are used to choose the segment.
	 */
	final int rowSegmentMask;
	final int columnSegmentMask;

	/**
	 * Shift value for indexing within segments.
	 */
	final int rowSegmentShift;
	final int columnSegmentShift;

	/**
	 * The segments, each of which is a specialized hash table.
	 */
	final Block<RK, CK, V>[][] blocks;

	final HeadSegment<RK>[] rowHeadSegments;
	final HeadSegment<CK>[] columnHeadSegments;

	/**
	 * Creates a new, empty matrix with the specified initial capacity, load factor
	 * and concurrency level.
	 * 
	 * @param initialCapacity
	 *            the initial capacity. The implementation performs internal
	 *            sizing to accommodate this many elements.
	 * @param loadFactor
	 *            the load factor threshold, used to control resizing. Resizing
	 *            may be performed when the average number of elements per bin
	 *            exceeds this threshold.
	 * @param concurrencyLevel
	 *            the estimated number of concurrently updating threads. The
	 *            implementation performs internal sizing to try to accommodate
	 *            this many threads.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative or the load factor or
	 *             concurrencyLevel are nonpositive.
	 */
	public ConcurrentHashMatrix(int initialRowCapacity,
			int initialColumnCapacity, float loadFactor,
			int rowConcurrencyLevel, int columnConcurrencyLevel) {
		if (!(loadFactor > 0) || initialRowCapacity < 0
				|| initialColumnCapacity < 0 || rowConcurrencyLevel <= 0
				|| columnConcurrencyLevel <= 0)
			throw new IllegalArgumentException();

		if (rowConcurrencyLevel > MAX_SEGMENTS)
			rowConcurrencyLevel = MAX_SEGMENTS;
		if (columnConcurrencyLevel > MAX_SEGMENTS)
			columnConcurrencyLevel = MAX_SEGMENTS;

		// Find power-of-two sizes best matching arguments
		int rsshift = 0;
		int rssize = 1;
		while (rssize < rowConcurrencyLevel) {
			++rsshift;
			rssize <<= 1;
		}
		rowSegmentShift = 32 - rsshift;
		rowSegmentMask = rssize - 1;
		int csshift = 0;
		int cssize = 1;
		while (cssize < columnConcurrencyLevel) {
			++csshift;
			cssize <<= 1;
		}
		columnSegmentShift = 32 - csshift;
		columnSegmentMask = cssize - 1;
		this.rowHeadSegments = HeadSegment.<RK> newArray(rssize);
		this.columnHeadSegments = HeadSegment.<CK> newArray(cssize);
		this.blocks = Block.<RK, CK, V> newArray(rssize, cssize);

		if (initialRowCapacity > MAXIMUM_CAPACITY)
			initialRowCapacity = MAXIMUM_CAPACITY;
		if (initialColumnCapacity > MAXIMUM_CAPACITY)
			initialColumnCapacity = MAXIMUM_CAPACITY;
		int r = initialRowCapacity / rssize;
		int c = initialColumnCapacity / cssize;
		if (r * rssize < initialRowCapacity)
			++r;
		if (c * cssize < initialColumnCapacity)
			++c;

		int rcap = 1;
		while (rcap < r)
			rcap <<= 1;

		int ccap = 1;
		while (ccap < c)
			ccap <<= 1;

		for (int i = 0; i < this.rowHeadSegments.length; ++i)
			this.rowHeadSegments[i] = new HeadSegment<RK>(rcap, loadFactor);
		for (int j = 0; j < this.columnHeadSegments.length; ++j)
			this.columnHeadSegments[j] = new HeadSegment<CK>(ccap, loadFactor);

		for (int i = 0; i < this.blocks.length; ++i) {
			for (int j = 0; j < this.blocks[0].length; ++j) {
				this.blocks[i][j] = new Block<RK, CK, V>(
						this.rowHeadSegments[i], this.columnHeadSegments[j],
						rcap, ccap, loadFactor);
			}
		}
	}

	/**
	 * Creates a new, empty matrix with the specified initial capacity and load
	 * factor and with the default concurrencyLevel (16).
	 * 
	 * @param initialCapacity
	 *            The implementation performs internal sizing to accommodate
	 *            this many elements.
	 * @param loadFactor
	 *            the load factor threshold, used to control resizing. Resizing
	 *            may be performed when the average number of elements per bin
	 *            exceeds this threshold.
	 * @throws IllegalArgumentException
	 *             if the initial capacity of elements is negative or the load
	 *             factor is nonpositive
	 * 
	 * @since 1.6
	 */
	public ConcurrentHashMatrix(int initialRowCapacity,
			int initialColumnCapacity, float loadFactor) {
		this(initialRowCapacity, initialColumnCapacity, loadFactor,
				DEFAULT_CONCURRENCY_LEVEL, DEFAULT_CONCURRENCY_LEVEL);
	}

	/**
	 * Creates a new, empty matrix with the specified initial capacity and load
	 * factor and with the default concurrencyLevel (16).
	 * 
	 * @param initialCapacity
	 *            The implementation performs internal sizing to accommodate
	 *            this many elements.
	 * @param loadFactor
	 *            the load factor threshold, used to control resizing. Resizing
	 *            may be performed when the average number of elements per bin
	 *            exceeds this threshold.
	 * @throws IllegalArgumentException
	 *             if the initial capacity of elements is negative or the load
	 *             factor is nonpositive
	 * 
	 * @since 1.6
	 */
	public ConcurrentHashMatrix(int initialRowCapacity,
			int initialColumnCapacity) {
		this(initialRowCapacity, initialColumnCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new, empty matrix with the specified initial capacity and load
	 * factor and with the default concurrencyLevel (16).
	 * 
	 * @param initialCapacity
	 *            The implementation performs internal sizing to accommodate
	 *            this many elements.
	 * @param loadFactor
	 *            the load factor threshold, used to control resizing. Resizing
	 *            may be performed when the average number of elements per bin
	 *            exceeds this threshold.
	 * @throws IllegalArgumentException
	 *             if the initial capacity of elements is negative or the load
	 *             factor is nonpositive
	 * 
	 * @since 1.6
	 */
	public ConcurrentHashMatrix(int initialCapacity, float loadFactor) {
		this(initialCapacity, initialCapacity, loadFactor);
	}

	/**
	 * Creates a new, empty matrix with the specified initial capacity, and with
	 * default load factor (0.75) and concurrencyLevel (16).
	 * 
	 * @param initialCapacity
	 *            the initial capacity. The implementation performs internal
	 *            sizing to accommodate this many elements.
	 * @throws IllegalArgumentException
	 *             if the initial capacity of elements is negative.
	 */
	public ConcurrentHashMatrix(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new, empty matrix with a default initial capacity (16), load
	 * factor (0.75) and concurrencyLevel (16).
	 */
	public ConcurrentHashMatrix() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new matrix with the same mappings as the given matrix. The matrix is
	 * created with a capacity of 1.5 times the number of mappings in the given
	 * map or 16 (whichever is greater), and a default load factor (0.75) and
	 * concurrencyLevel (16).
	 * 
	 * @param m
	 *            the map
	 */
	public ConcurrentHashMatrix(
			Matrix<? extends RK, ? extends CK, ? extends V> m) {
		this(Math.max((int) (m.rows() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), Math.max(
				(int) (m.columns() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
		putAll(m);
	}

	/**
	 * Creates a new matrix with the same mappings as the given map. The matrix is
	 * created with a capacity of 1.5 times the number of mappings in the given
	 * map or 16 (whichever is greater), and a default load factor (0.75) and
	 * concurrencyLevel (16).
	 * 
	 * @param m
	 *            the map
	 */
	public ConcurrentHashMatrix(
			Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> m) {
		this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
		putAll(m);
	}

	/* ---------------- Small Utilities -------------- */

	/**
	 * Applies a supplemental hash function to a given hashCode, which defends
	 * against poor quality hash functions. This is critical because
	 * ConcurrentHashMap uses power-of-two length hash tables, that otherwise
	 * encounter collisions for hashCodes that do not differ in lower or upper
	 * bits.
	 */
	private static int hash(int h) {
		// Spread bits to regularize both segment and index locations,
		// using variant of single-word Wang/Jenkins hash.
		h += (h << 15) ^ 0xffffcd7d;
		h ^= (h >>> 10);
		h += (h << 3);
		h ^= (h >>> 6);
		h += (h << 2) + (h << 14);
		return h ^ (h >>> 16);
	}

	/**
	 * Returns the segment that should be used for key with given hash
	 * 
	 * @param hash
	 *            the hash code for the key
	 * @return the segment
	 */
	final Block<RK, CK, V> blockFor(int rowHash, int columnHash) {
		return blocks[rowBlockIndex(rowHash)][columnBlockIndex(columnHash)];
	}

	final int rowBlockIndex(int hash) {
		return (hash >>> rowSegmentShift) & rowSegmentMask;
	}

	final int columnBlockIndex(int hash) {
		return (hash >>> columnSegmentShift) & columnSegmentMask;
	}

	final HeadSegment<RK> rowHeadSegmentFor(int hash) {
		return rowHeadSegments[rowBlockIndex(hash)];
	}

	final HeadSegment<CK> columnHeadSegmentFor(int hash) {
		return columnHeadSegments[columnBlockIndex(hash)];
	}

	final void lockAllBlocks() {
		final Block<RK, CK, V>[][] blks = this.blocks;
		for (int i = 0; i < blks.length; ++i) {
			for (int j = 0; j < blks[0].length; ++j) {
				blks[i][j].lock();
			}
		}
	}

	final void unlockAllBlocks() {
		final Block<RK, CK, V>[][] blks = this.blocks;
		for (int i = 0; i < blks.length; ++i) {
			for (int j = 0; j < blks[0].length; ++j) {
				blks[i][j].unlock();
			}
		}
	}

	@Override
	public int rows() {
		// TODO Auto-generated method stub
		final HeadSegment<RK>[] rowHeadSegments = this.rowHeadSegments;
		long sum = 0;
		long check = 0;
		int[] mc = new int[rowHeadSegments.length];
		for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
			check = 0;
			sum = 0;
			int mcsum = 0;
			for (int i = 0; i < rowHeadSegments.length; ++i) {
				sum += rowHeadSegments[i].count;
				mcsum += mc[i] = rowHeadSegments[i].modCount;
			}
			if (mcsum != 0) {
				for (int i = 0; i < rowHeadSegments.length; ++i) {
					check += rowHeadSegments[i].count;
					if (mc[i] != rowHeadSegments[i].modCount) {
						check = -1; // force retry
						break;
					}
				}
			}
			if (check == sum)
				break;
		}
		if (check != sum) { // Resort to locking all segments
			sum = 0;
			for (int i = 0; i < rowHeadSegments.length; ++i) {
				rowHeadSegments[i].lock();
			}
			try {
				for (int i = 0; i < rowHeadSegments.length; ++i) {
					sum += rowHeadSegments[i].count;
				}
			} finally {
				for (int i = 0; i < rowHeadSegments.length; ++i) {
					rowHeadSegments[i].unlock();
				}
			}
		}
		if (sum > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return (int) sum;
	}

	@Override
	public int columns() {
		// TODO Auto-generated method stub
		final HeadSegment<CK>[] columnHeadSegments = this.columnHeadSegments;
		long sum = 0;
		long check = 0;
		int[] mc = new int[columnHeadSegments.length];
		for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
			check = 0;
			sum = 0;
			int mcsum = 0;
			for (int i = 0; i < columnHeadSegments.length; ++i) {
				sum += columnHeadSegments[i].count;
				mcsum += mc[i] = columnHeadSegments[i].modCount;
			}
			if (mcsum != 0) {
				for (int i = 0; i < columnHeadSegments.length; ++i) {
					check += columnHeadSegments[i].count;
					if (mc[i] != columnHeadSegments[i].modCount) {
						check = -1; // force retry
						break;
					}
				}
			}
			if (check == sum)
				break;
		}
		if (check != sum) { // Resort to locking all segments
			sum = 0;
			for (int i = 0; i < columnHeadSegments.length; ++i) {
				columnHeadSegments[i].lock();
			}
			try {
				for (int i = 0; i < columnHeadSegments.length; ++i) {
					sum += columnHeadSegments[i].count;
				}
			} finally {
				for (int i = 0; i < columnHeadSegments.length; ++i) {
					columnHeadSegments[i].unlock();
				}
			}
		}
		if (sum > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return (int) sum;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		final Block<RK, CK, V>[][] blks = this.blocks;
		long sum = 0;
		long check = 0;
		int[][] mc = new int[blks.length][blks[0].length];
		// Try a few times to get accurate count. On failure due to
		// continuous async changes in table, resort to locking.
		for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
			check = 0;
			sum = 0;
			int mcsum = 0;
			for (int i = 0; i < blks.length; ++i) {
				for (int j = 0; j < blks[0].length; ++j) {
					sum += blks[i][j].count;
					mcsum += mc[i][j] = blks[i][j].modCount;
				}
			}
			if (mcsum != 0) {
				for (int i = 0; i < blks.length; ++i) {
					for (int j = 0; j < blks[0].length; ++j) {
						check += blks[i][j].count;
						if (mc[i][j] != blks[i][j].modCount) {
							check = -1; // force retry
							break;
						}
					}
				}
			}
			if (check == sum)
				break;
		}
		if (check != sum) { // Resort to locking all segments
			sum = 0;
			this.lockAllBlocks();
			try {
				for (int i = 0; i < blks.length; ++i) {
					for (int j = 0; j < blks[0].length; ++j) {
						sum += blks[i][j].count;
					}
				}
			} finally {
				this.unlockAllBlocks();
			}
		}
		if (sum > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return (int) sum;
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		int rowHash = hash(row.hashCode());
		return rowHeadSegmentFor(rowHash).containsKey(row, rowHash);
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		int columnHash = hash(column.hashCode());
		return columnHeadSegmentFor(columnHash).containsKey(column, columnHash);
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		int rowHash = hash(row.hashCode());
		int columnHash = hash(column.hashCode());
		return blockFor(rowHash, columnHash).containsKey(row, rowHash, column,
				columnHash);
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		if (value == null)
			throw new NullPointerException();

		final Block<RK, CK, V>[][] blks = this.blocks;
		// See explanation of modCount use above
		int[][] mc = new int[blks.length][blks[0].length];

		// Try a few times without locking
		for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
			int mcsum = 0;
			for (int i = 0; i < blks.length; ++i) {
				for (int j = 0; j < blks[0].length; ++j) {
					mcsum += mc[i][j] = blks[i][j].modCount;
					if (blks[i][j].containsValue(value))
						return true;
				}
			}
			boolean cleanSweep = true;
			if (mcsum != 0) {
				for (int i = 0; i < blks.length; ++i) {
					for (int j = 0; j < blks[0].length; ++j) {
						if (mc[i][j] != blks[i][j].modCount) {
							cleanSweep = false;
							break;
						}
					}
				}
			}
			if (cleanSweep)
				return false;
		}
		// Resort to locking all segments
		this.lockAllBlocks();
		boolean found = false;
		try {
			for (int i = 0; i < blks.length; ++i) {
				for (int j = 0; j < blks[0].length; ++j) {
					if (blks[i][j].containsValue(value)) {
						found = true;
						break;
					}
				}
			}
		} finally {
			this.unlockAllBlocks();
		}
		return found;
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		int rowHash = hash(row.hashCode());
		int columnHash = hash(column.hashCode());
		return blockFor(rowHash, columnHash).get(row, rowHash, column,
				columnHash);
	}

	@Override
	public V put(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		if (value == null)
			throw new NullPointerException();
		int rowHash = hash(row.hashCode());
		int columnHash = hash(column.hashCode());
		return blockFor(rowHash, columnHash).put(row, rowHash, column,
				columnHash, value, false);
	}

	@Override
	public V putIfAbsent(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		if (value == null)
			throw new NullPointerException();
		int rowHash = hash(row.hashCode());
		int columnHash = hash(column.hashCode());
		return blockFor(rowHash, columnHash).put(row, rowHash, column,
				columnHash, value, true);
	}

	@Override
	public boolean replace(RK row, CK column, V oldValue, V newValue) {
		// TODO Auto-generated method stub
		if (oldValue == null || newValue == null)
			throw new NullPointerException();
		int rowHash = hash(row.hashCode());
		int columnHash = hash(column.hashCode());
		return blockFor(rowHash, columnHash).replace(row, rowHash, column,
				columnHash, oldValue, newValue);
	}

	@Override
	public V replace(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		if (value == null)
			throw new NullPointerException();
		int rowHash = hash(row.hashCode());
		int columnHash = hash(column.hashCode());
		return blockFor(rowHash, columnHash).replace(row, rowHash, column,
				columnHash, value);
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		int rowHash = hash(row.hashCode());
		int columnHash = hash(column.hashCode());
		return blockFor(rowHash, columnHash).remove(row, rowHash, column,
				columnHash, null);
	}

	@Override
	public boolean remove(Object row, Object column, Object value) {
		// TODO Auto-generated method stub
		int rowHash = hash(row.hashCode());
		int columnHash = hash(column.hashCode());
		return blockFor(rowHash, columnHash).remove(row, rowHash, column,
				columnHash, value) != null;
	}

	@Override
	public void removeRow(RK row) {
		// TODO Auto-generated method stub
		int rowHash = hash(row.hashCode());
		int rowBlkIndex = rowBlockIndex(rowHash);
		for (int j = 0; j < blocks[rowBlkIndex].length; ++j) {
			if (blocks[rowBlkIndex][j].removeRow(row, rowHash))
				break;
		}
	}

	@Override
	public void removeColumn(CK column) {
		// TODO Auto-generated method stub
		int columnHash = hash(column.hashCode());
		int columnBlkIndex = columnBlockIndex(columnHash);
		for (int i = 0; i < blocks.length; ++i) {
			if (blocks[i][columnBlkIndex].removeRow(column, columnHash))
				break;
		}
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		for (int i = 0; i < rowHeadSegments.length; ++i)
			rowHeadSegments[i].clear();
		for (int j = 0; j < columnHeadSegments.length; ++j)
			columnHeadSegments[j].clear();
		for (int i = 0; i < blocks.length; ++i)
			for (int j = 0; j < blocks[0].length; ++j)
				blocks[i][j].clear();
	}

	@Override
	protected int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		int rowHash = hash(row.hashCode());
		Head<RK> head = rowHeadSegmentFor(rowHash).getHead(row, rowHash);
		return head == null ? 0 : head.size;
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		int columnHash = hash(column.hashCode());
		Head<CK> head = columnHeadSegmentFor(columnHash).getHead(column,
				columnHash);
		return head == null ? 0 : head.size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<CK, V> rowMap(RK row) {
		// TODO Auto-generated method stub
		int rowHash = hash(row.hashCode());
		HeadSegment<RK> segment = rowHeadSegmentFor(rowHash);
		segment.lock();
		try {
			Head<RK> head = segment.getHead(row, rowHash);
			if (head == null) {
				return new RowMapView(row, rowHash, null);
			} else {
				if (head.viewMap == null) {
					head.viewMap = new RowMapView(row, rowHash, head);
				}
				return (Map<CK, V>) head.viewMap;
			}
		} finally {
			segment.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<RK, V> columnMap(CK column) {
		// TODO Auto-generated method stub
		int columnHash = hash(column.hashCode());
		HeadSegment<CK> segment = columnHeadSegmentFor(columnHash);
		segment.lock();
		try {
			Head<CK> head = segment.getHead(column, columnHash);
			if (head == null) {
				return new ColumnMapView(column, columnHash, null);
			} else {
				if (head.viewMap == null) {
					head.viewMap = new ColumnMapView(column, columnHash, head);
				}
				return (Map<RK, V>) head.viewMap;
			}
		} finally {
			segment.unlock();
		}
	}

	private abstract static class MapView<K1, K2, V> extends AbstractMap<K2, V> {
		transient volatile Head<K1> head;
		transient final K1 viewKey;
		transient final int keyHash;

		transient volatile Set<java.util.Map.Entry<K2, V>> entrySet = null;

		MapView(K1 viewKey, int keyHash, Head<K1> head) {
			this.viewKey = viewKey;
			this.head = head;
			this.keyHash = keyHash;
		}

		final boolean headNotExists() {
			if (head != null && head.disposed())
				head = null;
			return head == null;
		}

		@Override
		public final int size() {
			// TODO Auto-generated method stub
			return validateHead() == null ? 0 : head.size;
		}

		@Override
		public Set<Map.Entry<K2, V>> entrySet() {
			validateHead();
			if (entrySet == null) {
				entrySet = new AbstractSet<Map.Entry<K2, V>>() {

					@Override
					public final Iterator<Map.Entry<K2, V>> iterator() {
						// TODO Auto-generated method stub
						validateHead();
						return entryIterator();
					}

					@Override
					public final int size() {
						// TODO Auto-generated method stub
						return MapView.this.size();
					}

				};
			}
			return entrySet;
		}

		Head<K1> validateHead() {
			if (headNotExists()) {
				resetHead();
				if (head != null && head.viewMap == null)
					head.viewMap = this;
			}
			return head;
		}

		abstract Iterator<Map.Entry<K2, V>> entryIterator();

		abstract void resetHead();
	}

	private final class RowMapView extends MapView<RK, CK, V> {
		private RowMapView(RK row, int rowHash, Head<RK> head) {
			super(row, rowHash, head);
		}

		@Override
		public V put(CK key, V value) {
			// TODO Auto-generated method stub
			if (validateHead() == null) {
				head = ConcurrentHashMatrix.this.rowHeadSegmentFor(keyHash)
						.addIfAdsent(viewKey, keyHash);
				head.viewMap = this;
			}
			int columnHash = hash(key.hashCode());
			return ConcurrentHashMatrix.this.blockFor(keyHash, columnHash).put(
					viewKey, keyHash, key, columnHash, value, false);
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return false;
			int columnHash = hash(key.hashCode());
			return ConcurrentHashMatrix.this.blockFor(keyHash, columnHash)
					.containsKey(viewKey, keyHash, key, columnHash);
		}

		@Override
		public V get(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return null;
			int columnHash = hash(key.hashCode());
			return ConcurrentHashMatrix.this.blockFor(keyHash, columnHash).get(
					viewKey, keyHash, key, columnHash);
		}

		@Override
		public V remove(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return null;
			int columnHash = hash(key.hashCode());
			V oldValue = ConcurrentHashMatrix.this
					.blockFor(keyHash, columnHash).remove(viewKey, keyHash,
							key, columnHash, null);
			if (head.disposed())
				head = null;
			return oldValue;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			if (validateHead() != null) {
				ConcurrentHashMatrix.this.removeRow(viewKey);
				head = null;
			}
		}

		@Override
		final Iterator<Map.Entry<CK, V>> entryIterator() {
			// TODO Auto-generated method stub
			return new RowMapEntryIterator();
		}

		@Override
		final void resetHead() {
			// TODO Auto-generated method stub
			head = ConcurrentHashMatrix.this.rowHeadSegmentFor(keyHash)
					.getHead(viewKey, keyHash);
		}

		final class RowMapEntryIterator extends HashIterator implements
				Iterator<Map.Entry<CK, V>> {

			RowMapEntryIterator() {
				super();
			}

			@Override
			public final Map.Entry<CK, V> next() {
				return new WriteThroughEntry(super.nextEntry()).rowMapEntry();
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				super.remove();
				if (head.disposed()) {
					head = null;
					nextEntry = null;
				}
			}

			@Override
			final void init() {
				// TODO Auto-generated method stub
				super.init();
				nextBlkRowIndex = rowBlockIndex(keyHash);
			}

			@Override
			final boolean validateEntry(HashEntry<RK, CK, V> entry) {
				// TODO Auto-generated method stub
				return super.validateEntry(entry) && entry.rowHead == head;
			}

			@Override
			Block<RK, CK, V> blockAdvance() {
				// TODO Auto-generated method stub
				while (nextBlkColumnIndex >= 0) {
					Block<RK, CK, V> blk = blocks[nextBlkRowIndex][nextBlkColumnIndex--];
					if (validateBlock(blk)) {
						currentTable = blk.table;
						nextTableRowIndex = Block.index(keyHash, currentTable);
						nextTableColumnIndex = currentTable[nextTableRowIndex].length - 1;
						return blk;
					}
				}
				return null;
			}

			@Override
			HashEntry<RK, CK, V> tableAdvance() {
				// TODO Auto-generated method stub
				HashEntry<RK, CK, V> next = null;
				while (nextTableColumnIndex >= 0) {
					if (validateEntry(next = currentTable[nextTableRowIndex][nextTableColumnIndex--]))
						return next;
				}
				return null;
			}
		}
	}

	private final class ColumnMapView extends MapView<CK, RK, V> {

		private ColumnMapView(CK column, int columnHash, Head<CK> head) {
			super(column, columnHash, head);
		}

		@Override
		public V put(RK key, V value) {
			// TODO Auto-generated method stub
			if (validateHead() == null) {
				head = ConcurrentHashMatrix.this.columnHeadSegmentFor(keyHash)
						.addIfAdsent(viewKey, keyHash);
				head.viewMap = this;
			}
			int rowHash = hash(key.hashCode());
			return ConcurrentHashMatrix.this.blockFor(rowHash, keyHash).put(
					key, rowHash, viewKey, keyHash, value, false);
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return false;
			int rowHash = hash(key.hashCode());
			return ConcurrentHashMatrix.this.blockFor(rowHash, keyHash)
					.containsKey(key, rowHash, viewKey, keyHash);
		}

		@Override
		public V get(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return null;
			int rowHash = hash(key.hashCode());
			return ConcurrentHashMatrix.this.blockFor(rowHash, keyHash).get(
					key, rowHash, viewKey, keyHash);
		}

		@Override
		public V remove(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return null;
			int rowHash = hash(key.hashCode());
			V oldValue = ConcurrentHashMatrix.this.blockFor(rowHash, keyHash)
					.remove(key, rowHash, viewKey, keyHash, null);
			if (head.disposed())
				head = null;
			return oldValue;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			if (validateHead() != null) {
				ConcurrentHashMatrix.this.removeColumn(viewKey);
				head = null;
			}
		}

		@Override
		final Iterator<Map.Entry<RK, V>> entryIterator() {
			// TODO Auto-generated method stub
			return new ColumnMapEntryIterator();
		}

		@Override
		final void resetHead() {
			// TODO Auto-generated method stub
			head = ConcurrentHashMatrix.this.columnHeadSegmentFor(keyHash)
					.getHead(viewKey, keyHash);
		}

		final class ColumnMapEntryIterator extends HashIterator implements
				Iterator<Map.Entry<RK, V>> {
			ColumnMapEntryIterator() {
				super();
			}

			@Override
			public final Map.Entry<RK, V> next() {
				return new WriteThroughEntry(super.nextEntry())
						.columnMapEntry();
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				super.remove();
				if (head.disposed()) {
					head = null;
					nextEntry = null;
				}
			}

			@Override
			final void init() {
				// TODO Auto-generated method stub
				super.init();
				nextBlkColumnIndex = columnBlockIndex(keyHash);
			}

			@Override
			final boolean validateEntry(HashEntry<RK, CK, V> entry) {
				// TODO Auto-generated method stub
				return super.validateEntry(entry) && entry.columnHead == head;
			}

			@Override
			Block<RK, CK, V> blockAdvance() {
				// TODO Auto-generated method stub
				while (nextBlkRowIndex >= 0) {
					Block<RK, CK, V> blk = blocks[nextBlkRowIndex--][nextBlkColumnIndex];
					if (validateBlock(blk)) {
						currentTable = blk.table;
						nextTableRowIndex = currentTable.length - 1;
						nextTableColumnIndex = Block.index(keyHash, currentTable[0]);
						return blk;
					}
				}
				return null;
			}

			@Override
			HashEntry<RK, CK, V> tableAdvance() {
				// TODO Auto-generated method stub
				HashEntry<RK, CK, V> next = null;
				while (nextTableRowIndex >= 0) {
					if (validateEntry(next = currentTable[nextTableRowIndex--][nextTableColumnIndex]))
						return next;
				}
				return null;
			}
		}
	}

	// View
	protected transient volatile Set<RK> rowKeySet = null;
	protected transient volatile Set<CK> columnKeySet = null;
	protected transient volatile Set<Matrix.Entry<RK, CK, V>> entrySet = null;

	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		Set<RK> ks = rowKeySet;
		return (ks != null) ? ks : (rowKeySet = new RowKeySet());
	}

	@Override
	public Set<CK> columnKeySet() {
		// TODO Auto-generated method stub
		Set<CK> ks = columnKeySet;
		return (ks != null) ? ks : (columnKeySet = new ColumnKeySet());
	}

	@Override
	public Set<Pair<RK, CK>> keyPairSet() {
		Set<Pair<RK, CK>> ks = keyPairSet;
		return (ks != null) ? ks : (keyPairSet = new KeyPairSet());
	}

	@Override
	public Collection<V> values() {
		Collection<V> vs = values;
		return (vs != null) ? vs : (values = new Values());
	}

	@Override
	public Set<Matrix.Entry<RK, CK, V>> entrySet() {
		// TODO Auto-generated method stub
		Set<Matrix.Entry<RK, CK, V>> es = entrySet;
		return (es != null) ? es : (entrySet = new EntrySet());
	}

	// unsafe
	static abstract class KeyIterator<K> implements Iterator<K>, Enumeration<K> {
		int nextSegmentIndex;
		int nextTableIndex;
		Head<K>[] currentTable;
		Head<K> lastReturned;
		Head<K> nextHead;

		KeyIterator() {
			nextTableIndex = -1;
			initNextSegmentIndex();
			advance();
		}

		abstract void initNextSegmentIndex();

		abstract HeadSegment<K> segmentAt(int index);

		abstract void removeHead(Head<K> head);

		boolean validateHead(Head<K> head) {
			return head != null && head.size > 0;
		}

		boolean validateSegment(HeadSegment<K> segment) {
			return segment.count > 0;
		}

		HeadSegment<K> segmentAdvance() {
			// TODO Auto-generated method stub
			while (nextSegmentIndex >= 0) {
				HeadSegment<K> segment = segmentAt(nextSegmentIndex--);
				if (validateSegment(segment)) {
					currentTable = segment.headsTable;
					nextTableIndex = currentTable.length - 1;
					return segment;
				}
			}
			return null;
		}

		Head<K> tableAdvance() {
			Head<K> next = null;
			while (nextTableIndex >= 0) {
				if (validateHead(next = currentTable[nextTableIndex--]))
					break;
			}
			return next;
		}

		final void advance() {
			while (nextHead != null) {
				if (validateHead(nextHead = nextHead.next))
					return;
			}

			if (validateHead(nextHead = tableAdvance()))
				return;

			HeadSegment<K> segment = segmentAdvance();
			while (segment != null) {
				if (validateHead(nextHead = tableAdvance()))
					return;
				segment = segmentAdvance();
			}
		}

		Head<K> nextHead() {
			if (nextHead == null)
				throw new NoSuchElementException();
			lastReturned = nextHead;
			advance();
			return lastReturned;
		}

		@Override
		public boolean hasMoreElements() {
			// TODO Auto-generated method stub
			return nextHead != null;
		}

		@Override
		public K nextElement() {
			// TODO Auto-generated method stub
			return nextHead().getKey();
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return nextHead != null;
		}

		@Override
		public K next() {
			// TODO Auto-generated method stub
			return nextHead().getKey();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			if (lastReturned == null)
				throw new IllegalStateException();
			removeHead(lastReturned);
			lastReturned = null;
		}
	}

	final class RowKeyIterator extends KeyIterator<RK> {

		@Override
		final void initNextSegmentIndex() {
			// TODO Auto-generated method stub
			nextSegmentIndex = rowHeadSegments.length - 1;
		}

		@Override
		final HeadSegment<RK> segmentAt(int index) {
			// TODO Auto-generated method stub
			return rowHeadSegments[index];
		}

		@Override
		final void removeHead(Head<RK> head) {
			// TODO Auto-generated method stub
			ConcurrentHashMatrix.this.removeRow(head.getKey());
		}

	}

	final class ColumnKeyIterator extends KeyIterator<CK> {

		@Override
		final void initNextSegmentIndex() {
			// TODO Auto-generated method stub
			nextSegmentIndex = columnHeadSegments.length - 1;
		}

		@Override
		final HeadSegment<CK> segmentAt(int index) {
			// TODO Auto-generated method stub
			return columnHeadSegments[index];
		}

		@Override
		final void removeHead(Head<CK> head) {
			// TODO Auto-generated method stub
			ConcurrentHashMatrix.this.removeColumn(head.getKey());
		}

	}

	final class RowKeySet extends AbstractSet<RK> {

		@Override
		public final Iterator<RK> iterator() {
			// TODO Auto-generated method stub
			return new RowKeyIterator();
		}

		@Override
		public final int size() {
			// TODO Auto-generated method stub
			return ConcurrentHashMatrix.this.rows();
		}

		@Override
		public final boolean contains(Object o) {
			// TODO Auto-generated method stub
			return ConcurrentHashMatrix.this.containsRow(o);
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			try {
				@SuppressWarnings("unchecked")
				RK row = (RK) o;
				int rowHash = hash(row.hashCode());
				int rowBlkIndex = rowBlockIndex(rowHash);
				for (int j = 0; j < blocks[rowBlkIndex].length; ++j) {
					if (blocks[rowBlkIndex][j].removeRow(row, rowHash))
						return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			return false;
		}

		@Override
		public final void clear() {
			// TODO Auto-generated method stub
			ConcurrentHashMatrix.this.clear();
		}

	}

	final class ColumnKeySet extends AbstractSet<CK> {

		@Override
		public final Iterator<CK> iterator() {
			// TODO Auto-generated method stub
			return new ColumnKeyIterator();
		}

		@Override
		public final int size() {
			// TODO Auto-generated method stub
			return ConcurrentHashMatrix.this.columns();
		}

		@Override
		public final boolean contains(Object o) {
			// TODO Auto-generated method stub
			return ConcurrentHashMatrix.this.containsColumn(o);
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			try {
				@SuppressWarnings("unchecked")
				CK column = (CK) o;
				int columnHash = hash(column.hashCode());
				int columnBlkIndex = columnBlockIndex(columnHash);
				for (int j = 0; j < blocks.length; ++j) {
					if (blocks[j][columnBlkIndex].removeColumn(column,
							columnHash))
						return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			return false;
		}

		@Override
		public final void clear() {
			// TODO Auto-generated method stub
			ConcurrentHashMatrix.this.clear();
		}

	}

	final class EntrySet extends AbstractSet<Matrix.Entry<RK, CK, V>> {
		public final Iterator<Matrix.Entry<RK, CK, V>> iterator() {
			return new EntryIterator();
		}

		public boolean contains(Object o) {
			if (!(o instanceof Matrix.Entry))
				return false;
			Matrix.Entry<?, ?, ?> e = (Matrix.Entry<?, ?, ?>) o;
			V v = ConcurrentHashMatrix.this
					.get(e.getRowKey(), e.getColumnKey());
			return v != null && v.equals(e.getValue());
		}

		public boolean remove(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Matrix.Entry<?, ?, ?> e = (Matrix.Entry<?, ?, ?>) o;
			return ConcurrentHashMatrix.this.remove(e.getRowKey(),
					e.getColumnKey(), e.getValue());
		}

		public final int size() {
			return ConcurrentHashMatrix.this.size();
		}

		public final void clear() {
			ConcurrentHashMatrix.this.clear();
		}
	}

	final class KeyPairSet extends AbstractSet<Pair<RK, CK>> {
		public final Iterator<Pair<RK, CK>> iterator() {
			return new KeyPairIterator();
		}

		public final int size() {
			return ConcurrentHashMatrix.this.size();
		}

		public final boolean contains(Object o) {
			if (!(o instanceof Pair))
				return false;
			return ConcurrentHashMatrix.this.containsKey((Pair<?, ?>) o);
		}

		public final boolean remove(Object o) {
			if (!(o instanceof Pair))
				return false;
			return ConcurrentHashMatrix.this.remove((Pair<?, ?>) o) != null;
		}

		public final void clear() {
			ConcurrentHashMatrix.this.clear();
		}
	}

	final class Values extends AbstractCollection<V> {
		public final Iterator<V> iterator() {
			return new ValueIterator();
		}

		public final int size() {
			return ConcurrentHashMatrix.this.size();
		}

		public final boolean contains(Object o) {
			return ConcurrentHashMatrix.this.containsValue(o);
		}

		public final void clear() {
			ConcurrentHashMatrix.this.clear();
		}
	}

	/* ---------------- Iterator Support -------------- */

	abstract class HashIterator {
		int nextBlkRowIndex, nextBlkColumnIndex;
		int nextTableRowIndex, nextTableColumnIndex;
		HashEntry<RK, CK, V>[][] currentTable;
		HashEntry<RK, CK, V> nextEntry;
		HashEntry<RK, CK, V> lastReturned;

		HashIterator() {
			init();
			advance();
		}

		void init() {
			nextBlkRowIndex = blocks.length - 1;
			nextBlkColumnIndex = blocks[0].length - 1;
			nextTableRowIndex = nextTableColumnIndex = -1;
		}

		public boolean hasMoreElements() {
			return hasNext();
		}

		boolean validateEntry(HashEntry<RK, CK, V> entry) {
			return entry != null;
		}

		boolean validateBlock(Block<RK, CK, V> block) {
			return block.count > 0;
		}

		Block<RK, CK, V> blockAdvance() {
			while (nextBlkRowIndex >= 0) {
				while (nextBlkColumnIndex >= 0) {
					Block<RK, CK, V> blk = blocks[nextBlkRowIndex][nextBlkColumnIndex--];
					if (validateBlock(blk)) {
						currentTable = blk.table;
						nextTableRowIndex = currentTable.length - 1;
						nextTableColumnIndex = currentTable[nextTableRowIndex].length - 1;
						return blk;
					}
				}
				--nextBlkRowIndex;
				nextBlkColumnIndex = blocks[0].length - 1;
			}
			return null;
		}

		HashEntry<RK, CK, V> tableAdvance() {
			HashEntry<RK, CK, V> e = null;
			while (nextTableRowIndex >= 0) {
				while (nextTableColumnIndex >= 0) {
					if (validateEntry(e = currentTable[nextTableRowIndex][nextTableColumnIndex--])){
						return e;
					}
				}
				--nextTableRowIndex;
				nextTableColumnIndex = currentTable[0].length - 1;
			}
			return null;
		}

		final void advance() {
			while (nextEntry != null) {
				if (validateEntry(nextEntry = nextEntry.next)) {
					return;
				}
			}

			HashEntry<RK, CK, V> next = null;
			if (validateEntry(next = tableAdvance())) {
				nextEntry = next;
				return;
			}

			Block<RK, CK, V> blk = blockAdvance();
			while (blk != null) {
				if (validateEntry(next = tableAdvance())) {
					nextEntry = next;
					return;
				}
				blk = blockAdvance();
			}
		}

		public boolean hasNext() {
			return nextEntry != null;
		}

		HashEntry<RK, CK, V> nextEntry() {
			if (nextEntry == null)
				throw new NoSuchElementException();
			lastReturned = nextEntry;
			advance();
			return lastReturned;
		}

		public void remove() {
			if (lastReturned == null)
				throw new IllegalStateException();
			ConcurrentHashMatrix.this.remove(lastReturned.getRowKey(),
					lastReturned.getColumnKey());
			lastReturned = null;
		}
	}

	final class EntryIterator extends HashIterator implements
			Iterator<Matrix.Entry<RK, CK, V>> {
		public final Matrix.Entry<RK, CK, V> next() {
			HashEntry<RK, CK, V> e = super.nextEntry();
			return new WriteThroughEntry(e);
		}
	}

	final class KeyPairIterator extends HashIterator implements
			Iterator<Pair<RK, CK>>, Enumeration<Pair<RK, CK>> {
		public final Pair<RK, CK> next() {
			return super.nextEntry().getKeyPair();
		}

		public final Pair<RK, CK> nextElement() {
			return super.nextEntry().getKeyPair();
		}
	}

	final class ValueIterator extends HashIterator implements Iterator<V>,
			Enumeration<V> {
		public final V next() {
			return super.nextEntry().getValue();
		}

		public final V nextElement() {
			return super.nextEntry().getValue();
		}
	}

	/**
	 * Custom Entry class used by EntryIterator.next(), RowEntryIterator.next()
	 * and ColumnEntryIterator.next(), that relays setValue changes to the
	 * underlying matrix.
	 */
	final class WriteThroughEntry extends AbstractMatrix.SimpleEntry<RK, CK, V> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1717953847468972140L;

		WriteThroughEntry(RK rk, CK ck, V v) {
			super(rk, ck, v);
		}

		WriteThroughEntry(HashEntry<RK, CK, V> hashEntry) {
			super(hashEntry.getRowKey(), hashEntry.getColumnKey(), hashEntry
					.getValue());
		}

		/**
		 * Set our entry's value and write through to the matrix. The value to
		 * return is somewhat arbitrary here. Since a WriteThroughEntry does not
		 * necessarily track asynchronous changes, the most recent "previous"
		 * value could be different from what we return (or could even have been
		 * removed in which case the put will re-establish). We do not and
		 * cannot guarantee more.
		 */
		public V setValue(V value) {
			if (value == null)
				throw new NullPointerException();
			V v = super.setValue(value);
			ConcurrentHashMatrix.this.put(getRowKey(), getColumnKey(), value);
			return v;
		}
	}

	/**
	 * ConcurrentHashMatrix list entry. Note that this is never exported out as
	 * a user-visible Matrix.Entry. setValue(V) method was unsupported in
	 * HashEntry
	 */
	static final class HashEntry<RK, CK, V> extends
			AbstractImmutableEntry<RK, CK, V> {
		transient final Head<RK> rowHead;
		transient final Head<CK> columnHead;
		transient volatile V value;
		transient final HashEntry<RK, CK, V> next;

		HashEntry(Head<RK> rowHead, Head<CK> columnHead, V value,
				HashEntry<RK, CK, V> next) {
			this.rowHead = rowHead;
			this.columnHead = columnHead;
			this.value = value;
			this.next = next;
		}

		@Override
		public final RK getRowKey() {
			// TODO Auto-generated method stub
			return rowHead.getKey();
		}

		@Override
		public final CK getColumnKey() {
			// TODO Auto-generated method stub
			return columnHead.getKey();
		}

		@Override
		public final V getValue() {
			// TODO Auto-generated method stub
			return value;
		}

		final int rowHash() {
			return rowHead.hash;
		}

		final int columnHash() {
			return columnHead.hash;
		}

		final boolean matchHash(int rowHash, int columnHash) {
			return this.rowHash() == rowHash && this.columnHash() == columnHash;
		}

		@SuppressWarnings("unchecked")
		static final <RK, CK, V> HashEntry<RK, CK, V>[][] newArray(int row,
				int column) {
			return new HashEntry[row][column];
		}
	}

	private static final class Head<K> {
		final K key;
		final int hash;
		volatile int size = 0;
		transient volatile int modCount;
		transient volatile Head<K> next = null;

		transient volatile MapView<K, ?, ?> viewMap = null;

		Head(K key, int hash, Head<K> next) {
			this.key = key;
			this.hash = hash;
			this.next = next;
		}

		final K getKey() {
			return key;
		}

		final int increaseSize(int incr) {
			if (0 == incr)
				return size;
			++modCount;
			size = Math.max(0, size + incr);
			return size;
		}

		final void dispose() {
			size = -1;
			modCount = -1;
			next = null;
			viewMap = null;
		}

		final boolean disposed() {
			return size < 0;
		}

		@SuppressWarnings("unchecked")
		static final <K> Head<K>[] newArray(int len) {
			return new Head[len];
		}
	}

	/**
	 * The maximum number of times to tryLock in a prescan before possibly
	 * blocking on acquire in preparation for a locked segment operation. On
	 * multiprocessors, using a bounded number of retries maintains cache
	 * acquired while locating nodes.
	 */
	static final int MAX_SCAN_RETRIES = Runtime.getRuntime()
			.availableProcessors() > 1 ? 64 : 1;

	/**
	 * Segments are specialized versions of hash tables. This subclasses from
	 * ReentrantLock opportunistically, just to simplify some locking and avoid
	 * separate construction.
	 */
	static final class Block<RK, CK, V> extends ReentrantLock implements
			Serializable {
		/*
		 * Segments maintain a table of entry lists that are always kept in a
		 * consistent state, so can be read (via volatile reads of segments and
		 * tables) without locking. This requires replicating nodes when
		 * necessary during table resizing, so the old lists can be traversed by
		 * readers still using old version of table.
		 * 
		 * This class defines only mutative methods requiring locking. Except as
		 * noted, the methods of this class perform the per-segment versions of
		 * ConcurrentHashMap methods. (Other methods are integrated directly
		 * into ConcurrentHashMap methods.) These mutative methods use a form of
		 * controlled spinning on contention via methods scanAndLock and
		 * scanAndLockForPut. These intersperse tryLocks with traversals to
		 * locate nodes. The main benefit is to absorb cache misses (which are
		 * very common for hash tables) while obtaining locks so that traversal
		 * is faster once acquired. We do not actually use the found nodes since
		 * they must be re-acquired under lock anyway to ensure sequential
		 * consistency of updates (and in any case may be undetectably stale),
		 * but they will normally be much faster to re-locate. Also,
		 * scanAndLockForPut speculatively creates a fresh node to use in put if
		 * no node is found.
		 */

		/**
		 * 
		 */
		private static final long serialVersionUID = 4510748646823023744L;

		/**
		 * The per-segment row head table.
		 */
		final HeadSegment<RK> rowHeads;

		/**
		 * The per-segment column head table.
		 */
		final HeadSegment<CK> columnHeads;
		/**
		 * The per-segment table. Elements are accessed via entryAt/setEntryAt
		 * providing volatile semantics.
		 */
		transient volatile HashEntry<RK, CK, V>[][] table;

		/**
		 * The number of elements. Accessed only either within locks or among
		 * other volatile reads that maintain visibility.
		 */
		transient volatile int count;

		/**
		 * The number of rows and columns. Accessed only either within locks or
		 * among other volatile reads that maintain visibility.
		 */
		transient volatile int rows, columns;

		/**
		 * The total number of mutative operations in this segment. Even though
		 * this may overflows 32 bits, it provides sufficient accuracy for
		 * stability checks in CHM isEmpty() and size() methods. Accessed only
		 * either within locks or among other volatile reads that maintain
		 * visibility.
		 */
		transient int modCount;

		/**
		 * The table is rehashed when its size exceeds this threshold. (The
		 * value of this field is always <tt>(int)(capacity *
		 * loadFactor)</tt>.)
		 */
		transient int rowThreshold, columnThreshold;

		/**
		 * The load factor for the hash table. Even though this value is same
		 * for all segments, it is replicated to avoid needing links to outer
		 * object.
		 * 
		 * @serial
		 */
		final float loadFactor;

		Block(HeadSegment<RK> rowHeads, HeadSegment<CK> columnHeads,
				int initialRowCapacity, int initialColumnCapacity, float lf) {
			super();
			loadFactor = lf;
			this.rowHeads = rowHeads;
			this.columnHeads = columnHeads;
			setTable(HashEntry.<RK, CK, V> newArray(initialRowCapacity,
					initialColumnCapacity));
		}

		@SuppressWarnings("unchecked")
		static final <RK, CK, V> Block<RK, CK, V>[][] newArray(int row,
				int column) {
			return new Block[row][column];
		}

		/**
		 * Sets table to new HashEntry array. Call only while holding lock or in
		 * constructor.
		 */
		private final void setTable(HashEntry<RK, CK, V>[][] newTable) {
			rowThreshold = (int) (newTable.length * loadFactor);
			columnThreshold = (int) (newTable[0].length * loadFactor);
			table = newTable;
		}

		static final int index(int rowHash, Object[] tab) {
			return rowHash & (tab.length - 1);
		}

		/**
		 * Returns properly casted first entry of bin for given hash.
		 */
		final HashEntry<RK, CK, V> getFirst(int rowHash, int columnHash) {
			HashEntry<RK, CK, V>[][] tab = table;
			return tab[index(rowHash, tab)][index(columnHash, tab[0])];
		}

		/**
		 * Reads value field of an entry under lock. Called if value field ever
		 * appears to be null. This is possible only if a compiler happens to
		 * reorder a HashEntry initialization with its table assignment, which
		 * is legal under memory model but is not known to ever occur.
		 */
		final V readValueUnderLock(HashEntry<RK, CK, V> e) {
			lock();
			try {
				return e.value;
			} finally {
				unlock();
			}
		}

		/* Specialized implementations of map methods */

		V get(Object row, int rowHash, Object column, int columnHash) {
			if (count != 0) { // read-volatile
				HashEntry<RK, CK, V> e = getFirst(rowHash, columnHash);
				while (e != null) {
					if (e.matchHash(rowHash, columnHash)
							&& e.match(row, column)) {
						V v = e.value;
						if (v != null)
							return v;
						return readValueUnderLock(e); // recheck
					}
					e = e.next;
				}
			}
			return null;
		}

		boolean containsKey(Object row, int rowHash, Object column,
				int columnHash) {
			if (count != 0) { // read-volatile
				HashEntry<RK, CK, V> e = getFirst(rowHash, columnHash);
				while (e != null) {
					if (e.matchHash(rowHash, columnHash)
							&& e.match(row, column))
						return true;
					e = e.next;
				}
			}
			return false;
		}

		boolean containsValue(Object value) {
			if (count != 0) { // read-volatile
				HashEntry<RK, CK, V>[][] tab = table;
				int row = tab.length;
				int column = tab[0].length;
				for (int i = 0; i < row; i++) {
					for (int j = 0; j < column; ++j) {
						for (HashEntry<RK, CK, V> e = tab[i][j]; e != null; e = e.next) {
							V v = e.value;
							if (v == null) // recheck
								v = readValueUnderLock(e);
							if (value.equals(v))
								return true;
						}
					}
				}
			}
			return false;
		}

		boolean replace(RK row, int rowHash, CK column, int columnHash,
				V oldValue, V newValue) {
			lock();
			try {
				HashEntry<RK, CK, V> e = getFirst(rowHash, columnHash);
				while (e != null
						&& (!e.matchHash(rowHash, columnHash) || !e.match(row,
								column)))
					e = e.next;

				boolean replaced = false;
				if (e != null && oldValue.equals(e.value)) {
					replaced = true;
					e.value = newValue;
				}
				return replaced;
			} finally {
				unlock();
			}
		}

		V replace(RK row, int rowHash, CK column, int columnHash, V newValue) {
			lock();
			try {
				HashEntry<RK, CK, V> e = getFirst(rowHash, columnHash);
				while (e != null
						&& (!e.matchHash(rowHash, columnHash) || !e.match(row,
								column)))
					e = e.next;

				V oldValue = null;
				if (e != null) {
					oldValue = e.value;
					e.value = newValue;
				}
				return oldValue;
			} finally {
				unlock();
			}
		}

		V put(RK row, int rowHash, CK column, int columnHash, V value,
				boolean onlyIfAbsent) {
			lock();
			try {
				int r = rows;
				int c = columns;

				// ensure capacity
				if (r >= rowThreshold || c >= columnThreshold)
					rehash();
				HashEntry<RK, CK, V>[][] tab = table;
				int rowIndex = rowHash & (tab.length - 1);
				int columnIndex = columnHash & (tab[0].length - 1);
				HashEntry<RK, CK, V> first = tab[rowIndex][columnIndex];
				HashEntry<RK, CK, V> e = first;
				while (e != null
						&& (!e.matchHash(rowHash, columnHash) || !e.match(row,
								column)))
					e = e.next;

				V oldValue;
				if (e != null) {
					oldValue = e.value;
					if (!onlyIfAbsent)
						e.value = value;
				} else {
					oldValue = null;
					++modCount;
					rowHeads.lock();
					columnHeads.lock();
					try {
						Head<RK> rowHead = rowHeads.addIfAdsent(row, rowHash);
						Head<CK> columnHead = columnHeads.addIfAdsent(column,
								columnHash);
						tab[rowIndex][columnIndex] = new HashEntry<RK, CK, V>(
								rowHead, columnHead, value, first);
						rowHeads.incrHeadSizeUnderLock(rowHead, 1);
						columnHeads.incrHeadSizeUnderLock(columnHead, 1);
					} finally {
						rowHeads.unlock();
						columnHeads.unlock();
					}
					++count; // write-volatile
				}
				return oldValue;
			} finally {
				unlock();
			}
		}

		void rehash() {
			HashEntry<RK, CK, V>[][] oldTable = table;
			int oldRowCapacity = oldTable.length;
			int oldColumnCapacity = oldTable.length;
			if (oldRowCapacity >= MAXIMUM_CAPACITY
					|| oldColumnCapacity >= MAXIMUM_CAPACITY)
				return;

			int newRowCapacity = oldRowCapacity >= rowThreshold ? oldRowCapacity << 1
					: oldRowCapacity;
			int newColumnCapacity = oldColumnCapacity >= rowThreshold ? oldColumnCapacity << 1
					: oldColumnCapacity;

			HashEntry<RK, CK, V>[][] newTable = HashEntry.newArray(
					newRowCapacity, newColumnCapacity);
			rowThreshold = (int) (newTable.length * loadFactor);
			columnThreshold = (int) (newTable[0].length * loadFactor);
			int rowMask = newTable.length - 1;
			int columnMask = newTable[0].length - 1;
			for (int i = 0; i < oldRowCapacity; i++) {
				for (int j = 0; j < oldColumnCapacity; ++j) {
					// We need to guarantee that any existing reads of old Matrix
					// can
					// proceed. So we cannot yet null out each bin.
					HashEntry<RK, CK, V> e = oldTable[i][j];

					if (e != null) {
						HashEntry<RK, CK, V> next = e.next;
						int ridx = e.rowHash() & rowMask;
						int cidx = e.columnHash() & columnMask;

						// Single node on list
						if (next == null)
							newTable[ridx][cidx] = e;

						else {
							// Reuse trailing consecutive sequence at same slot
							HashEntry<RK, CK, V> lastRun = e;
							int lastRIdx = ridx;
							int lastCIdx = cidx;
							for (HashEntry<RK, CK, V> last = next; last != null; last = last.next) {
								int r = last.rowHash() & rowMask;
								int c = last.columnHash() & columnMask;
								if (r != lastRIdx || c != lastCIdx) {
									lastRIdx = r;
									lastCIdx = c;
									lastRun = last;
								}
							}
							newTable[lastRIdx][lastCIdx] = lastRun;

							// Clone all remaining nodes
							for (HashEntry<RK, CK, V> p = e; p != lastRun; p = p.next) {
								int r = p.rowHash() & rowMask;
								int c = p.columnHash() & columnMask;
								HashEntry<RK, CK, V> n = newTable[r][c];
								newTable[r][c] = new HashEntry<RK, CK, V>(
										p.rowHead, p.columnHead, p.value, n);
							}
						}
					}
				}
			}
			table = newTable;
		}

		/**
		 * Remove; match on key only if value null, else match both.
		 */
		V remove(Object row, int rowHash, Object column, int columnHash,
				Object value) {
			lock();
			try {
				int c = count - 1;
				HashEntry<RK, CK, V>[][] tab = table;
				int rowIndex = rowHash & (tab.length - 1);
				int columnIndex = columnHash & (tab.length - 1);
				HashEntry<RK, CK, V> first = tab[rowIndex][columnIndex];
				HashEntry<RK, CK, V> e = first;
				while (e != null
						&& (!e.matchHash(rowHash, columnHash) || !e.match(row,
								column)))
					e = e.next;

				V oldValue = null;
				if (e != null) {
					V v = e.value;
					if (value == null || value.equals(v)) {
						oldValue = v;
						// All entries following removed node can stay
						// in list, but all preceding ones need to be
						// cloned.
						++modCount;
						HashEntry<RK, CK, V> newFirst = e.next;
						for (HashEntry<RK, CK, V> p = first; p != e; p = p.next)
							newFirst = new HashEntry<RK, CK, V>(p.rowHead,
									p.columnHead, p.value, newFirst);
						rowHeads.lock();
						columnHeads.lock();
						try {
							tab[rowIndex][columnIndex] = newFirst;
							rowHeads.incrHeadSizeUnderLock(e.rowHead, -1);
							columnHeads.incrHeadSizeUnderLock(e.columnHead, -1);
						} finally {
							rowHeads.unlock();
							columnHeads.unlock();
						}
						count = c; // write-volatile
					}
				}
				return oldValue;
			} finally {
				unlock();
			}
		}

		boolean removeRow(Object row, int rowHash) {
			lock();
			rowHeads.lock();
			columnHeads.lock();
			try {
				HashEntry<RK, CK, V>[][] tab = table;
				int rowIndex = rowHash & (tab.length - 1);
				for (int j = 0; j < tab[rowIndex].length; ++j) {
					HashEntry<RK, CK, V> e = tab[rowIndex][j];
					if (e != null) {
						int nagRemoveCount = 0;
						Head<RK> rowHeadToRemove = null;
						HashEntry<RK, CK, V> p = e;
						HashEntry<RK, CK, V> lastRun = e;
						while (p != null) {
							if (p.rowHash() == rowHash
									&& eq(row, p.getRowKey())) {
								--nagRemoveCount;
								rowHeadToRemove = p.rowHead;
								lastRun = p.next;
							}
							p = p.next;
						}

						if (nagRemoveCount < 0) {
							++modCount;
							tab[rowIndex][j] = lastRun;
							while (e != lastRun) {
								if (e.rowHead == rowHeadToRemove) {
									columnHeads.incrHeadSizeUnderLock(
											e.columnHead, -1);
								} else {
									tab[rowIndex][j] = new HashEntry<RK, CK, V>(
											e.rowHead, e.columnHead, e.value,
											tab[rowIndex][j]);
								}
								e = e.next;
							}
							count += nagRemoveCount;
							if (rowHeads.incrHeadSizeUnderLock(rowHeadToRemove,
									nagRemoveCount))
								return true;
						}
					}
				}
				return false;
			} finally {
				unlock();
				rowHeads.unlock();
				columnHeads.unlock();
			}
		}

		boolean removeColumn(Object column, int columnHash) {
			lock();
			rowHeads.lock();
			columnHeads.lock();
			try {
				HashEntry<RK, CK, V>[][] tab = table;
				int columnIndex = columnHash & (tab[0].length - 1);
				for (int i = 0; i < tab.length; ++i) {
					HashEntry<RK, CK, V> e = tab[i][columnIndex];
					if (e != null) {
						int nagRemoveCount = 0;
						Head<CK> columnHeadToRemove = null;
						HashEntry<RK, CK, V> p = e;
						HashEntry<RK, CK, V> lastRun = e;
						while (p != null) {
							if (p.columnHash() == columnHash
									&& eq(column, p.getColumnKey())) {
								--nagRemoveCount;
								columnHeadToRemove = p.columnHead;
								lastRun = p.next;
							}
							p = p.next;
						}

						if (nagRemoveCount < 0) {
							++modCount;
							tab[i][columnIndex] = lastRun;
							while (e != lastRun) {
								if (e.columnHead == columnHeadToRemove) {
									rowHeads.incrHeadSizeUnderLock(e.rowHead,
											-1);
								} else {
									tab[i][columnIndex] = new HashEntry<RK, CK, V>(
											e.rowHead, e.columnHead, e.value,
											tab[i][columnIndex]);
								}
								e = e.next;
							}
							count += nagRemoveCount;
							if (columnHeads.incrHeadSizeUnderLock(
									columnHeadToRemove, nagRemoveCount))
								return true;
						}
					}
				}
				return false;
			} finally {
				unlock();
				rowHeads.unlock();
				columnHeads.unlock();
			}
		}

		void clear() {
			if (count != 0) {
				lock();
				rowHeads.lock();
				columnHeads.lock();
				try {
					HashEntry<RK, CK, V>[][] tab = table;
					for (int i = 0; i < tab.length; i++) {
						for (int j = 0; j < tab[0].length; ++j) {
							HashEntry<RK, CK, V> e = tab[i][j];
							while (e != null) {
								HashEntry<RK, CK, V> next = e.next;
								rowHeads.incrHeadSizeUnderLock(e.rowHead, -1);
								columnHeads.incrHeadSizeUnderLock(e.columnHead,
										-1);
								e = next;
							}
							tab[i][j] = null;
						}
					}
					++modCount;
					count = 0; // write-volatile
				} finally {
					unlock();
					rowHeads.unlock();
					columnHeads.unlock();
				}
			}
		}
	}

	static final class HeadSegment<K> extends ReentrantLock implements
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5328717766017115538L;

		transient volatile Head<K>[] headsTable;

		transient volatile int count;

		transient volatile int modCount;

		transient volatile int threshold;

		/**
		 * The load factor for the hash table. Even though this value is same
		 * for all segments, it is replicated to avoid needing links to outer
		 * object.
		 * 
		 * @serial
		 */
		final float loadFactor;

		HeadSegment(int initialCapacity, float lf) {
			super();
			loadFactor = lf;
			setHeadsTable(Head.<K> newArray(initialCapacity));
		}

		@SuppressWarnings("unchecked")
		static final <K> HeadSegment<K>[] newArray(int len) {
			return new HeadSegment[len];
		}

		final void setHeadsTable(Head<K>[] newTable) {
			threshold = (int) (newTable.length * loadFactor);
			headsTable = newTable;
		}

		private final Head<K> getFirst(int hash) {
			Head<K>[] tab = headsTable;
			return tab[hash & tab.length - 1];
		}

		Head<K> getHead(Object key, int hash) {
			Head<K> h = getHead0(key, hash);
			if (h == null)
				return getHeadUnderLock(key, hash);
			else
				return h;
		}

		Head<K> getHeadUnderLock(Object key, int hash) {
			lock();
			try {
				Head<K> h = getHead0(key, hash);
				return h;
			} finally {
				unlock();
			}
		}

		private final Head<K> getHead0(Object key, int hash) {
			Head<K> h = getFirst(hash);
			while (h != null) {
				if (h.hash == hash && eq(key, h.key))
					return h;
				h = h.next;
			}
			return null;
		}

		boolean containsKey(Object key, int hash) {
			if (containsKey0(key, hash))
				return true;
			else
				return containsKeyUnderLock(key, hash);
		}

		boolean containsKeyUnderLock(Object key, int hash) {
			lock();
			try {
				boolean result = containsKey0(key, hash);
				return result;
			} finally {
				unlock();
			}
		}

		private final boolean containsKey0(Object key, int hash) {
			Head<K> h = getFirst(hash);
			while (h != null) {
				if (h.hash == hash && eq(key, h.key))
					return h.size > 0;
				h = h.next;
			}
			return false;
		}

		Head<K> addIfAdsent(K key, int hash) {
			lock();
			try {
				int c = count;
				if (c++ > threshold)
					rehash();
				Head<K>[] tab = headsTable;
				int index = hash & (tab.length - 1);
				Head<K> first = tab[index];
				Head<K> h = first;
				while (h != null) {
					if (h.hash == hash && eq(key, h.key))
						return h;
					h = h.next;
				}

				++modCount;
				h = tab[index] = new Head<K>(key, hash, first);
				count = c;
				return h;
			} finally {
				unlock();
			}
		}

		void rehash() {
			// TODO Auto-generated method stub
			Head<K>[] oldTable = headsTable;
			int oldCapacity = oldTable.length;
			if (oldCapacity >= MAXIMUM_CAPACITY)
				return;

			Head<K>[] newTable = Head.<K> newArray(oldCapacity << 1);
			threshold = (int) (newTable.length * loadFactor);
			int sizeMask = newTable.length - 1;
			for (int i = 0; i < oldCapacity; i++) {
				// We need to guarantee that any existing reads of old Map can
				// proceed. So we cannot yet null out each bin.
				Head<K> e = oldTable[i];

				while (e != null) {
					Head<K> next = e.next;
					int idx = e.hash & sizeMask;

					e.next = newTable[idx];
					newTable[idx] = e;
					e = next;
				}
			}
			headsTable = newTable;
		}

		boolean remove(Object key, int hash) {
			lock();
			try {
				Head<K>[] tab = headsTable;
				int index = hash & (tab.length - 1);
				Head<K> h = tab[index];
				Head<K> prev = null;
				while (h != null) {
					if (h.hash == hash && eq(key, h.key)) {
						++modCount;
						if (prev == null)
							tab[index] = h.next;
						else
							prev.next = h.next;
						h.dispose();
						--count;
						return true;
					}
					prev = h;
					h = h.next;
				}

				return false;
			} finally {
				unlock();
			}
		}

		boolean remove(Head<K> head) {
			lock();
			try {
				Head<K>[] tab = headsTable;
				int index = head.hash & (tab.length - 1);
				Head<K> h = tab[index];
				Head<K> prev = null;
				while (h != null) {
					if (h == head) {
						++modCount;
						if (prev == null)
							tab[index] = h.next;
						else
							prev.next = h.next;
						h.dispose();
						--count;
						return true;
					}
					prev = h;
					h = h.next;
				}
				return false;
			} finally {
				unlock();
			}
		}

		boolean incrHeadSizeUnderLock(Head<K> head, int incr) {
			lock();
			try {
				if (head.increaseSize(incr) == 0)
					return remove(head);
				else
					return false;
			} finally {
				unlock();
			}
		}

		void clear() {
			lock();
			try {
				Head<K>[] tab = headsTable;
				++modCount;
				for (int i = 0; i < tab.length; ++i) {
					Head<K> h = tab[i];
					while (h != null) {
						Head<K> next = h.next;
						tab[i] = next;
						h.dispose();
						h = next;
					}
				}
				count = 0;
			} finally {
				unlock();
			}
		}

	}

	/**
	 * Save the state of the <tt>ConcurrentHashMap</tt> instance to a stream
	 * (i.e., serialize it).
	 * 
	 * @param s
	 *            the stream
	 * @serialData the key (Object) and value (Object) for each key-value
	 *             mapping, followed by a null pair. The key-value mappings are
	 *             emitted in no particular order.
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		// force all segments for serialization compatibility
		s.defaultWriteObject();

		final Block<RK, CK, V>[][] blocks = this.blocks;
		for (int br = 0; br < blocks.length; ++br) {
			for (int bc = 0; bc < blocks[0].length; ++bc) {
				Block<RK, CK, V> blk = blocks[br][bc];
				blk.lock();
				try {
					HashEntry<RK, CK, V>[][] tab = blk.table;
					for (int tr = 0; tr < tab.length; ++tr) {
						for (int tc = 0; tc < tab[0].length; ++tc) {
							HashEntry<RK, CK, V> e = null;
							for (e = tab[tr][tc]; e != null; e = e.next) {
								s.writeObject(e.getRowKey());
								s.writeObject(e.getColumnKey());
								s.writeObject(e.getValue());
							}
						}
					}
				} finally {
					blk.unlock();
				}
			}
		}
		s.writeObject(null);
		s.writeObject(null);
		s.writeObject(null);
	}

	/**
	 * Reconstitute the <tt>ConcurrentHashMap</tt> instance from a stream (i.e.,
	 * deserialize it).
	 * 
	 * @param s
	 *            the stream
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		s.defaultReadObject();

		final HeadSegment<RK>[] rowHeads = this.rowHeadSegments;
		for (int i = 0; i < rowHeads.length; ++i)
			rowHeads[i].setHeadsTable(Head.<RK> newArray(1));
		final HeadSegment<CK>[] columnHeads = this.columnHeadSegments;
		for (int i = 0; i < columnHeads.length; ++i)
			columnHeads[i].setHeadsTable(Head.<CK> newArray(1));

		final Block<RK, CK, V>[][] blocks = this.blocks;
		for (int i = 0; i < blocks.length; ++i) {
			for (int j = 0; j < blocks[0].length; ++j) {
				blocks[i][j].setTable(HashEntry.<RK, CK, V> newArray(1, 1));
			}
		}

		// Read the keys and values, and put the mappings in the table
		for (;;) {
			RK row = (RK) s.readObject();
			CK column = (CK) s.readObject();
			V value = (V) s.readObject();
			if (row == null)
				break;
			put(row, column, value);
		}
	}

}
