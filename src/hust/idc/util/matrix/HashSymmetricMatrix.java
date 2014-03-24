package hust.idc.util.matrix;

import hust.idc.util.pair.Pair;

import java.io.IOException;
import java.util.Map;
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
	
	public HashSymmetricMatrix(){
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public HashSymmetricMatrix(int initialDimensions){
		this(initialDimensions, DEFAULT_LOAD_FACTOR);
	}
	
	public HashSymmetricMatrix(int initialDimensions, float loadFactor){
		super();
		if (initialDimensions < 0)
			throw new IllegalArgumentException("Illegal Dimension: " + initialDimensions);

		int dimensionCapacity = ensureCapacity(Math
				.min(MAXIMUM_CAPACITY, initialDimensions));
		this.loadFactor = loadFactor;
		threshold = (int) (dimensionCapacity * loadFactor);
		this.dimension = this.size = 0;
		initBuckets(dimensionCapacity);
		init();
	}

	public HashSymmetricMatrix(
			Matrix<? extends K, ? extends K, ? extends V> otherMatrix) {
		this(Math.max((int) (Math.max(otherMatrix.rows(), otherMatrix.columns()) / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
		putAllForCreate(otherMatrix);
	}

	public HashSymmetricMatrix(
			Map<? extends Pair<? extends K, ? extends K>, ? extends V> otherMatrix) {
		this();
		this.putAll(otherMatrix);
	}
	
	@SuppressWarnings("unchecked")
	void initBuckets(int dimension){
		table = new HashSymmetricMatrix.Entry[arraySize(dimension)];
		heads = new HashSymmetricMatrix.Head[dimension];
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
	
	private int ensureCapacity(int minCapacity) {
		// TODO Auto-generated method stub
		int capacity = 1;
		while (capacity < minCapacity)
			capacity <<= 1;
		return capacity;
	}
	
	private static int arraySize(int demension) {
		return demension == 0 ? 0 : (demension * (demension + 1)) >> 1;
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

	private void putAllForCreate(
			Matrix<? extends K, ? extends K, ? extends V> otherMatrix) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
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

		clone.clearViews();
		clone.modCount = 0;
		clone.dimension = clone.size = 0;
		clone.initBuckets(this.dimensionCapacity());
		clone.init();
		clone.putAllForCreate(this);

		return clone;
	}

	// View
	protected transient volatile Set<K> keySet = null;
	protected transient volatile Set<Matrix.Entry<K, K, V>> entrySet = null;

	@Override
	public Set<hust.idc.util.matrix.Matrix.Entry<K, K, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {

		}
		return entrySet;
	}

	private class Entry extends AbstractEntry<K, K, V> {
		V value;
		transient Head rowHead, columnHead;
		transient Entry next;

		Entry(V value, Entry next) {
			this.value = value;
			this.next = next;
		}

		@Override
		public K getRowKey() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public K getColumnKey() {
			// TODO Auto-generated method stub
			return null;
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

	private class Head {
		K key;
		final int hash;
		int size;
		transient Head next;

		transient volatile Map<?, V> viewMap = null;

		Head(K key, int hash, Head next) {
			this.key = key;
			this.hash = hash;
			this.size = 0;
			this.next = next;
		}

		K getKey() {
			return key;
		}

		int increaseSize(int incr) {
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
	 * @serialData The <i>capacity</i> of the HashSymmetricMatrix (the row and
	 *             column of the bucket array) is emitted (int), followed by the
	 *             <i>size</i> (an int, the number of keypair-value mappings),
	 *             followed by the rowKey (Object), columnKey (Object) and value
	 *             (Object) for each keypair-value mapping. The keypair-value
	 *             mappings are emitted in no particular order.
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {

	}

	/**
	 * Reconstitute the <tt>HashSymmetricMatrix</tt> instance from a stream
	 * (i.e., deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws IOException,
			ClassNotFoundException {

	}
	
	int dimensionCapacity() {
		return heads.length;
	}

	float loadFactor() {
		return loadFactor;
	}

}
