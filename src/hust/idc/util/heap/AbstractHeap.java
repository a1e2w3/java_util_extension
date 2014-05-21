package hust.idc.util.heap;

import java.util.AbstractCollection;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * This class provides skeletal implementations of some {@link Collection
 * Collection}, {@link java.util.Queue Queue} and {@link Heap} operations. The
 * implementations in this class are appropriate when the base implementation
 * does <em>not</em> allow <tt>null</tt> elements. Like the super class
 * <tt>AbstractQueue</tt> suggested, methods {@link #add add}, {@link #remove
 * remove}, and {@link #element element} are based on {@link #offer offer},
 * {@link #poll poll}, and {@link #peek peek}, respectively but throw exceptions
 * instead of indicating failure via <tt>false</tt> or <tt>null</tt> returns.
 * 
 * <p>
 * A <tt>Heap</tt> implementation that extends this class must minimally define
 * a method {@link Queue#offer} which does not permit insertion of <tt>null</tt>
 * elements, along with methods {@link Queue#peek}, {@link Queue#poll},
 * {@link Collection#size}, {@link Collection#remove}, {@link Heap#rebuild} and
 * a {@link Collection#iterator} supporting {@link Iterator#remove}. Typically,
 * additional methods will be overridden as well. If these requirements cannot
 * be met, consider instead subclassing {@link AbstractQueue} or
 * {@link java.util.AbstractCollection AbstractCollection}.
 * 
 * <p>
 * This class used an Object named {@linked HeapIndex} to locate an element in
 * the heap. Correspondingly, a method to obtain an index was needed. A
 * <tt>Heap</tt> implementation that extends this class must define several
 * abstract methods defined in this class based on its typical method of
 * organizing elements. {@link #indexOf} to return the index of a element in a
 * typical heap. {@link #heapEntry()} to return the entry index of the heap.
 * 
 * <p>
 * In addition, A <tt>Heap</tt> implementation that extends this class must
 * define {@link #copy()} method to support the {@link #clone()} and
 * {@link #sort()}. The <tt>copy()</tt> method used to copy a typical heap and
 * return a new instance.
 * 
 * 
 * <p>
 * This class is a member of the <a href="{@docRoot}
 * /../technotes/guides/collections/index.html"> Java Collections Framework</a>.
 * 
 * @since 1.2.0
 * @author Wang Cong
 * 
 * @param <E>
 *            the type of elements held in this collection
 */
public abstract class AbstractHeap<E> extends AbstractQueue<E> implements
		Heap<E> {
	final Comparator<? super E> comparator;

	public AbstractHeap() {
		super();
		this.comparator = null;
	}

	public AbstractHeap(Comparator<? super E> comparator) {
		super();
		this.comparator = comparator;
	}

	public AbstractHeap(Collection<? extends E> elements) {
		this();
		if (elements != null)
			this.addAll(elements);
	}

	public AbstractHeap(Collection<? extends E> elements,
			Comparator<? super E> comparator) {
		this(comparator);
		if (elements != null)
			this.addAll(elements);
	}

	public AbstractHeap(Heap<E> heap) {
		this(heap == null ? null : heap.getComparator());
		if (heap != null)
			this.addAll(heap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hust.idc.util.heap.Heap#rebuild()
	 */
	@Override
	public final Comparator<? super E> getComparator() {
		// TODO Auto-generated method stub
		return this.comparator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#contains(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		if (o == null)
			return false;
		try {
			return this.getEntry((E) o) != null;
		} catch (Throwable th) {
			return false;
		}
	}

	/**
	 * @Override Replace the old element with the new element. If new element
	 *           compare old element return 0, check
	 *           oldElement.equals(newElement), if not equal, replace the
	 *           element directly, else return <tt>false</tt> directly. Else if
	 *           new element is greater, call
	 *           {@link #increaseElement(Object, Object)}. Else, new element is
	 *           less, call {@link #decreaseElement(Object, Object)}.
	 * 
	 * @param oldElement
	 *            the old element to be replaced
	 * @param newElement
	 *            the new element to replace the old one
	 * @return <tt>true</tt> if the element changed.
	 * 
	 */
	public boolean replace(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		int result = this.compare(oldElement, newElement);
		if (0 == result) {
			if (oldElement.equals(newElement))
				return false;
			else
				return this.replaceNotHeaplify(oldElement, newElement) != null;
		} else if (0 > result) {
			return this.increaseElement(oldElement, newElement);
		} else {
			return this.decreaseElement(oldElement, newElement);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hust.idc.util.heap.Heap#increaseElement(E, E)
	 */
	@Override
	public boolean increaseElement(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		int result = this.compare(oldElement, newElement);
		if (result > 0) {
			throw new IllegalArgumentException(
					"new element is less than old element");
		} else if (result == 0) {
			if (oldElement.equals(newElement))
				return false;
		}
		HeapEntry<E> index = this.replaceNotHeaplify(oldElement, newElement);
		if (index == null)
			return false;

		if (result < 0)
			index.heaplifyUp();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hust.idc.util.heap.Heap#decreaseElement(E, E)
	 */
	@Override
	public boolean decreaseElement(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		int result = this.compare(oldElement, newElement);
		if (result < 0) {
			throw new IllegalArgumentException(
					"new element is greater than old element");
		} else if (result == 0) {
			if (oldElement.equals(newElement))
				return false;
		}
		HeapEntry<E> index = this.replaceNotHeaplify(oldElement, newElement);
		if (index == null)
			return false;

		if (result > 0)
			index.heaplifyDown();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hust.idc.util.heap.Heap#heaplifyAt(E)
	 */
	@Override
	public boolean heaplifyAt(E element) throws NoSuchElementException {
		HeapEntry<E> index = this.getEntryByReference(element);
		if (index == null)
			throw new NoSuchElementException("No such element in heap: "
					+ element);

		if (index.heaplifyUp())
			return true;
		return index.heaplifyDown();
	}

	@Override
	public int size() {
		return this.entrys().size();
	}

	@Override
	public abstract Collection<HeapEntry<E>> entrys();

	@Override
	public abstract Collection<HeapEntry<E>> roots();

	@Override
	public abstract boolean offer(E e);

	@Override
	public abstract E poll();

	@Override
	public abstract E peek();

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new Iterator<E>() {
			private Iterator<HeapEntry<E>> entrys = AbstractHeap.this.entrys()
					.iterator();

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return entrys.hasNext();
			}

			@Override
			public E next() {
				// TODO Auto-generated method stub
				return entrys.next().element();
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				entrys.remove();
			}

		};
	}

	/**
	 * Found the position of old element and replace it with the new element,
	 * but not change the structure of the heap.
	 * 
	 * @param oldElement
	 *            the old element to be replaced
	 * @param newElement
	 *            the new element to replace the old one
	 * @return <tt>null</tt> if the new element was <tt>null</tt> or the old
	 *         element was not found in heap; else return the actual position of
	 *         the replacement occurred.
	 */
	HeapEntry<E> replaceNotHeaplify(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		if (newElement == null)
			return null;
		HeapEntry<E> index = this.getEntry(oldElement);
		if (index != null) {
			index.set(newElement);
		}
		return index;
	}

	/**
	 * Get Heap Index of the reference of the element, using "==" to decide if
	 * it was the one we need
	 * 
	 * @param element
	 *            the reference of the element
	 * @return <tt>null</tt> if not found, else return the actual index of the
	 *         element
	 */
	HeapEntry<E> getEntryByReference(E element) {
		if (element == null)
			return null;
		Iterator<HeapEntry<E>> entries = this.entrys().iterator();
		while (entries.hasNext()) {
			HeapEntry<E> entry = entries.next();
			if (element == entry.element()) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * Found the position of an element in the heap. using <tt>equal</tt> method
	 * but not "==" to decide whether two element were equal.
	 * 
	 * @param element
	 *            the element try to found
	 * @return <tt>null</tt> if not found; else return the actual index of the
	 *         element
	 */
	HeapEntry<E> getEntry(E element) {
		if (element == null)
			return null;
		Iterator<HeapEntry<E>> roots = this.roots().iterator();
		while (roots.hasNext()) {
			HeapEntry<E> root = roots.next();
			HeapEntry<E> entry = this.find(element, root);
			if (entry != null)
				return entry;
		}
		return null;
	}

	/**
	 * Find the position of the element in a tree
	 * 
	 * @param element
	 *            the element to find
	 * @param root
	 *            the root position of the tree
	 * @return <tt>null</tt> if the root was <tt>null</tt> or element not found;
	 *         else return the actual index of the element
	 */
	HeapEntry<E> find(E element, HeapEntry<E> root) {
		if (element == null || root == null)
			return null;

		int result = this.compare(element, root.element());
		if (result > 0)
			return null;
		if (result == 0) {
			if (element.equals(root.element()))
				return root;
		}

		Iterator<HeapEntry<E>> childIt = root.children().iterator();
		while (childIt.hasNext()) {
			HeapEntry<E> index = this.find(element, childIt.next());
			if (index != null)
				return index;
		}
		return null;
	}

	/**
	 * Compare two elements using the user defined comparator.
	 * 
	 * @param elem1
	 * @param elem2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	int compare(E elem1, E elem2) {
		if (this.comparator == null) {
			try {
				if (elem1 != null && elem1 instanceof Comparable<?>) {
					return ((Comparable<E>) elem1).compareTo(elem2);
				} else if (elem2 != null && elem2 instanceof Comparable<?>) {
					return 0 - ((Comparable<E>) elem2).compareTo(elem1);
				} else {
					return 0;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return 0;
			}
		} else
			return this.comparator.compare(elem1, elem2);
	}

	/**
	 * Compare two elements contains in heap index
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	int compare(HeapEntry<? extends E> i1, HeapEntry<? extends E> i2) {
		if (i1 == null) {
			return i2 == null ? 0 : -1;
		} else if (i2 == null) {
			return 1;
		} else {
			return this.compare(i1.element(), i2.element());
		}
	}

	/**
	 * Base class for Iterators for Heaps Using preorder tree traversal method
	 * to iterate the element in a heap
	 * 
	 * @author Wang Cong
	 * 
	 */
	abstract class HeapPreorderIterator implements Iterator<HeapEntry<E>> {
		HeapEntry<E> current = null;
		HeapEntry<E> next;
		boolean removed = false;

		HeapPreorderIterator() {
			Iterator<HeapEntry<E>> roots = AbstractHeap.this.roots().iterator();
			next = roots.hasNext() ? roots.next() : null;
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return next != null;
		}

		@Override
		public HeapEntry<E> next() {
			// TODO Auto-generated method stub
			// checkForComodification();
			if (next == null) {
				throw new NoSuchElementException();
			}

			removed = false;
			current = next;
			next = this.getNext();
			return current;
		}

		@Override
		public void remove() {
			if (current == null || removed) {
				throw new IllegalStateException();
			}
			removed = this.removeCurrent();
		}

		boolean removeCurrent() {
			throw new UnsupportedOperationException(
					"unsupported operation: iterator.remove");
		}

		HeapEntry<E> getNext() {
			if (current == null) {
				return null;
			} else {
				HeapEntry<E> index = this.leftChild();
				if (index != null) {
					return index;
				}
				index = current.rightSibling();
				if (index != null) {
					return index;
				} else {
					HeapEntry<E> ancestor = current.parent();
					while (ancestor != null) {
						index = ancestor.rightSibling();
						if (index != null) {
							return index;
						}
						ancestor = ancestor.parent();
					}
					return null;
				}
			}
		}

		private final HeapEntry<E> leftChild() {
			if (current == null)
				return null;
			return current.child();
		}
	}

	/**
	 * Base class for Heap Index
	 * 
	 * @author Wang Cong
	 * 
	 */
	abstract class AbstractHeapEntry implements HeapEntry<E> {
		AbstractHeapEntry() {

		}

		boolean exchangeElementWith(HeapEntry<E> otherIndex) {
			if (otherIndex == null)
				return false;
			if (otherIndex.equals(this))
				return true;

			E temp = this.element();
			this.set(otherIndex.element());
			otherIndex.set(temp);
			return true;
		}

		int subHeapSize() {
			int size = 1;
			Iterator<HeapEntry<E>> childIt = this.children().iterator();
			while (childIt.hasNext()) {
				size += ((AbstractHeapEntry) childIt.next()).subHeapSize();
			}
			return size;
		}

		@Override
		public boolean isRoot() {
			// TODO Auto-generated method stub
			return this.parent() == null;
		}

		@Override
		public boolean isLeaf() {
			// TODO Auto-generated method stub
			return this.child() == null;
		}

		@Override
		public abstract E element();

		@Override
		public abstract AbstractHeapEntry parent();

		@Override
		public abstract HeapEntry<E> child();

		@Override
		public abstract AbstractHeapEntry leftSibling();

		@Override
		public abstract AbstractHeapEntry rightSibling();

		@Override
		public boolean set(E element) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException(
					"unsupported operation: HeapEntry.set");
		}

		@Override
		public int degree() {
			// TODO Auto-generated method stub
			return this.children().size();
		}

		protected transient volatile Collection<HeapEntry<E>> children = null;

		@Override
		public Collection<HeapEntry<E>> children() {
			if (children == null) {
				children = new AbstractCollection<HeapEntry<E>>() {

					@Override
					public Iterator<HeapEntry<E>> iterator() {
						// TODO Auto-generated method stub
						return new DefaultChildIterator();
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						int count = 0;
						Iterator<HeapEntry<E>> childIt = iterator();
						while (childIt.hasNext()) {
							childIt.next();
							++count;
						}
						return count;

					}

				};
			}
			return children;
		}

		@Override
		public boolean heaplifyUp() {
			// TODO Auto-generated method stub
			AbstractHeapEntry parent = this.parent();
			if (parent == null)
				return false;
			HeapEntry<E> largest = parent.heaplifyAtRoot();
			if (!largest.equals(parent)) {
				parent.heaplifyUp();
				return true;
			}
			return false;
		}

		@Override
		public boolean heaplifyDown() {
			// TODO Auto-generated method stub
			HeapEntry<E> largest = this.heaplifyAtRoot();
			if (!largest.equals(this)) {
				largest.heaplifyDown();
				return true;
			}
			return false;
		}

		HeapEntry<E> heaplifyAtRoot() {
			HeapEntry<E> largest = this;

			// find largest index in children
			Iterator<HeapEntry<E>> it = this.children().iterator();
			while (it.hasNext()) {
				HeapEntry<E> current = it.next();
				if (AbstractHeap.this.compare(current, largest) > 0) {
					largest = current;
				}
			}

			if (!largest.equals(this)) {
				this.exchangeElementWith(largest);
			}
			return largest;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("[Element : ");
			builder.append(this.element().toString());
			builder.append(", parent : ");
			AbstractHeapEntry parent = this.parent();
			builder.append(parent == null ? "null" : parent.element()
					.toString());
			builder.append(", child : ");
			HeapEntry<E> child = this.child();
			builder.append(child == null ? "null" : child.element().toString());
			builder.append(", left : ");
			AbstractHeapEntry left = this.leftSibling();
			builder.append(left == null ? "null" : left.element().toString());
			builder.append(", right : ");
			AbstractHeapEntry right = this.rightSibling();
			builder.append(right == null ? "null" : right.element().toString());
			builder.append("]");
			return builder.toString();
		}

		private class DefaultChildIterator implements Iterator<HeapEntry<E>> {
			private HeapEntry<E> nextChild, entryChild;
			private boolean mark = true;

			private DefaultChildIterator() {
				this.entryChild = AbstractHeapEntry.this.child();
				this.nextChild = entryChild;
			}

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return nextChild != null;
			}

			@Override
			public HeapEntry<E> next() {
				// TODO Auto-generated method stub
				if (nextChild == null) {
					throw new NoSuchElementException();
				}

				HeapEntry<E> current = nextChild;
				if (mark) {
					nextChild = current.rightSibling();
					if (nextChild == null) {
						nextChild = entryChild.leftSibling();
						mark = false;
						if (nextChild == entryChild) {
							nextChild = null;
						}
					} else if (nextChild == entryChild) {
						nextChild = null;
					}
				} else {
					nextChild = current.leftSibling();
					if (nextChild == entryChild) {
						nextChild = null;
					}
				}
				return current;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException(
						"unsupported operation: HeapEntry.children().iterator().remove()");
			}

		}

	}

	abstract class AbstractBinaryHeapEntry extends AbstractHeapEntry {
		AbstractBinaryHeapEntry(){
			super();
		}
		
		public abstract AbstractBinaryHeapEntry parent();
		
		abstract AbstractBinaryHeapEntry leftChild();

		abstract AbstractBinaryHeapEntry rightChild();

		@Override
		public final HeapEntry<E> child() {
			return this.leftChild() == null ? this.rightChild() : this
					.leftChild();
		}

		@Override
		public AbstractBinaryHeapEntry leftSibling() {
			// TODO Auto-generated method stub
			if (this.parent() == null || this == parent().leftChild())
				return null;
			return parent().leftChild();
		}

		@Override
		public AbstractHeapEntry rightSibling() {
			// TODO Auto-generated method stub
			if (this.parent() == null || this == parent().rightChild())
				return null;
			return parent().rightChild();
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
							private boolean inLeft = (AbstractBinaryHeapEntry.this
									.leftChild() != null);
							private HeapEntry<E> next = AbstractBinaryHeapEntry.this
									.child();

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								return next != null;
							}

							@Override
							public HeapEntry<E> next() {
								// TODO Auto-generated method stub
								if (next == null)
									throw new NoSuchElementException();
								HeapEntry<E> cur = next;
								next = inLeft ? cur.rightSibling() : null;
								inLeft = false;
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
						if (AbstractBinaryHeapEntry.this.leftChild() == null)
							return AbstractBinaryHeapEntry.this.rightChild() == null ? 0
									: 1;
						else
							return AbstractBinaryHeapEntry.this.rightChild() == null ? 1
									: 2;
					}

				};
			}
			return children;
		}
	}
}
