package hust.idc.util.heap;

import hust.idc.util.EmptyIterator;
import hust.idc.util.Mergeable;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class FibonacciHeap<E> extends AbstractHeap<E> implements Heap<E>,
		Mergeable<FibonacciHeap<E>>, Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1775006086768839688L;
	transient FibonacciHeapEntry minRoot;
	boolean consolidated = true;
	/**
	 * used to iterate
	 */
	transient Set<FibonacciHeapEntry> nodes;

	transient volatile int modCount = 0;

	static final double Ln_Phi = Math.log((Math.sqrt(5.0d) + 1) / 2);

	public FibonacciHeap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(Collection<? extends E> elements,
			Comparator<? super E> comparator) {
		super(elements, comparator);
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(Collection<? extends E> elements) {
		super(elements);
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(Comparator<? super E> comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	public FibonacciHeap(Heap<E> heap) {
		super(heap);
		// TODO Auto-generated constructor stub
	}

	private Set<FibonacciHeapEntry> getNodeSet(FibonacciHeapEntry root,
			Set<FibonacciHeapEntry> nodeSet) {
		if (root == null)
			return null;
		if (nodeSet == null)
			nodeSet = new HashSet<FibonacciHeapEntry>();
		FibonacciHeapEntry current = root, next = null;
		nodeSet.add(current);
		while (current != null) {
			next = current.child();
			if (next != null) {
				current = next;
				nodeSet.add(current);
				continue;
			}
			next = current.rightSibling();
			FibonacciHeapEntry entry = current.parent() == null ? this.minRoot
					: current.parent().child();
			if (next != entry) {
				current = next;
				nodeSet.add(current);
				continue;
			} else {
				FibonacciHeapEntry ancestor = current.parent();
				boolean found = false;
				while (ancestor != null) {
					next = ancestor.rightSibling();
					entry = ancestor.parent() == null ? this.minRoot : ancestor
							.parent().child();
					if (next != entry) {
						current = next;
						nodeSet.add(current);
						found = true;
						break;
					}
					ancestor = ancestor.parent();
				}
				if (found) {
					continue;
				}
				current = null;
			}
		}

		return nodeSet;
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		if (e == null)
			return false;
		FibonacciHeapEntry newNode = new FibonacciHeapEntry(e);
		this.minRoot = this.insertSubHeap(null, newNode);
		if (this.nodes == null) {
			this.nodes = new HashSet<FibonacciHeapEntry>();
		}
		this.nodes.add(newNode);
		++modCount;
		return true;
	}

	@Override
	public E poll() {
		return this.poll(true);
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		try {
			@SuppressWarnings("unchecked")
			FibonacciHeapEntry node = (FibonacciHeapEntry) this.getEntry((E) o);
			if (this.nodes != null)
				this.nodes.remove(node);
			return this.removeEntry(node);
		} catch (Exception e) {
			return false;
		}
	}

	private E poll(boolean consolidate) {
		// TODO Auto-generated method stub
		if (this.isEmpty())
			return null;
		FibonacciHeapEntry poll = this.minRoot;
		if (this.minRoot.leftSibling() == this.minRoot)
			this.minRoot = null;
		else
			this.minRoot = this.minRoot.leftSibling();

		// remove peek node
		this.cutSubHeap(poll);
		this.nodes.remove(poll);

		if (poll.child() != null) {
			FibonacciHeapEntry child = poll.child();
			child.parent = null;
			FibonacciHeapEntry current = child.rightSibling();
			while (current != child) {
				current.parent = null;
				current = current.rightSibling();
			}

			if (this.minRoot == null)
				this.minRoot = child;
			else {
				current = child.leftSibling();
				child.left = this.minRoot;
				current.right = this.minRoot.rightSibling();
				this.minRoot.right.left = current;
				this.minRoot.right = child;
			}
		}

		// merge root list
		if (consolidate) {
			this.minRoot = this.consolidate(null);
			this.consolidated = true;
		} else {
			++modCount;
		}

		this.minRoot = this.peekNode();
		return poll.element();
	}

	protected transient volatile Collection<HeapEntry<E>> entrys = null;
	protected transient volatile Collection<HeapEntry<E>> roots = null;

	@Override
	Collection<HeapEntry<E>> entrys() {
		// TODO Auto-generated method stub
		if (entrys == null) {
			entrys = new AbstractCollection<HeapEntry<E>>() {

				@Override
				public Iterator<hust.idc.util.heap.Heap.HeapEntry<E>> iterator() {
					// TODO Auto-generated method stub
					return new FibonacciHeapIterator();
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return FibonacciHeap.this.nodes == null ? 0
							: FibonacciHeap.this.nodes.size();
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
						private int expectedModCount = FibonacciHeap.this.modCount;
						private HeapEntry<E> next = FibonacciHeap.this.minRoot;

						private final void checkForComodification() {
							if (expectedModCount != FibonacciHeap.this.modCount)
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
							if (next == null)
								throw new NoSuchElementException();
							HeapEntry<E> cur = next;
							next = cur.rightSibling();
							if (next == FibonacciHeap.this.minRoot)
								next = null;
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
					int count = 0;
					Iterator<HeapEntry<E>> rootIt = iterator();
					while (rootIt.hasNext()) {
						rootIt.next();
						++count;
					}
					return count;
				}

			};
		}
		return roots;
	}

	public void consolidate() {
		if (!consolidated) {
			this.consolidate(null);
			this.minRoot = this.peekNode();
			consolidated = true;
		}
	}

	private FibonacciHeapEntry consolidate(FibonacciHeapEntry parent) {
		FibonacciHeapEntry entry = parent == null ? this.minRoot : parent
				.child();
		if (entry == null)
			return entry;
		if (entry.rightSibling() == entry)
			return entry;

		int size = this.size();
		// System.out.println("size : " + size);
		int maxDegree = size == 0 ? 1 : (int) (Math.log(size) / Ln_Phi) + 2;
		FibonacciHeapEntry y = null;
		// Buckets
		ArrayList<FibonacciHeapEntry> array = new ArrayList<FibonacciHeapEntry>(
				maxDegree);
		// new root list
		ArrayList<FibonacciHeapEntry> rootList = new ArrayList<FibonacciHeapEntry>();
		for (int i = 0; i < maxDegree; ++i)
			array.add(null);
		FibonacciHeapEntry current = entry.rightSibling();
		rootList.add(entry);
		while (current != entry) {
			rootList.add(current);
			current = current.rightSibling();
		}

		for (int i = 0; i < rootList.size(); ++i) {
			current = rootList.get(i);
			if (current.parent() != parent)
				continue;
			while (array.get(current.degree()) != null) {
				y = array.get(current.degree());
				if (this.compare(y, current) > 0) {
					// swap current and y
					FibonacciHeapEntry temp = current;
					current = y;
					y = temp;
				}
				if (y == entry)
					entry = current;
				this.fibonacciLink(current, y);
				array.set(current.degree() - 1, null);
			}
			array.set(current.degree(), current);
		}

		++modCount;
		return entry;
	}

	private void cutSubHeap(FibonacciHeapEntry root) {
		if (root == null)
			return;
		if (root.leftSibling() == root) {
			if (root.parent() == null) {
				this.minRoot = null;
			} else {
				root.parent.child = null;
				root.parent.degree = 0;
			}
			return;
		} else if (root.parent() != null) {
			if (root.parent.child == root) {
				root.parent.child = root.leftSibling();
			}
			--root.parent.degree;
		}

		root.left.right = root.rightSibling();
		root.right.left = root.leftSibling();
		if (root == this.minRoot) {
			this.minRoot = root.leftSibling();
			this.minRoot = this.peekNode();
		}

		root.left = root;
		root.right = root;
	}

	private FibonacciHeapEntry insertSubHeap(FibonacciHeapEntry parent,
			FibonacciHeapEntry root) {
		FibonacciHeapEntry entry = parent == null ? this.minRoot : parent
				.child();
		if (root == null)
			return entry;

		root.parent = parent;
		if (entry == null) {
			if (parent == null) {
				this.minRoot = root;
				return this.minRoot;
			} else {
				parent.child = root;
				root.left = root.right = root;
				parent.degree = 1;
				return parent.child();
			}
		}

		root.left = entry;
		root.right = entry.rightSibling();
		entry.right.left = root;
		entry.right = root;

		if (parent != null) {
			++parent.degree;
		} else {
			if (this.compare(root, this.minRoot) > 0) {
				this.minRoot = root;
				return this.minRoot;
			}
		}
		this.consolidated = false;
		return entry;
	}

	private void fibonacciLink(FibonacciHeapEntry parent,
			FibonacciHeapEntry child) {
		if (parent == null || child == null)
			return;

		this.cutSubHeap(child);

		// child.parent = parent;
		// FibonacciHeapNode entryChild = parent.child();
		// if(entryChild == null){
		// parent.child = child;
		// child.left = child.right = child;
		// parent.degree = 1;
		// } else {
		// child.left = entryChild;
		// child.right = entryChild.right;
		// entryChild.right.left = child;
		// entryChild.right = child;
		// ++parent.degree;
		// }
		this.insertSubHeap(parent, child);
		child.mark = false;
	}

	private FibonacciHeapEntry peekNode() {
		if (this.isEmpty())
			return null;
		FibonacciHeapEntry peek = this.minRoot, current = this.minRoot
				.leftSibling();
		while (current != this.minRoot) {
			if (this.compare(peek, current) < 0) {
				peek = current;
			}
			current = current.leftSibling();
		}
		return peek;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return this.minRoot == null ? null : this.minRoot.element();
	}

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
		FibonacciHeapEntry index = (FibonacciHeapEntry) this
				.replaceNotHeaplify(oldElement, newElement);
		if (index == null)
			return false;

		if (result < 0) {
			FibonacciHeapEntry parent = index.parent();
			if (parent == null)
				return true;
			else if (this.compare(parent, index) > 0)
				return true;

			this.cutSubHeap(index);
			this.insertSubHeap(null, index);
			index.mark = false;
			this.cascadeCut(parent);
		}
		++modCount;
		return true;
	}

	private void cascadeCut(FibonacciHeapEntry node) {
		FibonacciHeapEntry parent = node.parent();
		if (parent != null) {
			if (node.mark == false) {
				node.mark = true;
			} else {
				this.cutSubHeap(node);
				this.insertSubHeap(null, node);
				node.mark = false;
				this.cascadeCut(parent);
			}
		}
	}

	@Override
	public int size() {
		return (this.nodes == null || this.minRoot == null) ? 0 : this.nodes
				.size();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if (this.nodes != null) {
			this.nodes.clear();
			this.nodes = null;
		}
		this.minRoot = null;
		this.consolidated = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FibonacciHeap<E> clone() {
		// TODO Auto-generated method stub
		FibonacciHeap<E> clone;
		try {
			clone = (FibonacciHeap<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InternalError();
		}

		FibonacciHeapEntry newMinRoot = null, curRoot = null;
		Iterator<HeapEntry<E>> rootIt = this.roots().iterator();
		while (rootIt.hasNext()) {
			FibonacciHeapEntry root = (FibonacciHeapEntry) rootIt.next();
			FibonacciHeapEntry newRoot = clone.cloneBinomialTree(root);
			if (curRoot == null) {
				newMinRoot = newRoot;
			} else {
				newRoot.right = curRoot.right;
				newRoot.left = curRoot;
				curRoot.right.left = newRoot;
				curRoot.right = newRoot;
			}
			curRoot = newRoot;
		}

		clone.minRoot = newMinRoot;
		clone.nodes = clone.getNodeSet(clone.minRoot, null);
		clone.modCount = 0;
		clone.entrys = null;
		clone.roots = null;
		return clone;
	}

	private FibonacciHeapEntry cloneBinomialTree(FibonacciHeapEntry root) {
		FibonacciHeapEntry newRoot = new FibonacciHeapEntry(root.element());
		FibonacciHeapEntry curChild = null;
		Iterator<HeapEntry<E>> childIt = root.children().iterator();
		while (childIt.hasNext()) {
			FibonacciHeapEntry child = (FibonacciHeapEntry) childIt.next();
			FibonacciHeapEntry newChild = cloneBinomialTree(child);
			newChild.parent = newRoot;
			if (curChild == null) {
				newRoot.child = newChild;
			} else {
				newChild.right = curChild.right;
				newChild.left = curChild;
				curChild.right.left = newChild;
				curChild.right = newChild;
			}
			curChild = newChild;
		}
		newRoot.degree = root.degree();
		return newRoot;
	}

	@Override
	public boolean union(FibonacciHeap<E> otherElem) {
		// TODO Auto-generated method stub
		if (otherElem == null)
			return false;
		// this.nodes might be null
		this.nodes = this.getNodeSet(otherElem.minRoot, this.nodes);
		return this.unionInternal(null, otherElem.minRoot);
	}

	private boolean unionInternal(FibonacciHeapEntry parent,
			FibonacciHeapEntry otherEntry) {
		// TODO Auto-generated method stub
		// merge root list
		if (otherEntry == null)
			return false;

		if (this.minRoot == null) {
			if (parent == null) {
				this.minRoot = otherEntry;
			} else {
				parent.child = otherEntry;
			}
		} else {
			FibonacciHeapEntry entry = parent == null ? this.minRoot : parent
					.child();
			FibonacciHeapEntry current = otherEntry.rightSibling();
			current.parent = parent;
			if (parent != null)
				++parent.degree;
			while (current != otherEntry) {
				current.parent = entry.parent();
				if (parent != null)
					++parent.degree;
				current = current.rightSibling();
			}

			FibonacciHeapEntry tail = otherEntry.leftSibling();
			otherEntry.left = entry;
			tail.right = entry.rightSibling();
			entry.right.left = tail;
			entry.right = otherEntry;

			this.consolidated = false;
		}

		++modCount;
		return true;
	}

	private boolean removeEntry(FibonacciHeapEntry node) {
		if (node == null)
			return false;

		this.cutSubHeap(node);

		if (node.child() != null) {
			// insert node's children to root list
			FibonacciHeapEntry child = node.child();
			child.parent = null;
			FibonacciHeapEntry current = child.rightSibling();
			while (current != child) {
				current.parent = null;
				current = current.rightSibling();
			}

			if (this.minRoot == null)
				this.minRoot = child;
			else {
				current = child.leftSibling();
				child.left = this.minRoot;
				current.right = this.minRoot.rightSibling();
				this.minRoot.right.left = current;
				this.minRoot.right = child;
			}
		}

		if (node.parent != null) {
			this.cascadeCut(node.parent());
			this.consolidated = true;
		}
		this.consolidate(null);
		this.minRoot = this.peekNode();
		return true;
	}

	// @Override
	// public String toString(){
	// return this.sort().toString() + " / " + super.toString();
	// }

	private class FibonacciHeapEntry extends AbstractHeapEntry implements
			HeapEntry<E> {
		private E element;
		transient FibonacciHeapEntry parent, child, left, right;
		transient int degree = 0;
		private boolean mark = false;

		private FibonacciHeapEntry(E element) {
			super(Objects.requireNonNull(element));
			this.parent = null;
			this.child = null;
			this.left = this;
			this.right = this;
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
			this.element = element;
			return true;
		}

		@Override
		public FibonacciHeapEntry leftSibling() {
			// TODO Auto-generated method stub
			return this.left;
		}

		@Override
		public FibonacciHeapEntry rightSibling() {
			// TODO Auto-generated method stub
			return this.right;
		}

		@Override
		public FibonacciHeapEntry child() {
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
							private int expectedModCount = FibonacciHeap.this.modCount;
							private HeapEntry<E> next = FibonacciHeapEntry.this
									.child();

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								return next != null;
							}

							private final void checkForComodification() {
								if (expectedModCount != FibonacciHeap.this.modCount)
									throw new ConcurrentModificationException();
							}

							@Override
							public HeapEntry<E> next() {
								// TODO Auto-generated method stub
								checkForComodification();
								if (next == null)
									throw new NoSuchElementException();
								HeapEntry<E> cur = next;
								next = cur.rightSibling();
								if (next == FibonacciHeapEntry.this.child())
									next = null;
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
						return FibonacciHeapEntry.this.degree;
					}

				};
			}
			return children;
		}

		@Override
		public FibonacciHeapEntry parent() {
			// TODO Auto-generated method stub
			return this.parent;
		}

		@Override
		public boolean heaplifyUp() {
			// TODO Auto-generated method stub
			boolean modified = super.heaplifyUp();
			if (modified || this.parent() == null) {
				FibonacciHeap.this.minRoot = FibonacciHeap.this.peekNode();
			}
			return modified;
		}

		@Override
		public boolean heaplifyDown() {
			// TODO Auto-generated method stub
			boolean modified = super.heaplifyDown();
			if (modified || this.parent() == null) {
				FibonacciHeap.this.minRoot = FibonacciHeap.this.peekNode();
			}
			return modified;
		}

		@Override
		public int degree() {
			// TODO Auto-generated method stub
			return this.degree;
		}

	}

	private final class FibonacciHeapIterator implements Iterator<HeapEntry<E>> {
		transient Iterator<FibonacciHeapEntry> entryIt;
		transient FibonacciHeapEntry current = null;

		public FibonacciHeapIterator() {
			// TODO Auto-generated constructor stub
			if (FibonacciHeap.this.nodes == null) {
				this.entryIt = EmptyIterator.<FibonacciHeapEntry> getInstance();
			} else {
				this.entryIt = FibonacciHeap.this.nodes.iterator();
			}
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return entryIt != null && entryIt.hasNext();
		}

		@Override
		public HeapEntry<E> next() {
			// TODO Auto-generated method stub
			this.current = entryIt.next();
			return this.current;
		}

		@Override
		public void remove() {
			if (this.current == null)
				throw new IllegalStateException();
			entryIt.remove();
			FibonacciHeap.this.removeEntry(this.current);
			this.current = null;
		}

	}

	/**
	 * Save the state of the <tt>FibonacciHeap</tt> instance to a stream (that
	 * is, serialize it).
	 * 
	 * @serialData The root count of the heap backing the <tt>FibonacciHeap</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();

		// Write out all elements in the proper order.
		s.writeInt(roots().size());
		if (!this.isEmpty()) {
			Iterator<HeapEntry<E>> iterator = this.roots().iterator();
			while (iterator.hasNext()) {
				this.writeFibonacciTree((FibonacciHeapEntry) iterator.next(), s);
			}
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	private void writeFibonacciTree(FibonacciHeapEntry root,
			java.io.ObjectOutputStream s) throws IOException {
		assert root != null && s != null;
		s.writeObject(root.element);
		s.writeBoolean(root.mark);
		s.writeInt(root.degree);
		Iterator<HeapEntry<E>> childIt = root.children().iterator();
		while (childIt.hasNext()) {
			writeFibonacciTree((FibonacciHeapEntry) childIt.next(), s);
		}
	}

	/**
	 * Reconstitute the <tt>FibonacciHeap</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden stuff
		s.defaultReadObject();

		// Read in roots size
		int roots = s.readInt();

		// Read in all elements in the proper order.
		FibonacciHeapEntry curRoot = null;
		for (int i = 0; i < roots; i++) {
			FibonacciHeapEntry root = this.readFibonacciTree(s);
			if(curRoot == null)
				this.minRoot = root;
			else{
				root.left = curRoot;
				root.right = curRoot.right;
				curRoot.right.left = root;
				curRoot.right = root;
			}
			curRoot = root;
		}
		this.nodes = this.getNodeSet(minRoot, null);
	}

	private FibonacciHeapEntry readFibonacciTree(java.io.ObjectInputStream s)
			throws ClassNotFoundException, IOException {
		assert s != null;
		@SuppressWarnings("unchecked")
		E element = (E) s.readObject();
		FibonacciHeapEntry root = new FibonacciHeapEntry(element);
		root.mark = s.readBoolean();
		root.degree = s.readInt();
		FibonacciHeapEntry curChild = null;
		for (int i = 0; i < root.degree; ++i) {
			FibonacciHeapEntry child = readFibonacciTree(s);
			child.parent = root;
			if (curChild == null) {
				root.child = child;
			} else {
				child.right = curChild.right;
				child.left = curChild;
				curChild.right.left = child;
				curChild.right = child;
			}
			curChild = child;
		}
		return root;
	}

}
