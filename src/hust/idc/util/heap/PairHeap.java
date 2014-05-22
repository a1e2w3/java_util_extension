package hust.idc.util.heap;

import hust.idc.util.Mergeable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class PairHeap<E> extends AbstractHeap<E> implements Heap<E>,
		Mergeable<PairHeap<E>>, Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7854703372936306012L;

	transient PairHeapEntry root;
	int size;
	transient volatile int modCount;

	public PairHeap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PairHeap(Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	public PairHeap(Collection<? extends E> elements) {
		super(elements);
		// TODO Auto-generated constructor stub
	}

	public PairHeap(Collection<? extends E> elements,
			Comparator<? super E> comparator) {
		super(elements, comparator);
		// TODO Auto-generated constructor stub
	}

	public PairHeap(Heap<E> heap) {
		super(heap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean union(PairHeap<E> otherHeap) {
		// TODO Auto-generated method stub
		if (otherHeap == null || otherHeap.isEmpty())
			return false;
		++modCount;
		this.root = this.pairLink(this.root, otherHeap.root);
		this.size += otherHeap.size;
		return true;
	}

	private PairHeapEntry pairLink(PairHeapEntry root1, PairHeapEntry root2) {
		if (root1 == null)
			return root2;
		if (root2 == null)
			return root1;
		if (this.compare(root1, root2) < 0)
			return pairLink(root2, root1);
		root2.parent = root1;
		PairHeapEntry temp = root1.child;
		root1.child = root2;
		root2.sibling = temp;
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
					return new PairHeapEntryIterator();
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return PairHeap.this.size;
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
						private boolean hasNext = !PairHeap.this.isEmpty();
						private int expectedModCount = PairHeap.this.modCount;

						private void checkForComodification() {
							if (expectedModCount != PairHeap.this.modCount)
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
							return PairHeap.this.root;
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
					return PairHeap.this.root == null ? 0 : 1;
				}

			};
		}
		return roots;
	}

	private class PairHeapEntryIterator extends HeapPreorderIterator {
		private int expectedModCount = PairHeap.this.modCount;

		private PairHeapEntryIterator() {
			super();
		}

		private void checkForComodification() {
			if (expectedModCount != PairHeap.this.modCount)
				throw new ConcurrentModificationException();
		}

		@Override
		boolean removeCurrent() {
			// TODO Auto-generated method stub
			checkForComodification();
			next = PairHeap.this.removeEntry((PairHeapEntry) current);
			expectedModCount = PairHeap.this.modCount;
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
		this.root = this.pairLink(this.root, new PairHeapEntry(e));
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
		return root == null ? null : root.element();
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		if (this.isEmpty())
			return false;
		try {
			@SuppressWarnings("unchecked")
			PairHeapEntry entry = (PairHeapEntry) this.getEntry((E) o);
			if (entry == null)
				return false;
			this.removeEntry(entry);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private PairHeapEntry removeEntry(PairHeapEntry entry) {
		assert entry != null : "parameter is null in method removeEntry";
		PairHeapEntry firstChild = entry.child;
		entry.child = null;

		if (firstChild == null)
			return null;

		PairHeapEntry child = (firstChild.sibling == null ? null
				: firstChild.sibling.sibling);
		firstChild = this.pairLink(firstChild, firstChild.sibling);
		PairHeapEntry prev = firstChild;

		while (child != null) {
			PairHeapEntry next = (child.sibling == null ? null
					: child.sibling.sibling);
			child = this.pairLink(child, child.sibling);
			prev.sibling = child;
			prev = child;
			child = next;
		}

		child = firstChild.sibling;
		while (child != null) {
			PairHeapEntry next = child.sibling;
			firstChild = this.pairLink(firstChild, child);
			child = next;
		}

		firstChild.parent = entry.parent;
		if (entry.parent == null) {
			this.root = firstChild;
		} else {
			child = entry.parent.child;
			if (child == entry) {
				entry.parent.child = firstChild;
				firstChild.sibling = entry.sibling;
			} else {
				while (child.sibling != entry)
					child = child.sibling;
				child.sibling = firstChild;
				firstChild.sibling = entry.sibling;
			}
			entry.parent = null;
			entry.sibling = null;
		}
		return firstChild;
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

	private void clear(PairHeapEntry root) {
		if (root == null)
			return;
		root.parent = null;
		PairHeapEntry child = root.child;
		while (child != null) {
			clear(child);
			PairHeapEntry next = child.sibling;
			child.sibling = null;
			child = next;
		}
		root.child = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PairHeap<E> clone() {
		// TODO Auto-generated method stub
		PairHeap<E> clone;
		try {
			clone = (PairHeap<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalError();
		}
		// cannot use this.clone(root), otherwise, the clone.root.this$0 will
		// not be
		// clone but the current heap(this)
		clone.root = clone.clone(root);
		clone.modCount = 0;
		clone.entrys = null;
		clone.roots = null;
		return clone;
	}

	private PairHeapEntry clone(PairHeapEntry root) {
		if (root == null)
			return null;
		PairHeapEntry newRoot = new PairHeapEntry(root.element());
		PairHeapEntry child = root.child, curChild = null;
		while (child != null) {
			PairHeapEntry newChild = clone(child);
			newChild.parent = newRoot;
			if (curChild == null)
				newRoot.child = newChild;
			else
				curChild.sibling = newChild;
			child = child.sibling;
		}
		return newRoot;
	}

	private class PairHeapEntry extends AbstractHeapEntry {
		private E element;
		private transient PairHeapEntry parent;
		private transient PairHeapEntry child, sibling;

		private PairHeapEntry(E element) {
			super(Objects.requireNonNull(element));
			this.parent = null;
			this.child = null;
			this.sibling = null;
		}

		@Override
		public E element() {
			// TODO Auto-generated method stub
			return element;
		}

		@Override
		public AbstractHeapEntry parent() {
			// TODO Auto-generated method stub
			return parent;
		}

		@Override
		public HeapEntry<E> child() {
			// TODO Auto-generated method stub
			return child;
		}

		@Override
		public PairHeapEntry leftSibling() {
			// TODO Auto-generated method stub
			if (parent == null)
				return null;
			PairHeapEntry ch = parent.child;
			while (ch != null && ch.sibling != this) {
				ch = ch.sibling;
			}
			return ch;
		}

		@Override
		public AbstractHeapEntry rightSibling() {
			// TODO Auto-generated method stub
			return sibling;
		}

		@Override
		public boolean set(E element) {
			// TODO Auto-generated method stub
			if (element == null)
				return false;
			this.element = element;
			return true;
		}

	}

	/**
	 * Save the state of the <tt>PairHeap</tt> instance to a stream (that is,
	 * serialize it).
	 * 
	 * @serialData The length of the array backing the <tt>PairHeap</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();

		if (!this.isEmpty()) {
			writeSubTree(root, s);
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	private void writeSubTree(PairHeapEntry root, ObjectOutputStream s)
			throws IOException {
		// TODO Auto-generated method stub
		assert root != null && s != null;
		s.writeObject(root.element());
		s.writeInt(root.degree());
		Iterator<HeapEntry<E>> iterator = root.children().iterator();
		while (iterator.hasNext()) {
			writeSubTree((PairHeapEntry) iterator.next(), s);
		}
	}

	/**
	 * Reconstitute the <tt>PairHeap</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();
		if (size > 0)
			this.root = readSubTree(s);
	}

	private PairHeapEntry readSubTree(ObjectInputStream s)
			throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		assert s != null;
		@SuppressWarnings("unchecked")
		E element = (E) s.readObject();
		PairHeapEntry root = new PairHeapEntry(element);
		int degree = s.readInt();
		PairHeapEntry curChild = null;
		for (int i = 0; i < degree; ++i) {
			PairHeapEntry child = readSubTree(s);
			child.parent = root;
			if(curChild == null)
				root.child = child;
			else
				curChild.sibling = child;
			curChild = child;
		}
		return root;
	}

}
