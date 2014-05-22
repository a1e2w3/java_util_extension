package hust.idc.util.heap;

import hust.idc.util.Mergeable;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class LeftistTree<E> extends AbstractHeap<E> implements Heap<E>,
		Mergeable<LeftistTree<E>>, Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 525489916375766502L;
	transient LeftistTreeEntry root;
	int size;
	transient volatile int modCount = 0;

	public LeftistTree() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LeftistTree(Collection<? extends E> elements,
			Comparator<? super E> comparator) {
		super(elements, comparator);
		// TODO Auto-generated constructor stub
	}

	public LeftistTree(Collection<? extends E> elements) {
		super(elements);
		// TODO Auto-generated constructor stub
	}

	public LeftistTree(Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	public LeftistTree(Heap<E> heap) {
		super(heap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean union(LeftistTree<E> otherHeap) {
		// TODO Auto-generated method stub
		if (otherHeap == null || otherHeap.isEmpty())
			return false;
		++modCount;
		this.root = this.unionInternal(this.root, otherHeap.root);
		this.size += otherHeap.size;
		return true;
	}

	private LeftistTreeEntry unionInternal(LeftistTreeEntry root1,
			LeftistTreeEntry root2) {
		if (root1 == null)
			return root2;
		if (root2 == null)
			return root1;
		if (this.compare(root1, root2) < 0)
			return unionInternal(root2, root1);

		root2.parent = root1;
		root1.rightChild = unionInternal(root1.rightChild, root2);
		root1.leftist();
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
					return new LeftistTreeEntryIterator();
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return LeftistTree.this.size;
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
						private boolean hasNext = !LeftistTree.this.isEmpty();
						private int expectedModCount = LeftistTree.this.modCount;

						private void checkForComodification() {
							if (expectedModCount != LeftistTree.this.modCount)
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
							return LeftistTree.this.root;
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
					return LeftistTree.this.root == null ? 0 : 1;
				}

			};
		}
		return roots;
	}
	
	private class LeftistTreeEntryIterator extends HeapPreorderIterator {
		private int expectedModCount = LeftistTree.this.modCount;
		
		private LeftistTreeEntryIterator(){
			super();
		}

		private void checkForComodification() {
			if (expectedModCount != LeftistTree.this.modCount)
				throw new ConcurrentModificationException();
		}

		@Override
		boolean removeCurrent() {
			// TODO Auto-generated method stub
			checkForComodification();
			next = LeftistTree.this.removeEntry((LeftistTreeEntry) current);
			expectedModCount = LeftistTree.this.modCount;
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
		if(e == null)
			return false;
		++modCount;
		this.root = this.unionInternal(this.root, new LeftistTreeEntry(e));
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
			LeftistTreeEntry entry = (LeftistTreeEntry) this.getEntry((E) o);
			if (entry == null)
				return false;
			this.removeEntry(entry);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private LeftistTreeEntry removeEntry(LeftistTreeEntry entry) {
		assert entry != null : "parameter is null in method removeEntry";
		++modCount;
		if (entry.leftChild != null) {
			entry.leftChild.parent = null;
		}
		if (entry.rightChild != null) {
			entry.rightChild.parent = null;
		}

		LeftistTreeEntry newRoot = this.unionInternal(entry.rightChild,
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
		this.leftist(newRoot);
		--size;
		return newRoot;
	}
	
	private void leftist(LeftistTreeEntry entry){
		while(entry != null){
			entry.leftist();
			entry = entry.parent();
		}
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

	private void clear(LeftistTreeEntry root) {
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
	public LeftistTree<E> clone() {
		// TODO Auto-generated method stub
		LeftistTree<E> clone;
		try {
			clone = (LeftistTree<E>) super.clone();
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
	
	private LeftistTreeEntry clone(LeftistTreeEntry root){
		if(root == null)
			return null;
		LeftistTreeEntry newRoot = new LeftistTreeEntry(root.element());
		newRoot.leftChild = this.clone(root.leftChild());
		if(newRoot.leftChild != null)
			newRoot.leftChild.parent = newRoot;
		newRoot.rightChild = this.clone(root.rightChild());
		if(newRoot.rightChild != null)
			newRoot.rightChild.parent = newRoot;
		newRoot.npl = root.npl;
		return newRoot;
	}

	private class LeftistTreeEntry extends AbstractBinaryHeapEntry {
		private E element;
		private transient int npl = 0;
		private transient LeftistTreeEntry parent;
		private transient LeftistTreeEntry leftChild, rightChild;

		private LeftistTreeEntry(E element) {
			super(Objects.requireNonNull(element));
			this.parent = null;
			this.leftChild = this.rightChild = null;
		}

		private void leftist() {
			if (this.leftChildNPL() < this.rightChildNPL()) {
				LeftistTreeEntry temp = this.leftChild;
				this.leftChild = this.rightChild;
				this.rightChild = temp;
			}
			this.npl = this.rightChildNPL() + 1;
		}

		private int leftChildNPL() {
			return leftChild == null ? -1 : leftChild.npl;
		}

		private int rightChildNPL() {
			return rightChild == null ? -1 : rightChild.npl;
		}

		@Override
		public E element() {
			// TODO Auto-generated method stub
			return element;
		}

		@Override
		public LeftistTreeEntry parent() {
			// TODO Auto-generated method stub
			return parent;
		}

		@Override
		public LeftistTreeEntry leftChild() {
			// TODO Auto-generated method stub
			return leftChild;
		}

		@Override
		public LeftistTreeEntry rightChild() {
			// TODO Auto-generated method stub
			return rightChild;
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
	 * Save the state of the <tt>LeftistTree</tt> instance to a stream (that is,
	 * serialize it).
	 * 
	 * @serialData The length of the array backing the <tt>LeftistTree</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();
		
		if(!this.isEmpty()){
			root.writeBinaryTree(s);
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Reconstitute the <tt>LeftistTree</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();
		if(size > 0)
			this.root = readLeftistTree(s);
	}
	
	private LeftistTreeEntry readLeftistTree(java.io.ObjectInputStream s) throws ClassNotFoundException, IOException{
		@SuppressWarnings("unchecked")
		E element = (E) s.readObject();
		LeftistTreeEntry root = new LeftistTreeEntry(element);
		if(s.readBoolean()){
			root.leftChild = readLeftistTree(s);
			root.leftChild.parent = root;
		}
		if(s.readBoolean()){
			root.rightChild = readLeftistTree(s);
			root.rightChild.parent = root;
		}
		root.npl = root.rightChild == null ? 0 : root.rightChild.npl + 1;
		return root;
	}

}
