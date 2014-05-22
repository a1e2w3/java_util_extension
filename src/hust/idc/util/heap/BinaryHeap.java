package hust.idc.util.heap;

import hust.idc.util.Sortable;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A Complete-Binary-Tree Based implementation of the <tt>Heap</tt> interface,
 * Implements almost all optional heap operations, except remove an element
 * through iterator.remove(). And it permits all elements except <tt>null</tt>,
 * since <tt>null</tt> is used as a special return value by the <tt>poll</tt>
 * method to indicate that the heap contains no elements.
 * <p>
 * 
 * Binary Heap hold all element in a specific order by an array list. Each
 * element in the array list was seemed as a node in heap, the index of root
 * node was 0. For a node which index is i, its left child would be held at
 * index (2 * i) + 1 and right child would be(2 * i) + 2, if they existed.
 * <p>
 * 
 * In addition to implementing the <tt>Heap</tt> interface, this class provides
 * methods to manipulate the size of the array list that is used internally to
 * store the element list.
 * <p>
 * 
 * The <tt>size</tt>, <tt>isEmpty</tt>, <tt>peek</tt>, and <tt>iterator</tt>
 * operations run in constant time. The <tt>offer</tt>, <tt>add</tt>,
 * <tt>poll</tt>, <tt>remove</tt> operation runs in <i>logarithmic time</i>,
 * that is, add(or poll, remove) an elements in the heap contains n elements
 * requires O(log n) time. similarly, the <tt>contains</tt> operation runs in
 * <i>logarithmic time</i>, too.
 * <p>
 * 
 * <p>
 * The iterators returned by this class's <tt>iterator</tt> method are not
 * support <tt>iterator.remove</tt> operation, because the order of elements in
 * the heap may be changed once any element is removed, the iterator cannot
 * ensure the iterative sequence. And the iterators are <i>fail-fast</i>: if the
 * list is structurally modified at any time after the iterator is created the
 * iterator will throw a {@link ConcurrentModificationException}.
 * 
 * @author WangCong
 * @version 1.2.0, 08/10/13
 * @see Collection
 * @see Queue
 * @see Sortable
 * @see Heap
 * @see AbstractHeap
 * @since 1.0
 * @param <E>
 *            the element type
 */
public class BinaryHeap<E> extends AbstractHeap<E> implements Heap<E>,
		Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7581128060475967485L;
	transient BinaryHeapEntry[] elements;
	int size;
	transient volatile int modCount = 0;

	private static final int DEFAULT_INITIAL_CAPACITY = 10;

	public BinaryHeap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BinaryHeap(Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	public BinaryHeap(Collection<? extends E> elements) {
		super();
		// TODO Auto-generated constructor stub
		if (elements != null && !elements.isEmpty()) {
			@SuppressWarnings("unchecked")
			E[] array = (E[]) elements.toArray();
			this.elements = this.initArray(array.length);
			for (int i = 0; i < this.elements.length; ++i)
				this.elements[i] = new BinaryHeapEntry(array[i], i);
			this.buildHeap();
		}
	}

	public BinaryHeap(Collection<? extends E> elements,
			Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
		if (elements != null && !elements.isEmpty()) {
			@SuppressWarnings("unchecked")
			E[] array = (E[]) elements.toArray();
			this.elements = this.initArray(array.length);
			for (int i = 0; i < this.elements.length; ++i)
				this.elements[i] = new BinaryHeapEntry(array[i], i);
			this.buildHeap();
		}
	}

	public BinaryHeap(Heap<E> heap) {
		super(heap == null ? null : heap.getComparator());
		if (heap != null && !heap.isEmpty()) {
			@SuppressWarnings("unchecked")
			E[] array = (E[]) heap.toArray();
			this.elements = this.initArray(array.length);
			for (int i = 0; i < this.elements.length; ++i)
				this.elements[i] = new BinaryHeapEntry(array[i], i);
			this.buildHeap();
		}
	}

	public BinaryHeap(int initialCapacity) {
		this(initialCapacity, null);
	}

	public BinaryHeap(int initialCapacity, Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		this.elements = this.initArray(initialCapacity);
	}

	@SuppressWarnings("unchecked")
	private BinaryHeapEntry[] initArray(int capacity) {
		return new BinaryHeap.BinaryHeapEntry[capacity];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayLisy#ensureCapacity()
	 */
	public void ensureCapacity(int minCapacity) {
		if (minCapacity > 0)
			this.ensureCapacityInternal(minCapacity);

	}

	private void ensureCapacityInternal(int minCapacity) {
		++modCount;
		if (this.elements == null) {
			this.elements = this.initArray(minCapacity);
		} else if (minCapacity - this.elements.length > 0) {
			this.grow(minCapacity);
		}
	}

	/**
	 * The maximum size of array to allocate. Some VMs reserve some header words
	 * in an array. Attempts to allocate larger arrays may result in
	 * OutOfMemoryError: Requested array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	private void grow(int minCapacity) {
		// overflow-conscious code
		int oldCapacity = elements == null ? 1 : elements.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;
		if (newCapacity - MAX_ARRAY_SIZE > 0)
			newCapacity = hugeCapacity(minCapacity);
		// minCapacity is usually close to size, so this is a win:
		if (elements != null)
			elements = Arrays.copyOf(elements, newCapacity);
	}

	private static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE
				: MAX_ARRAY_SIZE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayLisy#trimToSize()
	 */
	public void trimToSize() {
		if (this.elements != null) {
			modCount++;
			int oldCapacity = elements.length;
			if (size < oldCapacity) {
				elements = Arrays.copyOf(elements, size);
			}
		}
	}

	private boolean buildHeap() {
		if (this.size <= 1)
			return false;
		boolean modified = false;
		for (int i = (this.size << 1); i > 0; --i) {
			modified |= this.elements[i].heaplifyDown();
		}
		return modified;
	}

	protected transient volatile Collection<HeapEntry<E>> entrys = null;
	protected transient volatile Collection<HeapEntry<E>> roots = null;

	@Override
	Collection<HeapEntry<E>> entrys() {
		// TODO Auto-generated method stub
		if (entrys == null) {
			entrys = new AbstractCollection<HeapEntry<E>>() {

				@Override
				public Iterator<HeapEntry<E>> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<HeapEntry<E>>() {
						private int expectedModCount = BinaryHeap.this.modCount;
						private int cur = -1;

						private void checkForComodification() {
							if (expectedModCount != BinaryHeap.this.modCount)
								throw new ConcurrentModificationException();
						}

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return cur + 1 < BinaryHeap.this.size;
						}

						@Override
						public HeapEntry<E> next() {
							// TODO Auto-generated method stub
							checkForComodification();
							if (++cur >= BinaryHeap.this.size)
								throw new NoSuchElementException();
							return BinaryHeap.this.elements[cur];
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							throw new UnsupportedOperationException(
									"unsupported operation: iterator.remove");
						}
					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return BinaryHeap.this.size;
				}
			};
		}
		return entrys;
	}

	@Override
	Collection<Heap.HeapEntry<E>> roots() {
		// TODO Auto-generated method stub
		if (roots == null) {
			roots = new AbstractCollection<HeapEntry<E>>() {

				@Override
				public Iterator<HeapEntry<E>> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<HeapEntry<E>>() {
						private int expectedModCount = BinaryHeap.this.modCount;
						private boolean hasNext = (BinaryHeap.this.size > 0);

						private void checkForComodification() {
							if (expectedModCount != BinaryHeap.this.modCount)
								throw new ConcurrentModificationException();
						}

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return hasNext;
						}

						@Override
						public HeapEntry<E> next() {
							// TODO Auto-generated method stub
							checkForComodification();
							if (hasNext) {
								hasNext = false;
								return BinaryHeap.this.elements[0];
							} else {
								throw new NoSuchElementException();
							}
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							throw new UnsupportedOperationException(
									"unsupported operation: iterator.remove");
						}
					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return BinaryHeap.this.size == 0 ? 0 : 1;
				}
			};
		}
		return roots;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if (this.elements != null && this.size > 0) {
			++modCount;
			for (int i = 0; i < size; ++i) {
				this.elements[i] = null;
			}
			size = 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BinaryHeap<E> clone() {
		// TODO Auto-generated method stub
		BinaryHeap<E> clone = null;
		try {
			clone = (BinaryHeap<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalError();
		}

		clone.elements = clone.initArray(this.elements.length);
		for(int i = 0; i < this.size; ++i){
			clone.elements[i] = clone.new BinaryHeapEntry(this.elements[i].element(), i);
		}
		clone.modCount = 0;
		clone.entrys = null;
		clone.roots = null;
		return clone;
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		if (e == null)
			return false;
		if (this.elements == null) {
			this.ensureCapacityInternal(DEFAULT_INITIAL_CAPACITY); // Increments modCount!!
		} else {
			this.ensureCapacityInternal(size + 1); // Increments modCount!!
		}
		this.elements[size++] = new BinaryHeapEntry(e, size - 1);
		if (size > 1) {
			this.elements[size - 1].heaplifyUp();
		}
		return true;
	}

	public boolean addAll(Collection<? extends E> c) {
		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacityInternal(size + numNew); // Increments modCount
		System.arraycopy(a, 0, elements, size, numNew);
		size += numNew;
		return numNew != 0;
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		if (size <= 0)
			return null;
		E element = this.peek();
		this.removeEntry(0); // Increments modCount
		return element;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		if (this.isEmpty())
			return false;
		try {
			@SuppressWarnings("unchecked")
			BinaryHeapEntry entry = (BinaryHeapEntry) this.getEntry((E) o);
			if (entry == null)
				return false;
			this.removeEntry(entry.index); // Increments modCount
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void removeEntry(int index) {
		assert index >= 0 && index < this.size : "parameter is illegal in method removeEntry";

		++modCount;
		if(index == size - 1){
			this.elements[size - 1] = null;
			--size;
			return ;
		}else {
			E elem = this.elements[index].element();
			this.elements[index].set(this.elements[size - 1].element());
			this.elements[size - 1] = null;
			--size;

			int comp = this.compare(elem, this.elements[index].element());
			if (comp > 0) {
				this.elements[index].heaplifyDown();
			} else if (comp < 0) {
				this.elements[index].heaplifyUp();
			}
		}
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		if (this.isEmpty())
			return null;
		else
			return this.elements[0].element();
	}

	private class BinaryHeapEntry extends AbstractBinaryHeapEntry implements
			HeapEntry<E> {
		private E element;
		private transient final int index;

		private BinaryHeapEntry(E element, int index) {
			super();
			this.element = element;
			this.index = index;
		}

		@Override
		public boolean set(E element) {
			// TODO Auto-generated method stub
			if (element == null)
				return false;
			this.element = element;
			return true;
		}

		@Override
		public E element() {
			// TODO Auto-generated method stub
			return this.element;
		}

		@Override
		public BinaryHeapEntry parent() {
			// TODO Auto-generated method stub
			if (index > 0)
				return BinaryHeap.this.elements[(index - 1) >> 1];
			else
				return null;
		}

		@Override
		public BinaryHeapEntry leftSibling() {
			// TODO Auto-generated method stub
			if (index <= 1)
				return null;
			else if ((index & 0x01) == 0)
				return BinaryHeap.this.elements[index - 1];
			else
				return null;
		}

		@Override
		public BinaryHeapEntry rightSibling() {
			// TODO Auto-generated method stub
			if (index <= 0)
				return null;
			else if ((index & 0x01) != 0) {
				return index + 1 < BinaryHeap.this.elements.length ? BinaryHeap.this.elements[index + 1]
						: null;
			} else
				return null;
		}

		@Override
		public int degree() {
			// TODO Auto-generated method stub
			BinaryHeapEntry child = this.leftChild();
			if (child == null)
				return 0;
			else
				return child.rightSibling() == null ? 1 : 2;
		}

		@Override
		BinaryHeapEntry leftChild() {
			// TODO Auto-generated method stub
			if (index >= 0) {
				int childIndex = ((index + 1) << 1) - 1;
				return childIndex < BinaryHeap.this.elements.length ? BinaryHeap.this.elements[childIndex]
						: null;
			}
			return null;
		}

		@Override
		BinaryHeapEntry rightChild() {
			// TODO Auto-generated method stub
			if (index >= 0) {
				int childIndex = (index + 1) << 1;
				return childIndex < BinaryHeap.this.elements.length ? BinaryHeap.this.elements[childIndex]
						: null;
			}
			return null;
		}
		
		@Override
		public String toString(){
			return "index = " + index + ", value = " + element();
		}

	}

	/**
	 * Save the state of the <tt>BinaryHeap</tt> instance to a stream (that is,
	 * serialize it).
	 * 
	 * @serialData The length of the array backing the <tt>BinaryHeap</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();

		// Write out array length
		s.writeInt(elements.length);

		// Write out all elements in the proper order.
		for (int i = 0; i < size; i++)
			s.writeObject(elements[i].element());

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Reconstitute the <tt>BinaryHeap</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();
		// Read in array length and allocate array
		int arrayLength = s.readInt();
		elements = new BinaryHeap.BinaryHeapEntry[arrayLength];

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++) {
			E element = (E) s.readObject();
			elements[i] = new BinaryHeapEntry(element, i);
		}
	}
}
