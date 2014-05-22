package hust.idc.util.heap;

import hust.idc.util.Mergeable;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SkewHeap<E> extends AbstractHeap<E> implements Heap<E>,
		Mergeable<SkewHeap<E>>, Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1780109760968396206L;
	transient SkewHeapEntry root;
	int size;
	transient volatile int modCount = 0;

	public SkewHeap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SkewHeap(Collection<? extends E> elements,
			Comparator<? super E> comparator) {
		super(elements, comparator);
		// TODO Auto-generated constructor stub
	}

	public SkewHeap(Collection<? extends E> elements) {
		super(elements);
		// TODO Auto-generated constructor stub
	}

	public SkewHeap(Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean union(SkewHeap<E> otherHeap) {
		// TODO Auto-generated method stub
		if (otherHeap == null || otherHeap.isEmpty())
			return false;
		++modCount;
		this.root = this.unionInternal(this.root, otherHeap.root);
		this.size += otherHeap.size;
		return true;
	}

	private SkewHeapEntry unionInternal(SkewHeapEntry root1, SkewHeapEntry root2) {
		if (root1 == null)
			return root2;
		if (root2 == null)
			return root1;
		if (this.compare(root1, root2) < 0)
			return unionInternal(root2, root1);

		root2.parent = root1;
		root1.rightChild = unionInternal(root1.rightChild, root2);
		root1.swapChild();
		return root1;
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
					return new SkewHeapEntryIterator();
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return SkewHeap.this.size;
				}

			};
		}
		return entrys;
	}

	@Override
	Collection<HeapEntry<E>> roots() {
		// TODO Auto-generated method stub
		if (roots == null) {
			roots = new AbstractCollection<HeapEntry<E>>() {

				@Override
				public Iterator<HeapEntry<E>> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<HeapEntry<E>>() {
						private boolean hasNext = !SkewHeap.this.isEmpty();
						private int expectedModCount = SkewHeap.this.modCount;

						private void checkForComodification() {
							if (expectedModCount != SkewHeap.this.modCount)
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
							if (!hasNext)
								throw new NoSuchElementException();
							hasNext = false;
							return SkewHeap.this.root;
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
					return SkewHeap.this.root == null ? 0 : 1;
				}

			};
		}
		return roots;
	}
	
	private class SkewHeapEntryIterator extends HeapPreorderIterator {
		private int expectedModCount = SkewHeap.this.modCount;
		
		private SkewHeapEntryIterator(){
			super();
		}

		private void checkForComodification() {
			if (expectedModCount != SkewHeap.this.modCount)
				throw new ConcurrentModificationException();
		}

		@Override
		boolean removeCurrent() {
			// TODO Auto-generated method stub
			checkForComodification();
			next = SkewHeap.this.removeEntry((SkewHeapEntry) current);
			expectedModCount = SkewHeap.this.modCount;
			return true;
		}

		@Override
		HeapEntry<E> getNext() {
			// TODO Auto-generated method stub
			checkForComodification();
			return super.getNext();
		}
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		if (e == null)
			return false;
		++modCount;
		this.root = this.unionInternal(this.root, new SkewHeapEntry(e));
		++size;
		return true;
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		if (this.root == null)
			return null;
		E element = this.root.element();
		this.removeEntry(this.root);
		return element;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return this.root == null ? null : this.root.element();
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		if (this.isEmpty())
			return false;
		try {
			@SuppressWarnings("unchecked")
			SkewHeapEntry entry = (SkewHeapEntry) this.getEntry((E) o);
			if (entry == null)
				return false;
			this.removeEntry(entry);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private SkewHeapEntry removeEntry(SkewHeapEntry entry) {
		assert entry != null : "parameter is null in method removeEntry";
		++modCount;
		if (entry.leftChild != null) {
			entry.leftChild.parent = null;
		}
		if (entry.rightChild != null) {
			entry.rightChild.parent = null;
		}

		SkewHeapEntry newRoot = this.unionInternal(entry.rightChild,
				entry.leftChild);
		entry.leftChild = entry.rightChild = null;
		if (entry.parent == null) {
			this.root = newRoot;
		} else {
			if (entry.parent.leftChild == entry)
				entry.parent.leftChild = newRoot;
			else
				entry.parent.rightChild = newRoot;
			entry.parent = null;
		}
		--size;
		return newRoot;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.root == null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		this.clear(this.root);
		size = 0;
	}

	private void clear(SkewHeapEntry root) {
		if (root == null)
			return;
		root.parent = null;
		clear(root.rightChild);
		root.rightChild = null;
		clear(root.leftChild);
		root.leftChild = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SkewHeap<E> clone() {
		// TODO Auto-generated method stub
		SkewHeap<E> clone;
		try {
			clone = (SkewHeap<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalError();
		}
		// cannot use this.clone(root), otherwise, the clone.root.this$0 will not be
		// clone but the current heap(this)
		clone.root = clone.clone(root);
		clone.modCount = 0;
		clone.entrys = null;
		clone.roots = null;
		return clone;
	}
	
	private SkewHeapEntry clone(SkewHeapEntry root){
		if(root == null)
			return null;
		SkewHeapEntry newRoot = new SkewHeapEntry(root.element());
		newRoot.leftChild = this.clone(root.leftChild());
		if(newRoot.leftChild != null)
			newRoot.leftChild.parent = newRoot;
		newRoot.rightChild = this.clone(root.rightChild());
		if(newRoot.rightChild != null)
			newRoot.rightChild.parent = newRoot;
		return newRoot;
	}

	private class SkewHeapEntry extends AbstractBinaryHeapEntry {
		private E element;
		private SkewHeapEntry parent;
		private SkewHeapEntry leftChild, rightChild;

		private SkewHeapEntry(E element) {
			super();
			this.element = Objects.requireNonNull(element);
			this.parent = null;
			this.leftChild = this.rightChild = null;
		}

		private void swapChild() {
			SkewHeapEntry temp = this.leftChild;
			this.leftChild = this.rightChild;
			this.rightChild = temp;
		}

		@Override
		public E element() {
			// TODO Auto-generated method stub
			return element;
		}

		@Override
		public SkewHeapEntry parent() {
			// TODO Auto-generated method stub
			return parent;
		}

		@Override
		public SkewHeapEntry leftChild() {
			// TODO Auto-generated method stub
			return leftChild;
		}

		@Override
		public SkewHeapEntry rightChild() {
			// TODO Auto-generated method stub
			return rightChild;
		}

		@Override
		public boolean set(E element) {
			// TODO Auto-generated method stub
			if(element == null)
				return false;
			this.element = element;
			return true;
		}

	}
	
	/**
	 * Save the state of the <tt>SkewHeap</tt> instance to a stream (that is,
	 * serialize it).
	 * 
	 * @serialData The length of the array backing the <tt>SkewHeap</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();
		
		Iterator<E> iterator = this.iterator();
		while(iterator.hasNext()){
			s.writeObject(iterator.next());
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Reconstitute the <tt>SkewHeap</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();
		int sz = this.size;
		
		for(int i = 0; i < sz; ++i){
			E element = (E) s.readObject();
			this.offer(element);
		}
		this.size = sz;
		this.modCount = 0;
	}

}
