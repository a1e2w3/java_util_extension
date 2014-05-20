package hust.idc.util.heap;

import hust.idc.util.Mergeable;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SkewHeap<E> extends AbstractHeap<E> implements Heap<E>,
		Mergeable<SkewHeap<E>> {
	transient SkewHeapEntry root;
	transient int size;
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
	public Collection<HeapEntry<E>> entrys() {
		// TODO Auto-generated method stub
		if (entrys == null) {
			entrys = new AbstractCollection<HeapEntry<E>>() {

				@Override
				public Iterator<HeapEntry<E>> iterator() {
					// TODO Auto-generated method stub
					return new SkewEntryIterator();
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
	public Collection<HeapEntry<E>> roots() {
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
	
	private class SkewEntryIterator extends HeapPreorderIterator {
		private int expectedModCount = SkewHeap.this.modCount;
		
		private SkewEntryIterator(){
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
			return super.removeCurrent();
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

	private class SkewHeapEntry extends AbstractHeapEntry {
		private E element;
		private SkewHeapEntry parent;
		private SkewHeapEntry leftChild, rightChild;

		private SkewHeapEntry(E element) {
			super();
			this.element = element;
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
		public AbstractHeapEntry parent() {
			// TODO Auto-generated method stub
			return parent;
		}

		@Override
		public HeapEntry<E> child() {
			// TODO Auto-generated method stub
			return leftChild == null ? rightChild : leftChild;
		}

		@Override
		public AbstractHeapEntry leftSibling() {
			// TODO Auto-generated method stub
			if (this.parent == null || this == parent.leftChild)
				return null;
			return parent.leftChild;
		}

		@Override
		public AbstractHeapEntry rightSibling() {
			// TODO Auto-generated method stub
			if (this.parent == null || this == parent.rightChild)
				return null;
			return parent.rightChild;
		}

		@Override
		public boolean set(E element) {
			// TODO Auto-generated method stub
			return super.set(element);
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
							private int expectedModCount = SkewHeap.this.modCount;
							private boolean inLeft = (SkewHeapEntry.this.leftChild != null);
							private HeapEntry<E> next = SkewHeapEntry.this
									.child();

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								return next != null;
							}

							private final void checkForComodification() {
								if (expectedModCount != SkewHeap.this.modCount)
									throw new ConcurrentModificationException();
							}

							@Override
							public HeapEntry<E> next() {
								// TODO Auto-generated method stub
								checkForComodification();
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
						if (SkewHeapEntry.this.leftChild == null)
							return SkewHeapEntry.this.rightChild == null ? 0
									: 1;
						else
							return SkewHeapEntry.this.rightChild == null ? 1
									: 2;
					}

				};
			}
			return children;
		}

	}

}
