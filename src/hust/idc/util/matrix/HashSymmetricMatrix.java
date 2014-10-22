package hust.idc.util.matrix;

import hust.idc.util.pair.UnorderedPair;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class HashSymmetricMatrix<K, V> extends AbstractSymmetricMatrix<K, V>
		implements SymmetricMatrix<K, V>, Matrix<K, K, V>, Cloneable,
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5561537055012178013L;

	/**
	 * The default initial capacity - MUST be a power of two.
	 */
	static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<15.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 15;

	/**
	 * The load fast used when none specified in constructor.
	 */
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * The table, resized as necessary.
	 */
	transient Entry[] table;
	/**
	 * The head table, resized as necessary. Length MUST Always be a power of
	 * two.
	 */
	transient Head[] heads;

	transient int size, dimension;

	/**
	 * The next size value at which to resize (dimensionCapacity * load factor).
	 */
	int threshold;

	/**
	 * The load factor for the hash table.
	 * 
	 * @serial
	 */
	final float loadFactor;

	/**
	 * The number of times this HashSymmetricMatrix has been structurally
	 * modified Structural modifications are those that change the number of
	 * mappings in the HashSymmetricMatrix or otherwise modify its internal
	 * structure (e.g., rehash). This field is used to make iterators on
	 * Collection-views of the HashMap fail-fast. (See
	 * ConcurrentModificationException).
	 */
	transient volatile int modCount;

	public HashSymmetricMatrix() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	public HashSymmetricMatrix(int initialDimensions) {
		this(initialDimensions, DEFAULT_LOAD_FACTOR);
	}

	public HashSymmetricMatrix(int initialDimensions, float loadFactor) {
		super();
		if (initialDimensions < 0)
			throw new IllegalArgumentException("Illegal Dimension: "
					+ initialDimensions);

		int dimensionCapacity = ensureCapacity(Math.min(MAXIMUM_CAPACITY,
				initialDimensions));
		this.loadFactor = loadFactor;
		threshold = (int) (dimensionCapacity * loadFactor);
		this.dimension = this.size = 0;
		initBuckets(dimensionCapacity);
		init();
	}

	public HashSymmetricMatrix(
			SymmetricMatrix<? extends K, ? extends V> otherMatrix) {
		this(
				Math.max(
						(int) (Math.max(otherMatrix.rows(),
								otherMatrix.columns()) / DEFAULT_LOAD_FACTOR) + 1,
						DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
		putAllForCreate(otherMatrix);
	}

	public HashSymmetricMatrix(
			Map<? extends UnorderedPair<? extends K>, ? extends V> otherMatrix) {
		this();
		this.putAll(otherMatrix);
	}

	@SuppressWarnings("unchecked")
	void initBuckets(int dimension) {
		table = new HashSymmetricMatrix.Entry[arraySize(dimension)];
		heads = new HashSymmetricMatrix.Head[dimension];
	}

	/**
	 * Initialization hook for subclasses. This method is called in all
	 * constructors and pseudo-constructors (clone, readObject) after
	 * HashSymmetricMatrix has been initialized but before any entries have been
	 * inserted. (In the absence of this method, readObject would require
	 * explicit knowledge of subclasses.)
	 */
	void init() {
	}

	private int ensureCapacity(int minCapacity) {
		// TODO Auto-generated method stub
		int capacity = 1;
		while (capacity < minCapacity)
			capacity <<= 1;
		return capacity;
	}

	static final int arraySize(int demension) {
		return demension == 0 ? 0 : (demension * (demension + 1)) >> 1;
	}

	static final int tableIndexFor(int row, int column) {
		if (row < column)
			return tableIndexFor(column, row);
		return arraySize(row) + column;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public int dimension() {
		// TODO Auto-generated method stub
		return dimension;
	}

	Head getHead(Object key) {
		int hash = (key == null ? 0 : HashMatrix.hash(key.hashCode()));
		int index = HashMatrix.indexFor(hash, heads.length);
		return getHead(key, hash, index);
	}

	Head getHead(Object key, int hash, int index) {
		Head head = heads[index];
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
	public boolean containsRowOrColumn(Object row) {
		// TODO Auto-generated method stub
		return getHead(row) != null;
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		int rowHash = (row == null ? 0 : HashMatrix.hash(row.hashCode()));
		int columnHash = (column == null ? 0 : HashMatrix.hash(column
				.hashCode()));
		return containsKey(row, rowHash, column, columnHash);
	}

	private boolean containsKey(Object row, int rowHash, Object column,
			int columnHash) {
		int rowIndex = HashMatrix.indexFor(rowHash, heads.length);
		int columnIndex = HashMatrix.indexFor(columnHash, heads.length);

		Entry entry = table[tableIndexFor(rowIndex, columnIndex)];
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
		int rowHash = (row == null ? 0 : HashMatrix.hash(row.hashCode()));
		int columnHash = (column == null ? 0 : HashMatrix.hash(column
				.hashCode()));
		return get(row, rowHash, column, columnHash);
	}

	private V get(Object row, int rowHash, Object column, int columnHash) {
		int rowIndex = HashMatrix.indexFor(rowHash, heads.length);
		int columnIndex = HashMatrix.indexFor(columnHash, heads.length);

		Entry entry = table[tableIndexFor(rowIndex, columnIndex)];
		while (entry != null) {
			if (entry.matchHash(rowHash, columnHash)
					&& entry.match(row, column))
				return entry.getValue();
			entry = entry.next;
		}
		return null;
	}

	private Head addHeadIfNotExists(K key) {
		int hash = (key == null ? 0 : HashMatrix.hash(key.hashCode()));
		int index = HashMatrix.indexFor(hash, heads.length);
		return addHeadIfNotExists(key, hash, index);
	}

	private Head addHeadIfNotExists(K key, int hash, int index) {
		Head head = heads[index];
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

		head = new Head(key, hash, heads[index]);
		heads[index] = head;
		++dimension;
		return head;
	}

	@SuppressWarnings("unchecked")
	void resize() {
		if (this.dimensionCapacity() == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		++modCount;
		int newDimension = this.dimensionCapacity() << 1;
		Head[] newHeads = new HashSymmetricMatrix.Head[newDimension];
		this.transferHeads(newHeads);

		Entry[] newTable = new HashSymmetricMatrix.Entry[arraySize(newDimension)];
		this.transferEntries(newTable);
	}

	void transferHeads(Head[] dest) {
		for (int i = 0; i < this.dimensionCapacity(); ++i) {
			Head head = heads[i];
			while (head != null) {
				Head next = head.next;
				int newIndex = HashMatrix.indexFor(head.hash, dest.length);
				head.next = dest[newIndex];
				dest[newIndex] = head;
				head = next;
			}
			heads[i] = null;
		}
	}

	void transferEntries(Entry[] dest) {
		for (int i = 0; i < table.length; ++i) {
			Entry entry = table[i];
			while (entry != null) {
				Entry next = entry.next;
				int rowIndex = HashMatrix.indexFor(entry.rowHash(),
						this.dimensionCapacity());
				int columnIndex = HashMatrix.indexFor(entry.columnHash(),
						this.dimensionCapacity());
				int newIndex = tableIndexFor(rowIndex, columnIndex);
				entry.next = dest[newIndex];
				dest[newIndex] = entry;
				entry = next;
			}
			table[i] = null;
		}
	}

	private V setValueAt(int rowIndex, int columnIndex, Head rowHead,
			Head columnHead, V value) {
		int tableIndex = tableIndexFor(rowIndex, columnIndex);
		Entry entry = table[tableIndex];
		while (entry != null) {
			if (entry.matchHash(rowHead.hash, columnHead.hash)
					&& entry.match(rowHead.getKey(), columnHead.getKey())) {
				entry.recordAccess(this);
				return entry.setValue(value);
			}
			entry = entry.next;
		}

		++modCount;
		table[tableIndex] = new Entry(value, rowHead, columnHead,
				table[tableIndex]);
		++size;
		rowHead.increaseSize(1);
		if (!table[tableIndex].isDiagonal())
			columnHead.increaseSize(1);
		return null;
	}

	@Override
	public V put(K row, K column, V value) {
		// TODO Auto-generated method stub
		int rowHash = (row == null ? 0 : HashMatrix.hash(row.hashCode()));
		int columnHash = (column == null ? 0 : HashMatrix.hash(column
				.hashCode()));
		return put(row, rowHash, column, columnHash, value);
	}

	private V put(K row, int rowHash, K column, int columnHash, V value) {
		int rowIndex = HashMatrix.indexFor(rowHash, heads.length);
		Head rowHead = this.addHeadIfNotExists(row, rowHash, rowIndex);
		int columnIndex = HashMatrix.indexFor(columnHash, heads.length);
		Head columnHead = this.addHeadIfNotExists(column, columnHash,
				columnIndex);

		if (this.dimension() >= threshold) {
			resize();
		}

		return setValueAt(rowIndex, columnIndex, rowHead, columnHead, value);
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		int rowHash = (row == null ? 0 : HashMatrix.hash(row.hashCode()));
		int rowIndex = HashMatrix.indexFor(rowHash, heads.length);

		int columnHash = (column == null ? 0 : HashMatrix.hash(column
				.hashCode()));
		int columnIndex = HashMatrix.indexFor(columnHash, heads.length);

		int tableIndex = tableIndexFor(rowIndex, columnIndex);
		Entry entry = table[tableIndex], prev = null;
		while (entry != null) {
			if (entry.matchHash(rowHash, columnHash)
					&& entry.match(row, column)) {
				++modCount;
				// remove entry from the
				if (null == prev)
					table[tableIndex] = entry.next;
				else
					prev.next = entry.next;
				--size;

				if (entry.rowHead.increaseSize(-1) == 0)
					removeHead(rowIndex, entry.rowHead);
				if (!entry.isDiagonal()
						&& entry.columnHead.increaseSize(-1) == 0)
					removeHead(columnIndex, entry.columnHead);

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

	private void removeHead(int index, Head head) {
		// TODO Auto-generated method stub
		Head h = heads[index], prev = null;
		while (h != null) {
			if (head == h) {
				removeHead(index, head, prev);
				return;
			}
			prev = h;
			h = h.next;
		}
	}

	private void removeHead(int index, Head head, Head prev) {
		// TODO Auto-generated method stub
		++modCount;
		if (prev == null)
			heads[index] = head.next;
		else
			prev.next = head.next;
		--dimension;
		head.dispose();
	}
	
	@Override
	public void removeRowAndColumn(K key) {
		int hash = (key == null ? 0 : HashMatrix.hash(key.hashCode()));
		removeKey(key, hash);
	}

	private void removeKey(K key, int hash) {
		// TODO Auto-generated method stub
		int rowIndex = HashMatrix.indexFor(hash, heads.length);

		// find the rowHead and its prev
		Head head = heads[rowIndex], prev = null, rowHead = null;
		if (key == null) {
			while (head != null) {
				if (head.hash == hash && head.getKey() == null) {
					rowHead = head;
					break;
				}
				prev = head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (head.hash == hash && key.equals(head.getKey())) {
					rowHead = head;
					break;
				}
				prev = head;
				head = head.next;
			}
		}
		if (rowHead == null)
			return ;

		if (rowHead.size > 0) {
			// remove all entry of this row
			for (int j = 0; j < this.dimensionCapacity(); ++j) {
				int tableIndex = tableIndexFor(rowIndex, j);
				Entry entry = table[tableIndex], prevEntry = null;
				while (entry != null) {
					Entry next = entry.next;
					if (entry.rowHead == rowHead) {
						if (prevEntry == null)
							table[tableIndex] = next;
						else
							prevEntry.next = next;
						--size;
						if (!entry.isDiagonal()
								&& entry.columnHead.increaseSize(-1) == 0)
							removeHead(j, entry.columnHead);

						entry.recordRemoval(this);
						entry.dispose();
					} else if (entry.columnHead == rowHead) {
						if (prevEntry == null)
							table[tableIndex] = next;
						else
							prevEntry.next = next;
						--size;
						if (entry.rowHead.increaseSize(-1) == 0)
							removeHead(j, entry.rowHead);
					} else {
						prevEntry = entry;
					}
					entry = next;
				}
			}
		}

		removeHead(rowIndex, rowHead, prev);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		++modCount;
		for (int i = 0; i < this.table.length; ++i) {
			Entry entry = table[i];
			while (entry != null) {
				Entry next = entry.next;
				entry.recordRemoval(this);
				entry.dispose();
				entry = next;
			}
			table[i] = null;
		}

		for (int j = 0; j < this.heads.length; ++j) {
			Head head = heads[j];
			while (head != null) {
				Head next = head.next;
				head.dispose();
				head = next;
			}
			heads[j] = null;
		}
		this.dimension = this.size = 0;
	}

	@Override
	void clearViews() {
		// TODO Auto-generated method stub
		super.clearViews();
		this.entrySet = null;
		this.keySet = null;
	}

	private void putAllForCreate(
			Matrix<? extends K, ? extends K, ? extends V> otherMatrix) {
		// TODO Auto-generated method stub
		for (Iterator<? extends Matrix.Entry<? extends K, ? extends K, ? extends V>> i = otherMatrix
				.entrySet().iterator(); i.hasNext();) {
			Matrix.Entry<? extends K, ? extends K, ? extends V> e = i.next();
			putForCreate(e.getRowKey(), e.getColumnKey(), e.getValue());
		}
	}

	/**
	 * This method is used instead of put by constructors and pseudoconstructors
	 * (clone, readObject). It does not resize the table, check for
	 * comodification, etc.
	 */
	private void putForCreate(K rowKey, K columnKey, V value) {
		// TODO Auto-generated method stub
		int rowHash = (rowKey == null) ? 0 : HashMatrix.hash(rowKey.hashCode());
		int rowIndex = HashMatrix.indexFor(rowHash, this.dimensionCapacity());
		Head rowHead = addHeadIfNotExists(rowKey, rowHash, rowIndex);

		int columnHash = (columnKey == null) ? 0 : HashMatrix.hash(columnKey
				.hashCode());
		int columnIndex = HashMatrix.indexFor(columnHash,
				this.dimensionCapacity());
		Head columnHead = addHeadIfNotExists(columnKey, columnHash, columnIndex);

		int tableIndex = tableIndexFor(rowIndex, columnIndex);
		table[tableIndex] = new Entry(value, rowHead, columnHead,
				table[tableIndex]);
		++size;
	}

	@Override
	public HashSymmetricMatrix<K, V> clone() {
		// TODO Auto-generated method stub
		HashSymmetricMatrix<K, V> clone = null;
		try {
			clone = (HashSymmetricMatrix<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			// assert false
			e.printStackTrace();
		}

		clone.modCount = 0;
		clone.dimension = clone.size = 0;
		clone.initBuckets(this.dimensionCapacity());
		clone.init();
		clone.putAllForCreate(this);

		return clone;
	}

	final class KeyMapView extends AbstractKeyMapView {
		transient final int keyHash;
		transient volatile Head head;

		private KeyMapView(K key, int keyHash, Head head) {
			super(key);
			this.keyHash = keyHash;
			this.head = head;
		}

		private final int modCount() {
			return headNotExists() ? 0 : head.modCount;
		}

		final boolean headNotExists() {
			if (head != null && head.disposed())
				head = null;
			return head == null;
		}

		Head validateHead() {
			if (headNotExists()) {
				head = HashSymmetricMatrix.this.getHead(viewKey);
				if (head != null && head.viewMap == null)
					head.viewMap = this;
			}
			return head;
		}

		@Override
		public V put(K key, V value) {
			// TODO Auto-generated method stub
			if (validateHead() == null) {
				head = HashSymmetricMatrix.this.addHeadIfNotExists(viewKey);
				head.viewMap = this;
			}
			int hash = key == null ? 0 : HashMatrix.hash(key.hashCode());
			return HashSymmetricMatrix.this.put(viewKey, keyHash, key, hash,
					value);
		}

		@Override
		public final int size() {
			// TODO Auto-generated method stub
			return validateHead() == null ? 0 : head.size;
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return false;
			int hash = key == null ? 0 : HashMatrix.hash(key.hashCode());
			return HashSymmetricMatrix.this.containsKey(viewKey, keyHash, key,
					hash);
		}

		@Override
		public V get(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return null;
			int hash = key == null ? 0 : HashMatrix.hash(key.hashCode());
			return HashSymmetricMatrix.this.get(viewKey, keyHash, key, hash);
		}

		@Override
		public V remove(Object key) {
			// TODO Auto-generated method stub
			if (validateHead() == null)
				return null;
			V oldValue = super.remove(key);
			if (head.disposed())
				head = null;
			return oldValue;
		}

		@Override
		public final void clear() {
			// TODO Auto-generated method stub
			if (validateHead() != null) {
				HashSymmetricMatrix.this.removeKey(viewKey, keyHash);
				head = null;
			}
		}
		
		@Override
		public final Set<Map.Entry<K, V>> entrySet() {
			// TODO Auto-generated method stub
			validateHead();
			return super.entrySet();
		}

		@Override
		final Iterator<Map.Entry<K, V>> entryIterator() {
			// TODO Auto-generated method stub
			validateHead();
			return new EntryIterator();
		}

		private final class EntryIterator implements Iterator<Map.Entry<K, V>> {
			private HashSymmetricMatrix<K, V>.Entry current = null, next;
			private final int rowIndex = headNotExists() ? -1 : HashMatrix
					.indexFor(head.hash, heads.length);
			private volatile int columnIndex = 0;

			private int expectedModCount;

			private EntryIterator() {
				expectedModCount = KeyMapView.this.modCount();
				getNext();
			}

			final void checkModCount() {
				if (KeyMapView.this.modCount() != expectedModCount)
					throw new ConcurrentModificationException();
			}

			final boolean match(HashSymmetricMatrix<K, V>.Entry entry) {
				if (entry == null)
					return false;
				else
					return head == entry.rowHead || head == entry.columnHead;
			}

			private void getNext() {
				if (KeyMapView.this.headNotExists() || rowIndex < 0) {
					next = null;
				} else {
					while (columnIndex < heads.length) {
						int tableIndex = tableIndexFor(rowIndex, columnIndex);
						if (match(table[tableIndex])) {
							next = table[tableIndex];
							++columnIndex;
							break;
						}
						++columnIndex;
					}
				}
			}

			@Override
			public final boolean hasNext() {
				// TODO Auto-generated method stub
				return next != null;
			}

			@Override
			public Map.Entry<K, V> next() {
				// TODO Auto-generated method stub
				checkModCount();
				if (null == next)
					throw new NoSuchElementException();
				current = next;
				while ((next = next.next) != null) {
					if (eq(viewKey, next.getRowKey()))
						break;
				}
				if (next == null) {
					getNext();
				}
				return head == current.rowHead ? current.rowMapEntry()
						: current.columnMapEntry();
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				if (null == current)
					throw new IllegalStateException();
				checkModCount();
				K column = head == current.rowHead ? current.getColumnKey()
						: current.getRowKey();
				current = null;
				HashSymmetricMatrix.this.remove(viewKey, column);
				if (head.disposed()) {
					head = null;
					next = null;
				}
				expectedModCount = KeyMapView.this.modCount();
			}

		}
	}

	@Override
	public Map<K, V> keyMap(K key) {
		// TODO Auto-generated method stub
		Head head = getHead(key);
		if (head == null) {
			return new KeyMapView(key, key == null ? 0 : HashMatrix.hash(key
					.hashCode()), null);
		}
		if (head.viewMap == null) {
			head.viewMap = new KeyMapView(key, head.hash, head);
		}
		return head.viewMap;
	}

	@Override
	protected int valueCount(Object key) {
		// TODO Auto-generated method stub
		Head head = getHead(key);
		return null == head ? 0 : head.size;
	}

	// View
	protected transient volatile Set<K> keySet = null;
	protected transient volatile Set<Matrix.Entry<K, K, V>> entrySet = null;

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		if (keySet == null) {
			keySet = new AbstractSet<K>() {

				@Override
				public final Iterator<K> iterator() {
					// TODO Auto-generated method stub
					return new KeyIterator();
				}

				@Override
				public final int size() {
					// TODO Auto-generated method stub
					return HashSymmetricMatrix.this.dimension();
				}

			};
		}
		return keySet;
	}

	@Override
	public Set<Matrix.Entry<K, K, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {
			entrySet = new AbstractSet<Matrix.Entry<K, K, V>>() {

				@Override
				public final Iterator<Matrix.Entry<K, K, V>> iterator() {
					// TODO Auto-generated method stub
					return new EntryIterator();
				}

				@Override
				public final int size() {
					// TODO Auto-generated method stub
					return HashSymmetricMatrix.this.size;
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

		final void checkModCount() {
			if (HashSymmetricMatrix.this.modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}

		final void resetModCount() {
			expectedModCount = HashSymmetricMatrix.this.modCount;
		}
	}

	private final class KeyIterator extends FastFailedIterator<K> {
		private Head current = null, next = null;
		private int index = 0;

		private KeyIterator() {
			super();
			while (index < heads.length && (next = heads[index++]) == null)
				;
		}

		@Override
		public final boolean hasNext() {
			// TODO Auto-generated method stub
			return null != next;
		}

		@Override
		public K next() {
			// TODO Auto-generated method stub
			checkModCount();
			if (null == next)
				throw new NoSuchElementException();
			current = next;
			if ((next = next.next) == null) {
				while (index < heads.length && (next = heads[index++]) == null)
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
			K key = current.getKey();
			current = null;
			HashSymmetricMatrix.this.removeRow(key);
			resetModCount();
		}

	}

	private final class EntryIterator extends
			FastFailedIterator<Matrix.Entry<K, K, V>> {
		private Entry current = null, next;
		private int index = 0;

		private EntryIterator() {
			super();
			getNext();
		}

		private void getNext() {
			for (; index < HashSymmetricMatrix.this.table.length
					&& (next = table[index++]) == null;)
				;
		}

		@Override
		public final boolean hasNext() {
			// TODO Auto-generated method stub
			return next != null;
		}

		@Override
		public Matrix.Entry<K, K, V> next() {
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
			K row = current.getRowKey();
			K column = current.getColumnKey();
			current = null;
			HashSymmetricMatrix.this.remove(row, column);
			resetModCount();
		}

	}

	class Entry extends AbstractSymmetricMatrixEntry<K, V> {
		V value;
		transient Head rowHead, columnHead;
		transient Entry next;

		Entry(V value, Head rowHead, Head columnHead, Entry next) {
			this.value = value;
			this.rowHead = rowHead;
			this.columnHead = columnHead;
			this.next = next;
		}

		@Override
		public K getRowKey() {
			// TODO Auto-generated method stub
			return rowHead.getKey();
		}

		@Override
		public K getColumnKey() {
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
			return rowHead.hash;
		}

		int columnHash() {
			return columnHead.hash;
		}

		boolean matchHash(int rowHash, int columnHash) {
			return (this.rowHash() == rowHash && this.columnHash() == columnHash)
					|| (this.rowHash() == columnHash && this.columnHash() == rowHash);
		}

		boolean isDiagonal() {
			return rowHead == columnHead;
		}

		@Override
		void dispose() {
			super.dispose();
			rowHead = null;
			columnHead = null;
			next = null;
			value = null;
		}

		/**
		 * This method is invoked whenever the value in an entry is overwritten
		 * by an invocation of put(k,v) for a key k that's already in the
		 * HashSymmetricMatrix.
		 */
		void recordAccess(HashSymmetricMatrix<K, V> m) {
		}

		/**
		 * This method is invoked whenever the entry is removed from the table.
		 */
		void recordRemoval(HashSymmetricMatrix<K, V> m) {
		}

	}

	private final class Head {
		K key;
		final int hash;
		int size;
		transient Head next;
		transient volatile int modCount;

		transient volatile KeyMapView viewMap = null;

		Head(K key, int hash, Head next) {
			this.key = key;
			this.hash = hash;
			this.size = 0;
			this.next = next;
		}

		final K getKey() {
			return key;
		}

		final int increaseSize(int incr) {
			size = Math.max(0, size + incr);
			return size;
		}

		final void dispose() {
			size = -1;
			next = null;
			if (viewMap != null) {
				viewMap.head = null;
				viewMap = null;
			}
		}

		final boolean disposed() {
			return size < 0;
		}
	}

	/**
	 * Save the state of the <tt>HashMatrix</tt> instance to a stream (i.e.,
	 * serialize it).
	 * 
	 * @serialData The <i>capacity</i> of the HashSymmetricMatrix (the row and
	 *             column of the bucket array) is emitted (int), followed by the
	 *             <i>size</i> (an int, the number of keypair-value mappings),
	 *             followed by the rowKey (Object), columnKey (Object) and value
	 *             (Object) for each keypair-value mapping. The keypair-value
	 *             mappings are emitted in no particular order.
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		Iterator<Matrix.Entry<K, K, V>> i = isEmpty() ? null : entrySet()
				.iterator();

		// Write out the threshold, loadfactor, and any hidden stuff
		s.defaultWriteObject();

		// Write out number of buckets
		s.writeInt(this.dimensionCapacity());

		// Write out size (number of Mappings)
		s.writeInt(size);

		// Write out keys and values (alternating)
		if (i != null) {
			while (i.hasNext()) {
				Matrix.Entry<K, K, V> e = i.next();
				s.writeObject(e.getRowKey());
				s.writeObject(e.getColumnKey());
				s.writeObject(e.getValue());
			}
		}
	}

	/**
	 * Reconstitute the <tt>HashSymmetricMatrix</tt> instance from a stream
	 * (i.e., deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		// Read in the threshold, loadfactor, and any hidden stuff
		s.defaultReadObject();

		// Read in number of buckets and allocate the bucket array;
		int dimensionCapacity = s.readInt();

		initBuckets(dimensionCapacity);

		init(); // Give subclass a chance to do its thing.

		// Read in size (number of Mappings)
		int size = s.readInt();

		// Read the keys and values, and put the mappings in the HashMap
		for (int i = 0; i < size; i++) {
			K row = (K) s.readObject();
			K column = (K) s.readObject();
			V value = (V) s.readObject();
			putForCreate(row, column, value);
		}
	}

	int dimensionCapacity() {
		return heads.length;
	}

	float loadFactor() {
		return loadFactor;
	}

}
