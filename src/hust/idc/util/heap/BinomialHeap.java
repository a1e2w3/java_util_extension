package hust.idc.util.heap;

import hust.idc.util.Mergeable;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BinomialHeap<E> extends AbstractHeap<E> implements Heap<E>,
		Mergeable<BinomialHeap<E>>, Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4248487128611771410L;
	transient BinomialHeapEntry head;
	// Cannot initilize to 0, this statement will be executed after super() in
	// constructor and cover the current value.
	int size;
	transient volatile int modCount = 0;

	public BinomialHeap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BinomialHeap(Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	public BinomialHeap(Collection<? extends E> elements,
			Comparator<? super E> comparator) {
		super(elements, comparator);
		// TODO Auto-generated constructor stub
	}

	public BinomialHeap(Collection<? extends E> elements) {
		super(elements);
		// TODO Auto-generated constructor stub
	}

	public BinomialHeap(Heap<E> heap) {
		super(heap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		if (this.isEmpty()) {
			this.head = new BinomialHeapEntry(e);
			++modCount;
			++size;
			return true;
		} else {
			BinomialHeapEntry newNode = new BinomialHeapEntry(e);
			boolean modified = this.unionInternal(newNode);
			++size;
			++modCount;
			return modified;
		}
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		BinomialHeapEntry peekNode = this.peekNode();
		if (peekNode == null)
			return null;
		E elem = peekNode.element();
		this.removeElementInNode(peekNode);
		++modCount;
		--size;
		return elem;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		BinomialHeapEntry peekNode = this.peekNode();
		return peekNode == null ? null : peekNode.element();
	}

	private BinomialHeapEntry peekNode() {
		if (this.head == null)
			return null;
		BinomialHeapEntry peekNode = this.head;
		BinomialHeapEntry current = this.head.rightSibling();
		while (current != null) {
			if (this.compare(peekNode, current) < 0)
				peekNode = current;
			current = current.rightSibling();
		}
		return peekNode;
	}

	protected transient volatile Collection<HeapEntry<E>> entrys = null;
	protected transient volatile Collection<HeapEntry<E>> roots = null;

	@Override
	Collection<HeapEntry<E>> entrys() {
		// TODO Auto-generated method stub
		if(entrys == null){
			entrys = new AbstractCollection<HeapEntry<E>>(){

				@Override
				public Iterator<HeapEntry<E>> iterator() {
					// TODO Auto-generated method stub
					return new BinomialHeapIterator();
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return BinomialHeap.this.size;
				}
				
			};
		}
		return entrys;
	}

	@Override
	Collection<HeapEntry<E>> roots() {
		// TODO Auto-generated method stub
		if(roots == null){
			roots = new AbstractCollection<HeapEntry<E>>(){

				@Override
				public Iterator<HeapEntry<E>> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<HeapEntry<E>>(){
						private int expectedModCount = BinomialHeap.this.modCount;
						private HeapEntry<E> next = BinomialHeap.this.head;
						
						private final void checkForComodification() {
							if (expectedModCount != BinomialHeap.this.modCount)
								throw new ConcurrentModificationException();
						}

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return next != null;
						}

						@Override
						public HeapEntry<E> next() {
							// TODO Auto-generated method stub
							checkForComodification();
							if(next == null)
								throw new NoSuchElementException();
							HeapEntry<E> cur = next;
							next = cur.rightSibling();
							return cur;
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							throw new UnsupportedOperationException();
						}
					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					int s = BinomialHeap.this.size;
					int count = 0;
				    for(; s != 0; ++count){
				        s &= (s - 1) ; // 清除最低位的1
				    }
				    return count;
				}
				
			};
		}
		return roots;
	}

	@Override
	public boolean union(BinomialHeap<E> otherElem) {
		// TODO Auto-generated method stub
		if (otherElem == null || otherElem.isEmpty())
			return false;

		boolean modified = this.unionInternal(otherElem.head);
		if (modified) {
			this.size += otherElem.size();
			++modCount;
		}
		return modified;
	}

	private boolean unionInternal(BinomialHeapEntry otherHead) {
		if (otherHead == null)
			return false;

		// merge the head list of two heaps
		this.head = this.mergeHead(otherHead);

		BinomialHeapEntry current = this.head;
		BinomialHeapEntry prev = null;
		BinomialHeapEntry next = current.rightSibling();
		while (next != null) {
			if (current.degree() != next.degree()
					|| (next.rightSibling() != null && next.rightSibling()
							.degree() == next.degree())) {
				// do not merge
				prev = current;
				current = next;
			} else if (this.compare(current, next) >= 0) {
				current.sibling = next.rightSibling();
				binomialLink(current, next);
			} else {
				if (prev == null)
					this.head = next;
				else
					prev.sibling = next;

				binomialLink(next, current);
				current = next;
			}

			next = current.rightSibling();
		}

		return true;
	}

	private static <E> void binomialLink(
			BinomialHeap<E>.BinomialHeapEntry parent,
			BinomialHeap<E>.BinomialHeapEntry child) {
		if (parent == null || child == null)
			return;

		child.parent = parent;
		child.sibling = parent.child();
		parent.child = child;
		++parent.degree;
	}

	private BinomialHeapEntry mergeHead(BinomialHeapEntry otherHead) {
		if (otherHead == null)
			return this.head;
		else if (this.head == null)
			return otherHead;

		BinomialHeapEntry newHead, thisCur, otherCur, newCur;
		if (this.head.degree() <= otherHead.degree()) {
			newHead = this.head;
			thisCur = this.head.rightSibling();
			otherCur = otherHead;
		} else {
			newHead = otherHead;
			thisCur = this.head;
			otherCur = otherHead.rightSibling();
		}
		newCur = newHead;

		while (thisCur != null && otherCur != null) {
			if (thisCur.degree() <= otherCur.degree()) {
				newCur.sibling = thisCur;
				newCur = thisCur;
				thisCur = thisCur.rightSibling();
			} else {
				newCur.sibling = otherCur;
				newCur = otherCur;
				otherCur = otherCur.rightSibling();
			}
		}

		if (thisCur != null) {
			newCur.sibling = thisCur;
		} else {
			newCur.sibling = otherCur;
		}
		return newHead;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		try {
			@SuppressWarnings("unchecked")
			BinomialHeapEntry node = (BinomialHeapEntry) this.getEntry((E) o);
			if (node == null) {
				return false;
			} else {
				BinomialHeapEntry removedNode = this.removeElementInNode(node);
				if (removedNode == null)
					return false;
				else {
					++modCount;
					--size;
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.head == null ? 0 : this.size;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		BinomialHeapEntry current = this.head;
		while (current != null) {
			BinomialHeapEntry next = current.rightSibling();
			clearSubHeap(current);
			current = next;
		}

		this.head = null;
		this.size = 0;
		++modCount;
	}

	private void clearSubHeap(BinomialHeapEntry root) {
		if (root == null)
			return;

		BinomialHeapEntry child = root.child();
		while (child != null) {
			BinomialHeapEntry next = child.rightSibling();
			clearSubHeap(child);
			child = next;
		}
		root.dispose();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BinomialHeap<E> clone() {
		// TODO Auto-generated method stub
		BinomialHeap<E> clone;
		try {
			clone = (BinomialHeap<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalError();
		}
		
		BinomialHeapEntry newHead = null, curRoot = null;
		Iterator<HeapEntry<E>> rootIt = this.roots().iterator();
		while(rootIt.hasNext()){
			BinomialHeapEntry root = (BinomialHeapEntry) rootIt.next();
			// cannot use this.cloneBinomialTree(root), otherwise, the newRoot.this$0 will not be
			// clone but the current heap(this)
			BinomialHeapEntry newRoot = clone.cloneBinomialTree(root);
			if(curRoot == null){
				newHead = newRoot;
			} else {
				curRoot.sibling = newRoot;
			}
			curRoot = newRoot;
		}
		
		clone.head = newHead;
		clone.modCount = 0;
		clone.entrys = null;
		clone.roots = null;
		return clone;
	}
	
	private BinomialHeapEntry cloneBinomialTree(BinomialHeapEntry root){
		BinomialHeapEntry newRoot = new BinomialHeapEntry(root.element());
		BinomialHeapEntry curChild = null;
		Iterator<HeapEntry<E>> childIt = root.children().iterator();
		while(childIt.hasNext()){
			BinomialHeapEntry child = (BinomialHeapEntry) childIt.next();
			BinomialHeapEntry newChild = cloneBinomialTree(child);
			newChild.parent = newRoot;
			if(curChild == null){
				newRoot.child = newChild;
			} else {
				curChild.sibling = newChild;
			}
			curChild = newChild;
		}
		newRoot.degree = root.degree();
		return newRoot;
	}

	/**
	 * remove the element in node instead of the node itself
	 * 
	 * @param node
	 * @return
	 */
	private BinomialHeapEntry removeElementInNode(BinomialHeapEntry node) {
		if (node == null)
			return null;

		// bubble up the node to the root
		BinomialHeapEntry current = node;
		BinomialHeapEntry parent = current.parent();
		while (parent != null) {
			parent.exchangeElementWith(current);

			current = parent;
			parent = current.parent();
		}

		// remove the current node from head list
		if (this.head == current) {
			this.head = current.rightSibling();
		} else {
			current.leftSibling().sibling = current.rightSibling();
		}
		current.sibling = null;

		// reverse the order of the linked list of current's children, set the
		// parent field of each child to null
		BinomialHeapEntry currentChild = current.child();

		if (currentChild != null) {
			currentChild.parent = null;

			BinomialHeapEntry next = currentChild.rightSibling();
			while (next != null) {
				next.parent = null;

				BinomialHeapEntry nextRight = next.rightSibling();
				next.sibling = currentChild;
				currentChild = next;
				next = nextRight;
			}
			current.child.sibling = null;
			current.child = null;

			this.unionInternal(currentChild);
		}

		return current;
	}

	private class BinomialHeapEntry extends AbstractHeapEntry implements
			HeapEntry<E> {
		E element;
		transient BinomialHeapEntry parent = null, child = null,
				sibling = null;
		transient int degree = 0;

		private BinomialHeapEntry(E element) {
			super();
			this.set(element);
		}

		private void dispose() {
			this.set(null);
			this.parent = null;
			this.child = null;
			this.sibling = null;
			this.degree = 0;
		}

		@Override
		public E element() {
			// TODO Auto-generated method stub
			return this.element;
		}

		@Override
		public boolean set(E element) {
			// TODO Auto-generated method stub
			if (element == null)
				return false;
			++BinomialHeap.this.modCount;
			this.element = element;
			return true;
		}

		@Override
		public BinomialHeapEntry parent() {
			// TODO Auto-generated method stub
			return this.parent;
		}

		@Override
		public BinomialHeapEntry leftSibling() {
			// TODO Auto-generated method stub
			BinomialHeapEntry sibling = null;
			if (this.parent() == null) {
				if ((sibling = BinomialHeap.this.head) == null)
					return null;
			} else {
				if ((sibling = this.parent().child()) == null)
					throw new IllegalStateException("Heap Damaged!");
			}

			if (this == sibling)
				return null;

			BinomialHeapEntry nextSibling = sibling.rightSibling();
			while (nextSibling != null && this != nextSibling) {
				sibling = nextSibling;
				nextSibling = sibling.rightSibling();
			}
			if (nextSibling == null)
				throw new IllegalStateException("Heap Damaged!");
			return sibling;
		}

		@Override
		public BinomialHeapEntry rightSibling() {
			// TODO Auto-generated method stub
			return this.sibling;
		}

		@Override
		public BinomialHeapEntry child() {
			// TODO Auto-generated method stub
			return this.child;
		}

		@Override
		public Collection<HeapEntry<E>> children() {
			// TODO Auto-generated method stub
			if (children == null) {
				children = new AbstractCollection<HeapEntry<E>>() {

					@Override
					public Iterator<HeapEntry<E>> iterator() {
						// TODO Auto-generated method stub
						return new Iterator<HeapEntry<E>>() {
							private int expectedModCount = BinomialHeap.this.modCount;
							private HeapEntry<E> next = BinomialHeapEntry.this.child();

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								return next != null;
							}

							private final void checkForComodification() {
								if (expectedModCount != BinomialHeap.this.modCount)
									throw new ConcurrentModificationException();
							}

							@Override
							public HeapEntry<E> next() {
								// TODO Auto-generated method stub
								checkForComodification();
								if(next == null)
									throw new NoSuchElementException();
								HeapEntry<E> cur = next;
								next = cur.rightSibling();
								return cur;
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								throw new UnsupportedOperationException(
										"unsupported operation: HeapEntry.children().iterator().remove()");
							}

						};
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return BinomialHeapEntry.this.degree;
					}

				};
			}
			return children;
		}

		@Override
		public int degree() {
			// TODO Auto-generated method stub
			return this.degree;
		}
	}

	private class BinomialHeapIterator extends HeapPreorderIterator implements
			Iterator<HeapEntry<E>> {
		private int expectedModCount = BinomialHeap.this.modCount;
		
		private BinomialHeapIterator() {
			super();
		}
		
		@Override
		public HeapEntry<E> next() {
			// TODO Auto-generated method stub
			checkForComodification();
			return super.next();
		}

		private final void checkForComodification() {
			if (expectedModCount != BinomialHeap.this.modCount)
				throw new ConcurrentModificationException();
		}

		@Override
		protected boolean removeCurrent() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

	}
	
	/**
	 * Save the state of the <tt>BinomialHeap</tt> instance to a stream (that is,
	 * serialize it).
	 * 
	 * @serialData The size of the heap backing the <tt>BinomialHeap</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();
		s.writeInt(size);

		// Write out all elements in the proper order.
		Iterator<E> iterator = this.iterator();
		while(iterator.hasNext()){
			s.writeObject(iterator.next());
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Reconstitute the <tt>BinomialHeap</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();
		
		// Read in size
		int size = s.readInt();

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++){
			E element = (E) s.readObject();
			this.offer(element);
		}
	}

}
