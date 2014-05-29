package hust.idc.util.matrix;

import hust.idc.util.EmptyIterator;
import hust.idc.util.pair.Pair;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class HashMatrix<RK, CK, V> extends AbstractMatrix<RK, CK, V> implements
		Matrix<RK, CK, V>, Cloneable, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -224619744188848843L;

	/**
	 * The default initial capacity - MUST be a power of two.
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

	/**
	 * The table, resized as necessary. Rows MUST be same with length of
	 * rowHeads and columns MUST be same with length of columnHeads.
	 */
	transient Entry[][] table;
	/**
	 * The row head table, resized as necessary. Length MUST Always be a power
	 * of two.
	 */
	transient Head<RK>[] rowHeads;
	/**
	 * The column head table, resized as necessary. Length MUST Always be a
	 * power of two.
	 */
	transient Head<CK>[] columnHeads;

	transient int rows, columns, size;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 */
	int rowThreshold, columnThreshold;

	/**
	 * The load factor for the hash table.
	 * 
	 * @serial
	 */
	final float loadFactor;

	/**
	 * The number of times this HashMatrix has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the HashMatrix or otherwise modify its internal structure (e.g., rehash).
	 * This field is used to make iterators on Collection-views of the HashMatrix
	 * fail-fast. (See ConcurrentModificationException).
	 */
	transient volatile int modCount;

	public HashMatrix() {
		super();
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		rowThreshold = columnThreshold = (int) (DEFAULT_INITIAL_CAPACITY);

		this.rows = this.columns = this.size = 0;
		initBuckets(DEFAULT_INITIAL_CAPACITY, DEFAULT_INITIAL_CAPACITY);
		init();
	}

	public HashMatrix(int initialRows, int initialColumns) {
		this(initialRows, initialColumns, DEFAULT_LOAD_FACTOR);
	}

	public HashMatrix(int initialRows, int initialColumns, float loadFactor) {
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
		rowThreshold = (int) (rowCapacity * loadFactor);
		columnThreshold = (int) (columnCapacity * loadFactor);
		this.rows = this.columns = this.size = 0;
		initBuckets(rowCapacity, columnCapacity);
		init();
	}

	public HashMatrix(int initialCapacity, float loadFactor) {
		this(initialCapacity, initialCapacity, loadFactor);
	}

	public HashMatrix(
			Matrix<? extends RK, ? extends CK, ? extends V> otherMatrix) {
		this(Math.max((int) (otherMatrix.rows() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), Math.max(
				(int) (otherMatrix.columns() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
		putAllForCreate(otherMatrix);
	}

	public HashMatrix(
			Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> otherMatrix) {
		this();
		this.putAll(otherMatrix);
	}
	
	@SuppressWarnings("unchecked")
	void initBuckets(int rowCapacity, int columnCapacity){
		table = new HashMatrix.Entry[rowCapacity][columnCapacity];
		rowHeads = new Head[rowCapacity];
		columnHeads = new Head[columnCapacity];
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

	Head<RK> rowHead(Object key) {
		int hash = (key == null ? 0 : hash(key.hashCode()));
		int index = indexFor(hash, rowHeads.length);
		return rowHead(key, hash, index);
	}

	Head<RK> rowHead(Object key, int hash, int index) {
		Head<RK> head = rowHeads[index];
		if (null == key) {
			while (head != null) {
				if (hash == head.hash && null == head.getKey())
					return head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (hash == head.hash && key.equals(head.getKey()))
					return head;
				head = head.next;
			}
		}
		return null;
	}

	Head<CK> columnHead(Object key) {
		int hash = (key == null ? 0 : hash(key.hashCode()));
		int index = indexFor(hash, columnHeads.length);
		return columnHead(key, hash, index);
	}

	Head<CK> columnHead(Object key, int hash, int index) {
		Head<CK> head = columnHeads[index];
		if (null == key) {
			while (head != null) {
				if (hash == head.hash && null == head.getKey())
					return head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (hash == head.hash && key.equals(head.getKey()))
					return head;
				head = head.next;
			}
		}
		return null;
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		return null != rowHead(row);
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		return null != columnHead(column);
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		int rowHash = (row == null ? 0 : hash(row.hashCode()));
		int rowIndex = indexFor(rowHash, rowHeads.length);
		int columnHash = (column == null ? 0 : hash(column.hashCode()));
		int columnIndex = indexFor(columnHash, columnHeads.length);

		Entry entry = table[rowIndex][columnIndex];
		while (entry != null) {
			if (entry.matchHash(rowHash, columnHash)
					&& entry.match(row, column))
				return true;
			entry = entry.next;
		}
		return false;
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		int rowHash = (row == null ? 0 : hash(row.hashCode()));
		int rowIndex = indexFor(rowHash, rowHeads.length);
		int columnHash = (column == null ? 0 : hash(column.hashCode()));
		int columnIndex = indexFor(columnHash, columnHeads.length);

		Entry entry = table[rowIndex][columnIndex];
		while (entry != null) {
			if (entry.matchHash(rowHash, columnHash)
					&& entry.match(row, column))
				return entry.getValue();
			entry = entry.next;
		}
		return null;
	}

	private Head<RK> addRowHeadIfNotExists(RK key) {
		int hash = (key == null ? 0 : hash(key.hashCode()));
		int index = indexFor(hash, rowHeads.length);
		return addRowHeadIfNotExists(key, hash, index);
	}

	private Head<RK> addRowHeadIfNotExists(RK key, int hash, int index) {
		Head<RK> head = rowHeads[index];
		if (null == key) {
			while (head != null) {
				if (hash == head.hash && null == head.getKey())
					return head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (hash == head.hash && key.equals(head.getKey()))
					return head;
				head = head.next;
			}
		}

		head = new Head<RK>(key, hash, rowHeads[index]);
		rowHeads[index] = head;
		++rows;
		return head;
	}

	private Head<CK> addColumnHeadIfNotExists(CK key) {
		int hash = (key == null ? 0 : hash(key.hashCode()));
		int index = indexFor(hash, columnHeads.length);
		return addColumnHeadIfNotExists(key, hash, index);
	}

	private Head<CK> addColumnHeadIfNotExists(CK key, int hash, int index) {
		Head<CK> head = columnHeads[index];
		if (null == key) {
			while (head != null) {
				if (hash == head.hash && null == head.getKey())
					return head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (hash == head.hash && key.equals(head.getKey()))
					return head;
				head = head.next;
			}
		}

		head = new Head<CK>(key, hash, columnHeads[index]);
		columnHeads[index] = head;
		++columns;
		return head;
	}

	@SuppressWarnings("unchecked")
	void resizeRow() {
		// TODO Auto-generated method stub
		if (this.rowCapacity() == MAXIMUM_CAPACITY) {
			rowThreshold = Integer.MAX_VALUE;
			return;
		}
		++modCount;
		int newCapacity = this.rowCapacity() << 1;

		Head<RK>[] newRowHeads = new Head[newCapacity];
		transferRows(newRowHeads);
		rowHeads = newRowHeads;
		rowThreshold = (int) (newCapacity * loadFactor);

		Entry[][] newTable = new HashMatrix.Entry[newCapacity][this
				.columnCapacity()];
		transferEntries(newTable);
		table = newTable;
	}

	@SuppressWarnings("unchecked")
	void resizeColumn() {
		// TODO Auto-generated method stub
		if (this.columnCapacity() == MAXIMUM_CAPACITY) {
			columnThreshold = Integer.MAX_VALUE;
			return;
		}

		++modCount;
		int newCapacity = this.columnCapacity() << 1;
		Head<CK>[] newColumnHeads = new Head[newCapacity];
		transferColumns(newColumnHeads);
		columnHeads = newColumnHeads;
		columnThreshold = (int) (newCapacity * loadFactor);

		Entry[][] newTable = new HashMatrix.Entry[this.rowCapacity()][newCapacity];
		transferEntries(newTable);
		table = newTable;
	}

	@SuppressWarnings("unchecked")
	void resize() {
		// TODO Auto-generated method stub
		boolean canResize = false;

		if (this.rowCapacity() == MAXIMUM_CAPACITY) {
			rowThreshold = Integer.MAX_VALUE;
		} else {
			canResize = true;
		}

		if (this.columnCapacity() == MAXIMUM_CAPACITY) {
			columnThreshold = Integer.MAX_VALUE;
			return;
		} else {
			canResize = true;
		}

		if (!canResize)
			return;

		++modCount;
		int newRowCapacity = this.rowCapacity() << 1;
		Head<RK>[] newRowHeads = new Head[newRowCapacity];
		transferRows(newRowHeads);
		rowHeads = newRowHeads;
		rowThreshold = (int) (newRowCapacity * loadFactor);

		int newColumnCapacity = this.columnCapacity() << 1;
		Head<CK>[] newColumnHeads = new Head[newColumnCapacity];
		transferColumns(newColumnHeads);
		columnHeads = newColumnHeads;
		columnThreshold = (int) (newColumnCapacity * loadFactor);

		Entry[][] newTable = new HashMatrix.Entry[newRowCapacity][newColumnCapacity];
		transferEntries(newTable);
		table = newTable;
	}

	void transferRows(Head<RK>[] newRowHeads) {
		// TODO Auto-generated method stub
		for (int i = 0; i < this.rowCapacity(); ++i) {
			Head<RK> head = rowHeads[i];
			while (head != null) {
				Head<RK> next = head.next;
				int newIndex = indexFor(head.hash, newRowHeads.length);
				head.next = newRowHeads[newIndex];
				newRowHeads[newIndex] = head;
				head = next;
			}
			rowHeads[i] = null;
		}
	}

	void transferColumns(Head<CK>[] newColumnHeads) {
		// TODO Auto-generated method stub
		for (int i = 0; i < this.columnCapacity(); ++i) {
			Head<CK> head = columnHeads[i];
			while (head != null) {
				Head<CK> next = head.next;
				int newIndex = indexFor(head.hash, newColumnHeads.length);
				head.next = newColumnHeads[newIndex];
				newColumnHeads[newIndex] = head;
				head = next;
			}
			columnHeads[i] = null;
		}
	}

	void transferEntries(Entry[][] newTable) {
		// TODO Auto-generated method stub
		int rowCapacity = newTable.length;
		int columnCapacity = newTable[0].length;

		for (int i = 0; i < this.rowCapacity(); ++i) {
			for (int j = 0; j < this.columnCapacity(); ++j) {
				Entry entry = table[i][j];
				while (entry != null) {
					Entry next = entry.next;
					int rowIndex = indexFor(entry.rowHash(), rowCapacity);
					int columnIndex = indexFor(entry.columnHash(),
							columnCapacity);
					entry.next = newTable[rowIndex][columnIndex];
					newTable[rowIndex][columnIndex] = entry;
					entry = next;
				}
				table[i][j] = null;
			}
		}
	}

	private V setValueAt(int rowIndex, int columnIndex, Head<RK> rowHead,
			Head<CK> columnHead, V value) {
		Entry entry = table[rowIndex][columnIndex];
		while (entry != null) {
			if (entry.matchHash(rowHead.hash, columnHead.hash)
					&& entry.match(rowHead.getKey(), columnHead.getKey())) {
				entry.recordAccess(this);
				return entry.setValue(value);
			}
			entry = entry.next;
		}

		++modCount;
		table[rowIndex][columnIndex] = new Entry(value, rowHead, columnHead,
				table[rowIndex][columnIndex]);
		++size;
		rowHead.increaseSize(1);
		columnHead.increaseSize(1);
		return null;
	}

	@Override
	public V put(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		int rowHash = (row == null ? 0 : hash(row.hashCode()));
		int rowIndex = indexFor(rowHash, rowHeads.length);
		Head<RK> rowHead = addRowHeadIfNotExists(row, rowHash, rowIndex);
		boolean resizeRow = rows >= rowThreshold;

		int columnHash = (column == null ? 0 : hash(column.hashCode()));
		int columnIndex = indexFor(columnHash, columnHeads.length);
		Head<CK> columnHead = addColumnHeadIfNotExists(column, columnHash,
				columnIndex);
		boolean resizeColumn = columns >= columnThreshold;

		if (resizeRow && resizeColumn)
			resize();
		else if (resizeRow)
			resizeRow();
		else if (resizeColumn)
			resizeColumn();

		return setValueAt(rowIndex, columnIndex, rowHead, columnHead, value);
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		int rowHash = (row == null ? 0 : hash(row.hashCode()));
		int rowIndex = indexFor(rowHash, rowHeads.length);

		int columnHash = (column == null ? 0 : hash(column.hashCode()));
		int columnIndex = indexFor(columnHash, columnHeads.length);

		Entry entry = table[rowIndex][columnIndex], prev = null;
		while (entry != null) {
			if (entry.matchHash(rowHash, columnHash)
					&& entry.match(row, column)) {
				++modCount;
				// remove entry from the
				if (null == prev)
					table[rowIndex][columnIndex] = entry.next;
				else
					prev.next = entry.next;
				--size;

				if (entry.rowHead.increaseSize(-1) == 0)
					removeRowHead(rowIndex, entry.rowHead);
				if (entry.columnHead.increaseSize(-1) == 0)
					removeColumnHead(columnIndex, entry.columnHead);

				V oldValue = entry.getValue();
				entry.recordRemoval(this);
				entry.dispose();
				return oldValue;
			}
			prev = entry;
			entry = entry.next;
		}
		return null;
	}

	private void removeRowHead(int index, Head<RK> head) {
		// TODO Auto-generated method stub
		Head<RK> h = rowHeads[index], prev = null;
		while (h != null) {
			if (head == h) {
				removeRowHead(index, head, prev);
				return;
			}
			prev = h;
			h = h.next;
		}
	}

	private void removeRowHead(int index, Head<RK> head, Head<RK> prev) {
		// TODO Auto-generated method stub
		++modCount;
		if (prev == null)
			rowHeads[index] = head.next;
		else
			prev.next = head.next;
		--rows;
		head.dispose();
	}

	private void removeColumnHead(int index, Head<CK> head) {
		// TODO Auto-generated method stub
		Head<CK> h = columnHeads[index], prev = null;
		while (h != null) {
			if (head == h) {
				removeColumnHead(index, head, prev);
				return;
			}
			prev = h;
			h = h.next;
		}
	}

	private void removeColumnHead(int index, Head<CK> head, Head<CK> prev) {
		++modCount;
		if (prev == null)
			columnHeads[index] = head.next;
		else
			prev.next = head.next;
		--columns;
		head.dispose();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		++modCount;
		for (int i = 0; i < this.rowCapacity(); ++i) {
			for (int j = 0; j < this.columnCapacity(); ++j) {
				Entry entry = table[i][j];
				while (entry != null) {
					Entry next = entry.next;
					entry.recordRemoval(this);
					entry.dispose();
					entry = next;
				}
				table[i][j] = null;
			}

			Head<RK> head = rowHeads[i];
			while (head != null) {
				Head<RK> next = head.next;
				head.dispose();
				head = next;
			}
			rowHeads[i] = null;
		}

		for (int j = 0; j < this.columnCapacity(); ++j) {
			Head<CK> head = columnHeads[j];
			while (head != null) {
				Head<CK> next = head.next;
				head.dispose();
				head = next;
			}
			columnHeads[j] = null;
		}
		this.rows = this.columns = this.size = 0;
	}

	@Override
	public void removeRow(RK row) {
		// TODO Auto-generated method stub
		int rowHash = (row == null ? 0 : hash(row.hashCode()));
		int rowIndex = indexFor(rowHash, rowHeads.length);

		// find the rowHead and its prev
		Head<RK> head = rowHeads[rowIndex], prev = null, rowHead = null;
		if (row == null) {
			while (head != null) {
				if (head.hash == rowHash && head.getKey() == null) {
					rowHead = head;
					break;
				}
				prev = head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (head.hash == rowHash && row.equals(head.getKey())) {
					rowHead = head;
					break;
				}
				prev = head;
				head = head.next;
			}
		}
		if (rowHead == null)
			return;

		if (rowHead.size > 0) {
			// remove all entry of this row
			for (int j = 0; j < this.columnCapacity(); ++j) {
				Entry entry = table[rowIndex][j], prevEntry = null;
				while (entry != null) {
					if (entry.rowHead == rowHead) {
						if (prevEntry == null)
							table[rowIndex][j] = entry.next;
						else
							prevEntry.next = entry.next;
						--size;
						if (entry.columnHead.increaseSize(-1) == 0)
							removeColumnHead(j, entry.columnHead);

						entry.recordRemoval(this);
						entry.dispose();
					}
				}
			}
		}

		removeRowHead(rowIndex, rowHead, prev);
	}

	@Override
	public void removeColumn(CK column) {
		// TODO Auto-generated method stub
		int columnHash = (column == null ? 0 : hash(column.hashCode()));
		int columnIndex = indexFor(columnHash, columnHeads.length);

		// find the rowHead and its prev
		Head<CK> head = columnHeads[columnIndex], prev = null, columnHead = null;
		if (column == null) {
			while (head != null) {
				if (head.hash == columnHash && head.getKey() == null) {
					columnHead = head;
					break;
				}
				prev = head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (head.hash == columnHash && column.equals(head.getKey())) {
					columnHead = head;
					break;
				}
				prev = head;
				head = head.next;
			}
		}
		if (columnHead == null)
			return;

		if (columnHead.size > 0) {
			// remove all entry of this row
			for (int j = 0; j < this.rowCapacity(); ++j) {
				Entry entry = table[j][columnIndex], prevEntry = null;
				while (entry != null) {
					if (entry.columnHead == columnHead) {
						if (prevEntry == null)
							table[j][columnIndex] = entry.next;
						else
							prevEntry.next = entry.next;
						--size;
						if (entry.rowHead.increaseSize(-1) == 0)
							removeRowHead(j, entry.rowHead);

						entry.recordRemoval(this);
						entry.dispose();
					}
				}
			}
		}

		removeColumnHead(columnIndex, columnHead, prev);
	}

	/**
	 * Returns a shallow copy of this <tt>HashMatrix</tt> instance: the keys and
	 * values themselves are not cloned.
	 * 
	 * @return a shallow copy of this matrix
	 */
	@Override
	public HashMatrix<RK, CK, V> clone() {
		// TODO Auto-generated method stub
		HashMatrix<RK, CK, V> clone = null;
		try {
			clone = (HashMatrix<RK, CK, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			// assert false
			e.printStackTrace();
		}

		clone.modCount = 0;
		clone.rows = clone.columns = clone.size = 0;
		clone.initBuckets(this.rowCapacity(), this.columnCapacity());
		clone.init();
		clone.putAllForCreate(this);

		return clone;
	}
	
	@Override
	void clearViews(){
		super.clearViews();
		rowKeySet = null;
		columnKeySet = null;
		entrySet = null;
	}

	private void putAllForCreate(
			Matrix<? extends RK, ? extends CK, ? extends V> m) {
		// TODO Auto-generated method stub
		for (Iterator<? extends Matrix.Entry<? extends RK, ? extends CK, ? extends V>> i = m
				.entrySet().iterator(); i.hasNext();) {
			Matrix.Entry<? extends RK, ? extends CK, ? extends V> e = i.next();
			putForCreate(e.getRowKey(), e.getColumnKey(), e.getValue());
		}
	}

	/**
	 * This method is used instead of put by constructors and pseudoconstructors
	 * (clone, readObject). It does not resize the table, check for
	 * comodification, etc.
	 */
	private void putForCreate(RK rowKey, CK columnKey, V value) {
		// TODO Auto-generated method stub
		int rowHash = (rowKey == null) ? 0 : hash(rowKey.hashCode());
		int rowIndex = indexFor(rowHash, this.rowCapacity());
		Head<RK> rowHead = addRowHeadIfNotExists(rowKey, rowHash, rowIndex);

		int columnHash = (columnKey == null) ? 0 : hash(columnKey.hashCode());
		int columnIndex = indexFor(columnHash, this.columnCapacity());
		Head<CK> columnHead = addColumnHeadIfNotExists(columnKey, columnHash,
				columnIndex);

		table[rowIndex][columnIndex] = new Entry(value, rowHead, columnHead,
				table[rowIndex][columnIndex]);
		++size;
	}

	@Override
	protected int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		Head<RK> head = rowHead(row);
		return null == head ? 0 : head.size;
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		Head<CK> head = columnHead(column);
		return null == head ? 0 : head.size;
	}

	// View
	protected transient volatile Set<RK> rowKeySet = null;
	protected transient volatile Set<CK> columnKeySet = null;
	protected transient volatile Set<Matrix.Entry<RK, CK, V>> entrySet = null;

	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		if (rowKeySet == null) {
			rowKeySet = new AbstractSet<RK>() {

				@Override
				public Iterator<RK> iterator() {
					// TODO Auto-generated method stub
					return new RowKeyIterator();
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return HashMatrix.this.rows;
				}

			};
		}
		return rowKeySet;
	}

	@Override
	public Set<CK> columnKeySet() {
		// TODO Auto-generated method stub
		if (columnKeySet == null) {
			columnKeySet = new AbstractSet<CK>() {

				@Override
				public Iterator<CK> iterator() {
					// TODO Auto-generated method stub
					return new ColumnKeyIterator();
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return HashMatrix.this.columns;
				}

			};
		}
		return columnKeySet;
	}

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
					return HashMatrix.this.size;
				}

			};
		}
		return entrySet;
	}

	private abstract class FastFailedIterator<E> implements Iterator<E> {
		int expectedModCount;

		FastFailedIterator() {
			resetModCount();
		}

		void checkModCount() {
			if (HashMatrix.this.modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
		
		void resetModCount(){
			expectedModCount = HashMatrix.this.modCount;
		}
	}

	private final class RowKeyIterator extends FastFailedIterator<RK> {
		private Head<RK> current = null, next = null;
		private int index = 0;

		private RowKeyIterator() {
			super();
			while (index < rowHeads.length
					&& (next = rowHeads[index++]) == null)
				;
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return null != next;
		}

		@Override
		public RK next() {
			// TODO Auto-generated method stub
			checkModCount();
			if (null == next)
				throw new NoSuchElementException();
			current = next;
			if ((next = next.next) == null) {
				while (index < rowHeads.length
						&& (next = rowHeads[index++]) == null)
					;
			}
			return current.getKey();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			if (null == current)
				throw new IllegalStateException();
			checkModCount();
			RK key = current.getKey();
			current = null;
			HashMatrix.this.removeRow(key);
			resetModCount();
		}

	}

	private final class ColumnKeyIterator extends FastFailedIterator<CK> {
		private Head<CK> current = null, next = null;
		private int index = 0;

		private ColumnKeyIterator() {
			super();
			while (index < columnHeads.length
					&& (next = columnHeads[index++]) == null)
				;
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return null != next;
		}

		@Override
		public CK next() {
			// TODO Auto-generated method stub
			checkModCount();
			if (null == next)
				throw new NoSuchElementException();
			current = next;
			if ((next = next.next) == null) {
				while (index < columnHeads.length
						&& (next = columnHeads[index++]) == null)
					;
			}
			return current.getKey();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			if (null == current)
				throw new IllegalStateException();
			checkModCount();
			CK key = current.getKey();
			current = null;
			HashMatrix.this.removeColumn(key);
			resetModCount();
		}

	}

	private final class EntryIterator extends
			FastFailedIterator<Matrix.Entry<RK, CK, V>> {
		private Entry current = null, next;
		private int row = 0, column = 0;

		private EntryIterator() {
			super();
			getNext();
		}

		private void getNext() {
			int rowCapacity = HashMatrix.this.rowCapacity();
			int columnCapacity = HashMatrix.this.columnCapacity();

			do {
				for (; column < columnCapacity
						&& (next = table[row][column++]) == null;)
					;
				if (next == null) {
					++row;
					column = 0;
				}
			} while (row < rowCapacity && next == null);
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return next != null;
		}

		@Override
		public Matrix.Entry<RK, CK, V> next() {
			// TODO Auto-generated method stub
			checkModCount();
			if (null == next)
				throw new NoSuchElementException();
			current = next;
			if ((next = next.next) == null)
				getNext();
			return current;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			if (null == current)
				throw new IllegalStateException();
			checkModCount();
			RK row = current.getRowKey();
			CK column = current.getColumnKey();
			current = null;
			HashMatrix.this.remove(row, column);
			resetModCount();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<CK, V> rowMap(RK row) {
		// TODO Auto-generated method stub
		Head<RK> head = rowHead(row);
		if (null == head) {
			return new RowMapView(row, null);
		}

		if (null == head.viewMap) {
			head.viewMap = new RowMapView(row, head);
		}
		return (Map<CK, V>) head.viewMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<RK, V> columnMap(CK column) {
		// TODO Auto-generated method stub
		Head<CK> head = columnHead(column);
		if (null == head) {
			return new ColumnMapView(column, null);
		}

		if (null == head.viewMap) {
			head.viewMap = new ColumnMapView(column, head);
		}
		return (Map<RK, V>) head.viewMap;
	}

	private final class RowMapView extends AbstractMap<CK, V> {
		private transient volatile Head<RK> head;
		private final transient RK row;

		private RowMapView(RK row, Head<RK> head) {
			this.row = row;
			this.head = head;
			modCount = head == null ? 0 : head.modCount;
		}
		
		private int modCount(){
			return head == null ? 0 : head.modCount;
		}

		@Override
		public V put(CK key, V value) {
			// TODO Auto-generated method stub
			if (null == head) {
				head = HashMatrix.this.addRowHeadIfNotExists(row);
				head.viewMap = this;
			}
			return HashMatrix.this.put(row, key, value);
		}

		private transient volatile Set<Map.Entry<CK, V>> entrySet = null;

		@Override
		public Set<Map.Entry<CK, V>> entrySet() {
			// TODO Auto-generated method stub
			if (null == entrySet) {
				entrySet = new AbstractSet<Map.Entry<CK, V>>() {
					@Override
					public Iterator<Map.Entry<CK, V>> iterator() {
						// TODO Auto-generated method stub
						if (head == null)
							return EmptyIterator.<Map.Entry<CK, V>>getInstance();
						else
							return new EntryIterator();
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return head == null ? 0 : head.size;
					}
				};
			}
			return entrySet;
		}

		private final class EntryIterator implements
				Iterator<Map.Entry<CK, V>> {
			private HashMatrix<RK, CK, V>.Entry current = null, next;
			private final int rowIndex = indexFor(head.hash, rowHeads.length);
			private volatile int columnIndex = 0;
			
			private int expectedModCount;

			private EntryIterator() {
				expectedModCount = RowMapView.this.modCount();
				getNext();
			}
			
			void checkModCount() {
				if (RowMapView.this.modCount() != expectedModCount)
					throw new ConcurrentModificationException();
			}

			private void getNext() {
				while (columnIndex < columnHeads.length) {
					if (table[rowIndex][columnIndex] != null
							&& table[rowIndex][columnIndex].rowHead == head) {
						next = table[rowIndex][columnIndex++];
						break;
					}
					++columnIndex;
				}
			}

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return next != null;
			}

			@Override
			public Map.Entry<CK, V> next() {
				// TODO Auto-generated method stub
				checkModCount();
				if (null == next)
					throw new NoSuchElementException();
				current = next;
				while ((next = next.next) != null) {
					if (eq(row, next.getRowKey()))
						break;
				}
				if (next == null) {
					getNext();
				}
				return current.rowMapEntry();
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				if (null == current)
					throw new IllegalStateException();
				checkModCount();
				CK column = current.getColumnKey();
				current = null;
				HashMatrix.this.remove(row, column);
				if (head.disposed())
					head = null;
				expectedModCount = RowMapView.this.modCount();
			}

		}
	}

	private final class ColumnMapView extends AbstractMap<RK, V> {
		private transient volatile Head<CK> head;
		private final transient CK column;

		private ColumnMapView(CK column, Head<CK> head) {
			this.column = column;
			this.head = head;
		}
		
		private int modCount(){
			return head == null ? 0 : head.modCount;
		}

		@Override
		public V put(RK key, V value) {
			// TODO Auto-generated method stub
			if (null == head) {
				head = HashMatrix.this.addColumnHeadIfNotExists(column);
				head.viewMap = this;
			}
			return HashMatrix.this.put(key, column, value);
		}

		private transient volatile Set<Map.Entry<RK, V>> entrySet = null;

		@Override
		public Set<Map.Entry<RK, V>> entrySet() {
			// TODO Auto-generated method stub
			if (null == entrySet) {
				entrySet = new AbstractSet<Map.Entry<RK, V>>() {
					@Override
					public Iterator<Map.Entry<RK, V>> iterator() {
						// TODO Auto-generated method stub
						if (null == head)
							return EmptyIterator.<Map.Entry<RK, V>>getInstance();
						else
							return new EntryIterator();
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return head == null ? 0 : head.size;
					}
				};
			}
			return entrySet;
		}

		private final class EntryIterator implements Iterator<Map.Entry<RK, V>> {

			private HashMatrix<RK, CK, V>.Entry current = null, next;
			private int rowIndex = 0;
			private final int columnIndex = indexFor(head.hash,
					columnHeads.length);
			
			private int expectedModCount;

			private EntryIterator() {
				expectedModCount = ColumnMapView.this.modCount();
				getNext();
			}
			
			void checkModCount() {
				if (ColumnMapView.this.modCount() != expectedModCount)
					throw new ConcurrentModificationException();
			}

			private void getNext() {
				while (rowIndex < rowHeads.length) {
					if (table[rowIndex][columnIndex] != null
							&& table[rowIndex][columnIndex].columnHead == head) {
						next = table[rowIndex++][columnIndex];
						break;
					}
					++rowIndex;
				}
			}

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return next != null;
			}

			@Override
			public Map.Entry<RK, V> next() {
				// TODO Auto-generated method stub
				checkModCount();
				if (null == next)
					throw new NoSuchElementException();
				current = next;
				while ((next = next.next) != null) {
					if (eq(column, next.getColumnKey()))
						break;
				}
				if (next == null) {
					getNext();
				}
				return current.columnMapEntry();
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				if (null == current)
					throw new IllegalStateException();
				checkModCount();
				RK row = current.getRowKey();
				current = null;
				HashMatrix.this.remove(row, column);

				if (head.disposed())
					head = null;
				expectedModCount = ColumnMapView.this.modCount();
			}

		}
	}

	private class Entry extends AbstractEntry<RK, CK, V> {
		V value;
		transient Head<RK> rowHead;
		transient Head<CK> columnHead;
		transient Entry next = null;

		Entry(V value, Head<RK> rowHead, Head<CK> columnHead, Entry next) {
			this.value = value;
			this.rowHead = rowHead;
			this.columnHead = columnHead;
			this.next = next;
		}

		@Override
		public RK getRowKey() {
			// TODO Auto-generated method stub
			return rowHead.getKey();
		}

		@Override
		public CK getColumnKey() {
			// TODO Auto-generated method stub
			return columnHead.getKey();
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

		int rowHash() {
			return rowHead == null ? 0 : rowHead.hash;
		}

		int columnHash() {
			return columnHead == null ? 0 : columnHead.hash;
		}

		boolean matchHash(int rowHash, int columnHash) {
			return this.rowHash() == rowHash && this.columnHash() == columnHash;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = rowHash() + columnHash() * prime;
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
			if (obj instanceof HashMatrix.Entry) {
				HashMatrix<?, ?, ?>.Entry other = (HashMatrix<?, ?, ?>.Entry) obj;
				if (other.rowHash() != this.rowHash()
						|| other.columnHash() != this.columnHash())
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
		void dispose(){
			super.dispose();
			rowHead = null;
			columnHead = null;
			next = null;
			value = null;
		}

		/**
		 * This method is invoked whenever the value in an entry is overwritten
		 * by an invocation of put(k,v) for a key k that's already in the
		 * HashMatrix.
		 */
		void recordAccess(HashMatrix<RK, CK, V> m) {
		}

		/**
		 * This method is invoked whenever the entry is removed from the table.
		 */
		void recordRemoval(HashMatrix<RK, CK, V> m) {
		}
	}

	private class Head<K> {
		final K key;
		final int hash;
		int size = 0;
		transient volatile int modCount;
		transient Head<K> next = null;

		transient volatile Map<?, V> viewMap = null;

		Head(K key, int hash, Head<K> next) {
			this.key = key;
			this.hash = hash;
			this.next = next;
		}

		K getKey() {
			return key;
		}

		int increaseSize(int incr) {
			if(0 == incr)
				return size;
			++modCount;
			size = Math.max(0, size + incr);
			return size;
		}

		void dispose() {
			size = -1;
			next = null;
			viewMap = null;
		}

		boolean disposed() {
			return size < 0;
		}
	}

	/**
     * Save the state of the <tt>HashMatrix</tt> instance to a stream (i.e.,
     * serialize it).
     *
     * @serialData The <i>capacity</i> of the HashMatrix (the row and column of the
     *		   bucket array) is emitted (int), followed by the
     *		   <i>size</i> (an int, the number of keypair-value
     *		   mappings), followed by the rowKey (Object), columnKey (Object) and value (Object)
     *		   for each keypair-value mapping.  The keypair-value mappings are
     *		   emitted in no particular order.
     */
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		Iterator<Matrix.Entry<RK, CK, V>> i = isEmpty() ? null : entrySet().iterator();

		// Write out the threshold, loadfactor, and any hidden stuff
		s.defaultWriteObject();

		// Write out number of buckets
		s.writeInt(this.rowCapacity());
		s.writeInt(this.columnCapacity());

		// Write out size (number of Mappings)
		s.writeInt(size);

		// Write out keys and values (alternating)
		if (i != null) {
			while (i.hasNext()) {
				Matrix.Entry<RK, CK, V> e = i.next();
				s.writeObject(e.getRowKey());
				s.writeObject(e.getColumnKey());
				s.writeObject(e.getValue());
			}
		}
	}

	/**
	 * Reconstitute the <tt>HashMatrix</tt> instance from a stream (i.e.,
	 * deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		// Read in the threshold, loadfactor, and any hidden stuff
		s.defaultReadObject();

		// Read in number of buckets and allocate the bucket array;
		int rowCapacity = s.readInt();
		int columnCapacity = s.readInt();

		initBuckets(rowCapacity, columnCapacity);

		init(); // Give subclass a chance to do its thing.

		// Read in size (number of Mappings)
		int size = s.readInt();

		// Read the keys and values, and put the mappings in the HashMap
		for (int i = 0; i < size; i++) {
			RK row = (RK) s.readObject();
			CK column = (CK) s.readObject();
			V value = (V) s.readObject();
			putForCreate(row, column, value);
		}
	}

	int rowCapacity() {
		return rowHeads.length;
	}

	int columnCapacity() {
		return columnHeads.length;
	}

	float loadFactor() {
		return loadFactor;
	}

}
