package hust.idc.util.heap;

import hust.idc.util.EmptyIterator;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * This class provides skeletal implementations of some {@link Collection Collection}, 
 * {@link java.util.Queue Queue} and {@link Heap} operations. The implementations in this class 
 * are appropriate when the base implementation does <em>not</em> allow <tt>null</tt>
 * elements.  Like the super class <tt>AbstractQueue</tt> suggested, methods {@link #add add}, 
 * {@link #remove remove}, and {@link #element element} are based on {@link #offer offer}, {@link
 * #poll poll}, and {@link #peek peek}, respectively but throw exceptions instead of indicating 
 * failure via <tt>false</tt> or <tt>null</tt> returns.
 *
 * <p> A <tt>Heap</tt> implementation that extends this class must
 * minimally define a method {@link Queue#offer} which does not permit
 * insertion of <tt>null</tt> elements, along with methods {@link
 * Queue#peek}, {@link Queue#poll}, {@link Collection#size}, {@link Collection#remove}, {@link Heap#rebuild} and a
 * {@link Collection#iterator} supporting {@link Iterator#remove}. 
 * Typically, additional methods will be overridden as well. If these requirements cannot be met, 
 * consider instead subclassing {@link AbstractQueue} or {@link java.util.AbstractCollection AbstractCollection}.
 * 
 * <p> This class used an Object named {@linked HeapIndex} to locate an element in the heap. Correspondingly, a method
 * to obtain an index was needed.   A <tt>Heap</tt> implementation that extends this class must define several abstract methods 
 * defined in this class based on its typical method of organizing elements.  {@link #indexOf} to return the index of a element in a typical heap.  
 * {@link #entry()} to return the entry index of the heap.
 * 
 * <p> In addition,  A <tt>Heap</tt> implementation that extends this class must define {@link #copy()} method to support the {@link #clone()} and {@link #sort()}. 
 * The <tt>copy()</tt> method used to copy a typical heap and return a new instance.
 * 
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.2.0
 * @author Wang Cong
 *
 * @param <E> the type of elements held in this collection
 */
public abstract class AbstractHeap<E> extends AbstractQueue<E> implements Heap<E>, Cloneable {
	transient Comparator<? super E> comparator = null;
	
	transient volatile int modCount = 0;
	
	public AbstractHeap(){
		super();
		this.setComparator(null);
	}
	
	public AbstractHeap(Comparator<? super E> comparator){
		super();
		this.setComparator(comparator);
	}
	
	public AbstractHeap(E element){
		this();
		if(element != null)
			this.offer(element);
	}
	
	public AbstractHeap(E element, Comparator<? super E> comparator){
		this(comparator);
		if(element != null)
			this.offer(element);
	}
	
	public AbstractHeap(Collection<? extends E> elements){
		this();
		if(elements != null)
			this.addAll(elements);
	}
	
	public AbstractHeap(Collection<? extends E> elements, Comparator<? super E> comparator){
		this(comparator);
		if(elements != null)
			this.addAll(elements);
	}
	
	public AbstractHeap(Heap<E> heap){
		this(heap, null);
		if(heap != null)
			this.setComparator(heap.getComparator());
		else
			this.setComparator(null);
	}

	
	@Override
	public AbstractHeap<E> clone() {
		// TODO Auto-generated method stub
		return this.copy();
	}
	
	/**
	 * copy the structure of heap to a new heap, do not deep clone
	 * the elements contains.
	 * @return the copy of the heap
	 */
	protected abstract AbstractHeap<E> copy();

	/* (non-Javadoc)
	 * @see hust.idc.util.heap.Heap#rebuild()
	 */
	@Override
	public void setComparator(Comparator<? super E> comparator) {
		// TODO Auto-generated method stub
		this.comparator = comparator;
	}

	/* (non-Javadoc)
	 * @see hust.idc.util.heap.Heap#rebuild()
	 */
	@Override
	public Comparator<? super E> getComparator() {
		// TODO Auto-generated method stub
		return this.comparator;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		if(o == null)
			return false;
		try{
			return this.indexOf((E) o) != null;
		} catch(Throwable th){
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(Object)
	 */
	@Override
	public abstract boolean remove(Object o);
	
	/**
	 * @Override
	 * Replace the old element with the new element. 
	 * If new element compare old element return 0, check oldElement.equals(newElement),
	 * if not equal, replace the element directly, else return <tt>false</tt> directly.
	 * Else if new element is greater, call {@link #increaseElement(Object, Object)}. 
	 * Else, new element is less, call {@link #decreaseElement(Object, Object)}.
	 * 
	 * @param oldElement the old element to be replaced
	 * @param newElement the new element to replace the old one
	 * @return <tt>true</tt> if the element changed.
	 * 			
	 */
	public boolean replace(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		int result = this.compare(oldElement, newElement);
		if(0 == result){
			if(oldElement.equals(newElement))
				return false;
			else
				return this.replaceNotHeaplify(oldElement, newElement) != null;
		} else if(0 > result){
			return this.increaseElement(oldElement, newElement);
		} else{
			return this.decreaseElement(oldElement, newElement);
		}
	}
	
	/* (non-Javadoc)
	 * @see hust.idc.util.heap.Heap#increaseElement(E, E)
	 */
	@Override
	public boolean increaseElement(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		int result = this.compare(oldElement, newElement);
		if(result > 0){
			throw new IllegalArgumentException("new element is less than old element");
		} else if(result == 0){
			if(oldElement.equals(newElement))
				return false;
		}
		HeapIndex<E> index = this.replaceNotHeaplify(oldElement, newElement);
		if(index == null)
			return false;
		
		if(result < 0)
			index.heaplifyUp();
		return true;
	}

	/* (non-Javadoc)
	 * @see hust.idc.util.heap.Heap#decreaseElement(E, E)
	 */
	@Override
	public boolean decreaseElement(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		int result = this.compare(oldElement, newElement);
		if(result < 0){
			throw new IllegalArgumentException("new element is greater than old element");
		} else if(result == 0){
			if(oldElement.equals(newElement))
				return false;
		}
		HeapIndex<E> index = this.replaceNotHeaplify(oldElement, newElement);
		if(index == null)
			return false;
		
		if(result > 0)
			index.heaplifyDown();
		return true;
	}
	
	/* (non-Javadoc)
	 * @see hust.idc.util.heap.Heap#heaplifyAt(E)
	 */
	@Override
	public boolean heaplifyAt(E element) throws NoSuchElementException {
		HeapIndex<E> index = this.getIndexByReference(element);
		if(index == null)
			throw new NoSuchElementException("No such element in heap: " + element);
		
		if(index.heaplifyUp())
			return true;
		return index.heaplifyDown();
	}
	
	/**
	 * Get Heap Index of the reference of the element, using "==" to decide 
	 * if it was the one we need
	 * @param element the reference of the element
	 * @return <tt>null</tt> if not found, else return the actual index of the element
	 */
	protected abstract AbstractHeapIndex getIndexByReference(E element);

	/**
	 * Found the position of old element and replace it with the new element, but not change the
	 * structure of the heap.
	 * @param oldElement the old element to be replaced
	 * @param newElement the new element to replace the old one
	 * @return <tt>null</tt> if the new element was <tt>null</tt> or the old element was not found in heap;
	 * 		   else return the actual position of the replacement occurred.
	 */
	protected HeapIndex<E> replaceNotHeaplify(E oldElement, E newElement) {
		// TODO Auto-generated method stub
		if(newElement == null)
			return null;
		HeapIndex<E> index = this.indexOf(oldElement);
		if(index != null && index.exists()){
			index.set(newElement);
			++modCount;
		}
		return index;
	}
	
	/**
	 * Found the position of an element in the heap. using <tt>equal</tt> method but not "==" to 
	 * decide whether two element were equal.
	 * @param element the element try to found
	 * @return <tt>null</tt> if not found; else return the actual index of the element
	 */
	protected abstract HeapIndex<E> indexOf(E element);
	
	/**
	 * Get the entry of the heap, that is an index of an element 
	 * which can visit any other index through it
	 * @return <tt>null</tt>if the heap is empty; else return the entry.
	 */
	protected abstract HeapIndex<E> entry();
	
	/**
	 * Find the position of the element in a tree 
	 * @param element the element to find
	 * @param root the root position of the tree
	 * @return <tt>null</tt> if the root was <tt>null</tt> or element not found;
	 * 		   else return the actual index of the element
	 */
	protected AbstractHeapIndex find(E element, AbstractHeapIndex root){
		if(element == null || root == null)
			return null;
		
		int result = this.compare(element, root.element());
		if(result > 0)
			return null;
		if(result == 0){
			if(element.equals(root.element()))
				return root;
		}
		
		Iterator<? extends AbstractHeapIndex> childIt = root.childIterator();
		while(childIt.hasNext()){
			AbstractHeapIndex index = this.find(element, childIt.next());
			if(index != null)
				return index;
		}
		return null;
	}

//	@Override
//	public boolean addAll(Collection<? extends E> c) {
//		// TODO Auto-generated method stub
//		boolean modified = super.addAll(c);
//		if(modified)
//			this.rebuild();
//		return modified;
//	}
//
//	@Override
//	public boolean removeAll(Collection<?> c) {
//		// TODO Auto-generated method stub
//		boolean modified = super.removeAll(c);
//		if(modified)
//			this.rebuild();
//		return modified;
//	}
//
//	@Override
//	public boolean retainAll(Collection<?> c) {
//		// TODO Auto-generated method stub
//		boolean modified = super.retainAll(c);
//		if(modified){
//			this.rebuild();
//		}
//		return modified;
//	}

	/**
	 * Get sorted elements of the heap
	 * @return the list contains the sorted elements
	 */
	@Override
	public List<E> sort() {
		// TODO Auto-generated method stub
		return this.copy().sortAndClear();
	}

	/* (non-Javadoc)
	 * @see hust.idc.util.heap.Heap#sortAndClear()
	 */
	@Override
	public List<E> sortAndClear() {
		// TODO Auto-generated method stub
		List<E> sorted = new ArrayList<E>(this.size());
		while(!this.isEmpty()){
			sorted.add(this.poll());
		}
		return sorted;
	}

	/**
	 * Compare two elements using the user defined comparator.
	 * @param elem1
	 * @param elem2
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	protected int compare(E elem1, E elem2){
		if(this.comparator == null){
			try {
				if(elem1 != null && elem1 instanceof Comparable<?>){
					return ((Comparable<E>) elem1).compareTo(elem2);
				} else if(elem2 != null && elem2 instanceof Comparable<?>){
					return 0 - ((Comparable<E>) elem2).compareTo(elem1);
				} else {
					return 0;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return 0;
			}
		}
		else
			return this.comparator.compare(elem1, elem2);
	}
	
	/**
	 * Compare two elements contains in heap index
	 * @param i1
	 * @param i2
	 * @return
	 */
	protected int compare(HeapIndex<? extends E> i1, HeapIndex<? extends E> i2){
		if(i1 == null){
			return i2 == null ? 0 : -1;
		} else if(i2 == null){
			return 1;
		} else {
			return this.compare(i1.element(), i2.element());
		}
	}
	
	/**
	 * @return An Iterator of empty collection
	 * @see EmptyIterator
	 */
	protected Iterator<E> emptyIterator(){
		return new EmptyIterator<E>();
	}
	
	/**
	 * Base class for Iterators for Heaps
	 * Using preorder tree traversal method to iterate the element in a heap
	 * @author Wang Cong
	 *
	 */
	abstract class HeapPreOrderIterator implements Iterator<E>{
		protected HeapIndex<E> current = null;
		protected HeapIndex<E> next;
		private int expectedModCount = modCount;
		protected boolean removed = false;
		
		protected HeapPreOrderIterator(){
			next = AbstractHeap.this.entry();
		}
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return next != null;
		}

		@Override
		public E next() {
			// TODO Auto-generated method stub
			checkForComodification();
			if(next == null){
				throw new NoSuchElementException();
			}
			
			removed = false;
			current = next;
			next = this.getNext();
			return current.element();
		}

		@Override
		public void remove(){
			if(current == null || removed){
				throw new IllegalStateException();
			}
			removed = this.removeCurrent();
		}
		
		protected abstract boolean removeCurrent();
		
		protected HeapIndex<E> getNext(){
			if(current == null){
				return AbstractHeap.this.entry();
			} else{
				HeapIndex<E> index = this.leftChild();
				if(index != null){
					return index;
				} 
				index = current.rightSibling();
				if(index != null){
					return index;
				} else {
					HeapIndex<E> ancestor = current.parent();
					while(ancestor != null){
						index = ancestor.rightSibling();
						if(index != null){
							return index;
						}
						ancestor = ancestor.parent();
					}
					return null;
				}
			}
		}
		
		protected HeapIndex<E> leftChild(){
			if(current == null)
				return null;
			return current.child();
		}
		
		final void checkForComodification() {
		    if (modCount != expectedModCount)
		    	throw new ConcurrentModificationException();
		}
	}
	
	/**
	 * Base class for Heap Index
	 * @author Wang Cong
	 *
	 */
	abstract class AbstractHeapIndex implements HeapIndex<E>{
		protected boolean exchangeElementWith(HeapIndex<E> otherIndex){
			if(otherIndex == null || !this.exists() || !otherIndex.exists())
				return false;
			if(otherIndex.equals(this))
				return true;
			
			E temp = this.element();
			this.set(otherIndex.element());
			otherIndex.set(temp);
			return true;
		}
		
		protected int subHeapSize(){
			int size = 1;
			Iterator<? extends AbstractHeapIndex> childIt = this.childIterator();
			while(childIt.hasNext()){
				size += childIt.next().subHeapSize();
			}
			return size;
		}
		
		@Override
		public abstract AbstractHeapIndex parent();

		@Override
		public abstract AbstractHeapIndex child();
		
		@Override
		public abstract AbstractHeapIndex leftSibling();
		
		@Override
		public abstract AbstractHeapIndex rightSibling();
		
//		@Override
//		public abstract int degree();
		
		protected Iterator<? extends AbstractHeapIndex> childIterator(){
			return new DefaultChildIterator();
		}

		@Override
		public boolean heaplifyUp() {
			// TODO Auto-generated method stub
			if(!this.exists())
				return false;
			
			AbstractHeapIndex parent = this.parent();
			if(parent == null)
				return false;
			AbstractHeapIndex largest = parent.heaplifyAtRoot();
			if(!largest.equals(parent)){
				parent.heaplifyUp();
				return true;
			}
			return false;
		}

		@Override
		public boolean heaplifyDown() {
			// TODO Auto-generated method stub
			if(!this.exists())
				return false;
			
			AbstractHeapIndex largest = this.heaplifyAtRoot();
			if(!largest.equals(this)){
				largest.heaplifyDown();
				return true;
			}
			return false;
		}
		
		protected AbstractHeapIndex heaplifyAtRoot(){
			if(!this.exists())
				return this;
			
			AbstractHeapIndex largest = this;
			
			// find largest index in children
			Iterator<? extends AbstractHeapIndex> it = this.childIterator();
			while(it.hasNext()){
				AbstractHeapIndex current = it.next();
				if(AbstractHeap.this.compare(current, largest) > 0){
					largest = current;
				}
			}
			
			if(!largest.equals(this)){
				this.exchangeElementWith(largest);
			}
			return largest;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("[Element : ");
			builder.append(this.element().toString());
			builder.append(", parent : ");
			AbstractHeapIndex parent = this.parent();
			builder.append(parent == null ? "null" : parent.element().toString());
			builder.append(", child : ");
			AbstractHeapIndex child = this.child();
			builder.append(child == null ? "null" : child.element().toString());
			builder.append(", left : ");
			AbstractHeapIndex left = this.leftSibling();
			builder.append(left == null ? "null" : left.element().toString());
			builder.append(", right : ");
			AbstractHeapIndex right = this.rightSibling();
			builder.append(right == null ? "null" : right.element().toString());
			builder.append("]");
			return builder.toString();
		}

		private class DefaultChildIterator implements Iterator<AbstractHeapIndex>{
			private AbstractHeapIndex nextChild, entryChild;
			private boolean mark = true;
			
			private DefaultChildIterator(){
				this.entryChild = AbstractHeapIndex.this.child();
				this.nextChild = entryChild;
			}

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return nextChild != null;
			}

			@Override
			public AbstractHeapIndex next() {
				// TODO Auto-generated method stub
				if(nextChild == null){
					throw new NoSuchElementException();
				}
				
				AbstractHeapIndex current = nextChild;
				if(mark){
					nextChild = current.rightSibling();
					if(nextChild == null){
						nextChild = entryChild.leftSibling();
						mark = false;
						if(nextChild == entryChild){
							nextChild = null;
						}
					} else if(nextChild == entryChild){
						nextChild = null;
					}
				} else {
					nextChild = current.leftSibling();
					if(nextChild == entryChild){
						nextChild = null;
					}
				}
				return current;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException();
			}
			
		}
		
	}

}
