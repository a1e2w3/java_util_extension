package hust.idc.util.heap;

import hust.idc.util.Mergeable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class BinomialHeap<E> extends AbstractHeap<E> implements Heap<E>,
		Mergeable<BinomialHeap<? extends E>> {
	transient BinomialHeapNode head;
	// Cannot initilize to 0, this statement will be executed after super() in
	// constructor and cover the current value.
	transient int size;

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

	public BinomialHeap(E element, Comparator<? super E> comparator) {
		super(element, comparator);
		// TODO Auto-generated constructor stub
	}

	public BinomialHeap(E element) {
		super(element);
		// TODO Auto-generated constructor stub
	}

	public BinomialHeap(Heap<E> heap) {
		super(heap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean rebuild() {
		// TODO Auto-generated method stub
		BinomialHeap<E> newHeap = new BinomialHeap<E>(this.getComparator());
		Iterator<E> iterator = this.iterator();
		while (iterator.hasNext()) {
			newHeap.offer(iterator.next());
		}
		this.clear();
		this.head = newHeap.head;
		++modCount;
		return true;
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		if (this.isEmpty()) {
			BinomialHeapNode node = new BinomialHeapNode(e);
			this.head = node;
			++modCount;
			++size;
			return true;
		} else {
			BinomialHeapNode newNode = new BinomialHeapNode(e);
			boolean modified = this.unionInternal(newNode);
			if (modified) {
				++size;
				++modCount;
			}
			return modified;
		}
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		BinomialHeapNode peekNode = this.peekNode();
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
		BinomialHeapNode peekNode = this.peekNode();
		return peekNode == null ? null : peekNode.element();
	}

	private BinomialHeapNode peekNode() {
		if (this.head == null)
			return null;
		BinomialHeapNode peekNode = this.head;
		BinomialHeapNode current = this.head.rightSibling();
		while (current != null) {
			if (this.compare(peekNode, current) < 0)
				peekNode = current;
			current = current.rightSibling();
		}
		return peekNode;
	}

	@Override
	public boolean union(BinomialHeap<? extends E> otherElem) {
		// TODO Auto-generated method stub
		if (otherElem == null || otherElem.isEmpty())
			return false;

		BinomialHeapNode newHead = copyHeap(otherElem.head,
				this.new BinomialHeapNode(null));
		boolean modified = this.unionInternal(newHead);
		if (modified) {
			this.size += otherElem.size();
			++modCount;
		}
		return modified;
	}

	private boolean unionInternal(BinomialHeapNode newHead) {
		if (newHead == null)
			return false;

		// merge the head list of two heaps
		this.head = this.mergeHead(newHead);

		BinomialHeapNode current = this.head;
		BinomialHeapNode prev = null;
		BinomialHeapNode next = current.rightSibling();
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
			BinomialHeap<E>.BinomialHeapNode parent,
			BinomialHeap<E>.BinomialHeapNode child) {
		if (parent == null || child == null)
			return;

		child.parent = parent;
		child.sibling = parent.child();
		parent.child = child;
		++parent.degree;
	}

	private BinomialHeapNode mergeHead(BinomialHeapNode otherHead) {
		if (otherHead == null)
			return this.head;
		else if (this.head == null)
			return otherHead;

		BinomialHeapNode newHead, thisCur, otherCur, newCur;
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
	protected AbstractHeap<E> copy() {
		// TODO Auto-generated method stub
		BinomialHeap<E> newHeap = new BinomialHeap<E>(this.getComparator());
		if (this.head == null)
			return newHeap;

		newHeap.head = copyHeap(this.head, newHeap.new BinomialHeapNode(null));
		newHeap.size = this.size();
		return newHeap;
	}

	// private static <E> List<BinomialHeap<E>.BinomialHeapNode>
	// getNodeSet(BinomialHeap<E>.BinomialHeapNode root,
	// List<BinomialHeap<E>.BinomialHeapNode> nodeSet) {
	// if (root == null)
	// return null;
	// if (nodeSet == null)
	// nodeSet = new ArrayList<BinomialHeap<E>.BinomialHeapNode>();
	// BinomialHeap<E>.BinomialHeapNode current = root, next = null;
	// nodeSet.add(current);
	// while (current != null) {
	// next = current.child();
	// if (next != null) {
	// current = next;
	// nodeSet.add(current);
	// continue;
	// }
	// next = current.rightSibling();
	// if (next != null) {
	// current = next;
	// nodeSet.add(current);
	// continue;
	// } else {
	// BinomialHeap<E>.BinomialHeapNode ancestor = current.parent();
	// boolean found = false;
	// while (ancestor != null) {
	// next = ancestor.rightSibling();
	// if (next != null) {
	// current = next;
	// nodeSet.add(current);
	// found = true;
	// break;
	// }
	// ancestor = ancestor.parent();
	// }
	// if (found) {
	// continue;
	// }
	// current = null;
	// }
	// }
	//
	// return nodeSet;
	// }

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		try {
			@SuppressWarnings("unchecked")
			BinomialHeapNode node = (BinomialHeapNode) this.indexOf((E) o);
			if (node == null) {
				return false;
			} else {
				BinomialHeapNode removedNode = this.removeElementInNode(node);
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
	protected AbstractHeapIndex indexOf(E element) {
		// TODO Auto-generated method stub
		if (this.isEmpty())
			return null;
		AbstractHeapIndex index = null;
		BinomialHeapNode root = this.head;
		while (root != null) {
			index = this.find(element, root);
			if (index != null)
				return index;
			root = root.rightSibling();
		}
		return index;
	}

	@Override
	protected BinomialHeapNode getIndexByReference(E element) {
		// TODO Auto-generated method stub
		if (this.isEmpty())
			return null;
		BinomialHeapNode root = this.entry();
		BinomialHeapNode index = null;
		while (root != null) {
			index = this.getIndexByReference(root, element);
			if (index != null)
				return index;
			root = root.rightSibling();
		}
		return null;
	}

	private BinomialHeapNode getIndexByReference(BinomialHeapNode root,
			E element) {
		if (root == null)
			return null;
		if (element == root.element())
			return root;
		BinomialHeapNode index = null;
		Iterator<? extends AbstractHeapIndex> childrenIt = root.childIterator();
		while (childrenIt.hasNext()) {
			@SuppressWarnings("unchecked")
			BinomialHeapNode child = (BinomialHeapNode) childrenIt.next();
			index = this.getIndexByReference(child, element);
			if (index != null)
				return index;
		}
		return null;
	}

	@Override
	protected BinomialHeapNode entry() {
		// TODO Auto-generated method stub
		return this.head;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		if (this.isEmpty())
			return this.emptyIterator();
		return new BinomialHeapIterator();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.head == null ? 0 : this.size;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		BinomialHeapNode current = this.head;
		while (current != null) {
			BinomialHeapNode next = current.rightSibling();
			clearSubHeap(current);
			current = next;
		}

		this.head = null;
		this.size = 0;
		++modCount;
	}

	private void clearSubHeap(BinomialHeapNode root) {
		if (root == null)
			return;

		BinomialHeapNode child = root.child();
		while (child != null) {
			BinomialHeapNode next = child.rightSibling();
			clearSubHeap(child);
			child = next;
		}
		root.dispose();
	}

	/**
	 * remove the element in node instead of the node itself
	 * 
	 * @param node
	 * @return
	 */
	private BinomialHeapNode removeElementInNode(BinomialHeapNode node) {
		if (node == null)
			return null;

		// bubble up the node to the root
		BinomialHeapNode current = node;
		BinomialHeapNode parent = current.parent();
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
		BinomialHeapNode currentChild = current.child();

		if (currentChild != null) {
			currentChild.parent = null;

			BinomialHeapNode next = currentChild.rightSibling();
			while (next != null) {
				next.parent = null;

				BinomialHeapNode nextRight = next.rightSibling();
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

	/**
	 * 
	 * @param head
	 * @param newHead
	 * @return
	 */
	private static <E> BinomialHeap<E>.BinomialHeapNode copyHeap(
			BinomialHeap<? extends E>.BinomialHeapNode head,
			BinomialHeap<E>.BinomialHeapNode newHead) {
		if (head == null || newHead == null)
			return null;

		BinomialHeap<E>.BinomialHeapNode newNode = newHead.copyNode();
		copySubHeap(head, newHead);

		BinomialHeap<? extends E>.BinomialHeapNode current = head
				.rightSibling();
		BinomialHeap<E>.BinomialHeapNode newCurrent = newHead;
		while (current != null) {
			BinomialHeap<E>.BinomialHeapNode newRoot = copySubHeap(current,
					newNode.copyNode());
			newCurrent.sibling = newRoot;

			current = current.rightSibling();
			newCurrent = newRoot;
		}

		return newHead;
	}

	private static <E> BinomialHeap<E>.BinomialHeapNode copySubHeap(
			BinomialHeap<? extends E>.BinomialHeapNode root,
			BinomialHeap<E>.BinomialHeapNode newRoot) {
		if (root == null || newRoot == null)
			return null;
		newRoot.parent = null;
		if (root.child() != null) {
			newRoot.child = copySubHeap(root.child(), newRoot.copyNode());
			newRoot.child.parent = newRoot;
			BinomialHeap<? extends E>.BinomialHeapNode current = root.child()
					.rightSibling();
			BinomialHeap<E>.BinomialHeapNode newCurrent = newRoot.child();
			while (current != null) {
				BinomialHeap<E>.BinomialHeapNode newChild = copySubHeap(
						current, newRoot.copyNode());
				newChild.parent = newRoot;
				newCurrent.sibling = newChild;
				newCurrent = newChild;
				current = current.rightSibling();
			}
		}

		// shallow copy of element,
		// if want to deep copy the element, clone the element in method
		// BinomialHeapNode.element() or BinomialHeapNode.set()
		newRoot.set(root.element());
		newRoot.degree = root.degree();
		return newRoot;
	}

	private class BinomialHeapNode extends AbstractHeapIndex implements
			HeapIndex<E> {
		E element;
		transient BinomialHeapNode parent = null, child = null, sibling = null;
		transient int degree = 0;

		private BinomialHeapNode(E element) {
			this.set(element);
		}

		private BinomialHeapNode copyNode() {
			return new BinomialHeapNode(this.element());
		}

		private void dispose() {
			this.set(null);
			this.parent = null;
			this.child = null;
			this.sibling = null;
			this.degree = 0;
		}

		// private BinomialHeapNode copySubHeap(BinomialHeapNode parent) {
		// BinomialHeapNode node = new BinomialHeapNode(this.element());
		// node.parent = parent;
		// node.child = (this.child() == null ? null : this.child
		// .copySubHeap(node));
		// node.sibling = (this.rightSibling() == null ? null : this.sibling
		// .copySubHeap(node.parent()));
		// node.degree = this.degree();
		// return node;
		// }

		@Override
		public boolean exists() {
			// TODO Auto-generated method stub
			BinomialHeapNode root = this.getRoot();
			BinomialHeapNode rootCur = BinomialHeap.this.head;
			while (rootCur != null && rootCur != root) {
				rootCur = rootCur.rightSibling();
			}
			return root == rootCur;
		}

		private BinomialHeapNode getRoot() {
			BinomialHeapNode ancestor = this;
			while (ancestor.parent() != null) {
				ancestor = ancestor.parent();
			}
			return ancestor;
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
		public BinomialHeapNode parent() {
			// TODO Auto-generated method stub
			return this.parent;
		}

		@Override
		public BinomialHeapNode leftSibling() {
			// TODO Auto-generated method stub
			BinomialHeapNode sibling = null;
			if (this.parent() == null) {
				if ((sibling = BinomialHeap.this.head) == null)
					return null;
			} else {
				if ((sibling = this.parent().child()) == null)
					throw new IllegalStateException("Heap Damaged!");
			}

			if (this == sibling)
				return null;

			BinomialHeapNode nextSibling = sibling.rightSibling();
			while (nextSibling != null && this != nextSibling) {
				sibling = nextSibling;
				nextSibling = sibling.rightSibling();
			}
			if (nextSibling == null)
				throw new IllegalStateException("Heap Damaged!");
			return sibling;
		}

		@Override
		public BinomialHeapNode rightSibling() {
			// TODO Auto-generated method stub
			return this.sibling;
		}

		@Override
		public BinomialHeapNode child() {
			// TODO Auto-generated method stub
			return this.child;
		}

		// protected BinomialHeapNode heaplifyAtRoot() {
		// if (!this.exists())
		// return this;
		//
		// BinomialHeapNode largest = this;
		// BinomialHeapNode child = this.child();
		// while (child != null) {
		// if (BinomialHeap.this.compare(child, largest) > 0) {
		// largest = child;
		// }
		// child = child.rightSibling();
		// }
		//
		// if (!largest.equals(this)) {
		// this.exchangeElementWith(largest);
		// }
		// return largest;
		// }

		@Override
		public int degree() {
			// TODO Auto-generated method stub
			return this.degree;
		}
	}

	private class BinomialHeapIterator extends HeapPreOrderIterator implements
			Iterator<E> {
		private BinomialHeapIterator() {
			super();
		}

		@Override
		protected boolean removeCurrent() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

	}

}
