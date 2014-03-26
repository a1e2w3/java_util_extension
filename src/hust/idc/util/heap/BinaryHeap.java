package hust.idc.util.heap;

import hust.idc.util.Sortable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A Complete-Binary-Tree Based implementation of the <tt>Heap</tt> interface, Implements
 * almost all optional heap operations, except remove an element through iterator.remove().
 * And it permits all elements except <tt>null</tt>, since <tt>null</tt> is used as a special return value 
 * by the <tt>poll</tt> method to indicate that the heap contains no elements.<p>
 * 
 * Binary Heap hold all element in a specific order by an array list. Each element in the array list was 
 * seemed as a node in heap, the index of root node was 0. For a node which index is i, its left child 
 * would be held at index (2 * i) + 1 and right child would be(2 * i) + 2, if they existed.<p>
 * 
 * In addition to implementing the <tt>Heap</tt> interface, this class provides methods to manipulate
 * the size of the array list that is used internally to store the element list. <p>
 * 
 * The <tt>size</tt>, <tt>isEmpty</tt>, <tt>peek</tt>, and <tt>iterator</tt> operations run in constant
 * time.  The <tt>offer</tt>, <tt>add</tt>, <tt>poll</tt>, <tt>remove</tt> operation runs in <i>logarithmic time</i>,
 * that is, add(or poll, remove) an elements in the heap contains n elements requires O(log n) time. 
 * similarly, the <tt>contains</tt> operation runs in <i>logarithmic time</i>, too.<p>
 * 
 * <p>The iterators returned by this class's <tt>iterator</tt> method are not support 
 * <tt>iterator.remove</tt> operation, because the order of elements in the heap may be changed once any 
 * element is removed, the iterator cannot ensure the iterative sequence. And the iterators are 
 * <i>fail-fast</i>: if the list is structurally modified at any time after the iterator is created
 * the iterator will throw a {@link ConcurrentModificationException}.
 * 
 * @author WangCong
 * @version 1.2.0, 08/10/13
 * @see	    Collection
 * @see	    Queue
 * @see	    Sortable
 * @see	    Heap
 * @see	    AbstractHeap
 * @since 1.0
 * @param <E> the element type
 */
public class BinaryHeap<E> extends AbstractHeap<E> implements Heap<E> {
	transient ArrayList<E> elements;
	transient final BinaryHeapIndex entry = new BinaryHeapIndex(0);
	
	public BinaryHeap(){
		super();
		// TODO Auto-generated constructor stub
	}
	
	public BinaryHeap(Comparator<? super E> comparator){
		super(comparator);
		// TODO Auto-generated constructor stub
	}
	
	public BinaryHeap(E element, Comparator<? super E> comparator) {
		super(element, comparator);
		// TODO Auto-generated constructor stub
	}

	public BinaryHeap(E element) {
		super(element);
		// TODO Auto-generated constructor stub
	}

	public BinaryHeap(Collection<? extends E> elements){
		super(elements);
		// TODO Auto-generated constructor stub
	}
	
	public BinaryHeap(Collection<? extends E> elements, Comparator<? super E> comparator){
		super(elements, comparator);
		// TODO Auto-generated constructor stub
	}
	
	public BinaryHeap(Heap<E> heap) {
		super(heap);
		// TODO Auto-generated constructor stub
	}

	public BinaryHeap(int initialCompacity){
		super();
		// TODO Auto-generated constructor stub
		this.elements = new ArrayList<E>(initialCompacity);
	}
	
	/* (non-Javadoc)
	 * @see java.util.ArrayLisy#ensureCapacity()
	 */
	public void ensureCapacity(int minCapacity){
		if(this.elements == null){
			this.elements = new ArrayList<E>(minCapacity);
		} else {
			this.elements.ensureCapacity(minCapacity);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayLisy#trimToSize()
	 */
	public void trimToSize(){
		if(this.elements != null)
			this.elements.trimToSize();
	}

	/* (non-Javadoc)
	 * @see hust.idc.util.heap.Heap#rebuild()
	 */
	@Override
	public boolean rebuild(){
		if(this.size() <= 1)
			return false;
		boolean modified = false;
		for(int i = (this.size() << 1); i > 0; --i){
			modified |= new BinaryHeapIndex(i).heaplifyDown();
		}
		if(modified)
			++modCount;
		return modified;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.elements == null ? 0 : this.elements.size();
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		if(this.elements == null)
			return this.emptyIterator();
		else
			return new BinaryHeapIterator();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if(this.elements != null && !this.elements.isEmpty()){
			this.elements.clear();
			++modCount;
		}
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		if(e == null)
			return false;
		if(this.elements == null){
			this.elements = new ArrayList<E>();
		}
		boolean modified = this.elements.add(e);
		if(modified){
			++modCount;
			if(this.size() > 1)
				new BinaryHeapIndex(this.size() - 1).heaplifyUp();
		}
		return modified;
	}
	

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		int size = this.size();
		if(size <= 0)
			return null;
		E element = this.peek();
		if(size == 1){
			this.elements.clear();
		} else {
			this.elements.set(0, this.elements.get(size - 1));
			this.elements.remove(size - 1);
			this.entry().heaplifyDown();
		}
		++modCount;
		return element;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		if(this.isEmpty())
			return false;
		try{
			@SuppressWarnings("unchecked")
			BinaryHeapIndex index = (BinaryHeapIndex) this.indexOf((E) o);
			if(index == null)
				return false;
			this.removeAtIndex(index);
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
	private void removeAtIndex(BinaryHeapIndex index){
		if(index == null || !index.exists())
			return ;
		
		int size = this.size();
		if(size == 1){
			this.clear();
			return ;
		}
		
		E elem = index.element();
		index.set(this.elements.get(size - 1));
		this.elements.remove(size - 1);
		++modCount;
		
		int comp = this.compare(elem, index.element());
		if(comp > 0){
			index.heaplifyDown();
		} else if(comp < 0){
			index.heaplifyUp();
		}
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		if(this.isEmpty())
			return null;
		else
			return this.entry().element();
	}

	@Override
	protected AbstractHeapIndex indexOf(E element) {
		// TODO Auto-generated method stub
		if(this.isEmpty())
			return null;
		else
			return this.find(element, this.entry());
	}

	@Override
	protected BinaryHeapIndex getIndexByReference(E element) {
		// TODO Auto-generated method stub
		if(this.isEmpty())
			return null;
		for(int i = 0; i < this.elements.size(); ++i){
			if(this.elements.get(i) == element)
				return new BinaryHeapIndex(i);
		}
		return null;
	}

	@Override
	protected BinaryHeapIndex entry() {
		// TODO Auto-generated method stub
		if(this.isEmpty())
			return null;
		else
			return this.entry;
	}

	@Override
	protected BinaryHeap<E> copy() {
		// TODO Auto-generated method stub
		BinaryHeap<E> newHeap = new BinaryHeap<E>(this.getComparator());
		newHeap.elements = new ArrayList<E>(this.elements);
		return newHeap;
	}
	
	private class BinaryHeapIndex extends AbstractHeapIndex implements HeapIndex<E> {
		private final int index;
		
		private BinaryHeapIndex(int index){
			this.index = index;
		}

		@Override
		public boolean exists() {
			// TODO Auto-generated method stub
			return index >= 0 && index < BinaryHeap.this.size();
		}

		@Override
		public boolean set(E element) {
			// TODO Auto-generated method stub
			if(element == null)
				return false;
			if(!this.exists())
				return false;
			BinaryHeap.this.elements.set(index, element);
			return true;
		}

		@Override
		public E element() {
			// TODO Auto-generated method stub
			if(this.exists())
				return BinaryHeap.this.elements.get(index);
			else
				throw new NoSuchElementException();
		}

		@Override
		public BinaryHeapIndex parent() {
			// TODO Auto-generated method stub
			if(index > 0)
				return new BinaryHeapIndex((index - 1) >> 1);
			else
				return null;
		}

		@Override
		public BinaryHeapIndex leftSibling() {
			// TODO Auto-generated method stub
			if(index <= 1)
				return null;
			else if(index % 2 == 0)
				return new BinaryHeapIndex(index - 1);
			else
				return null;
		}

		@Override
		public BinaryHeapIndex rightSibling() {
			// TODO Auto-generated method stub
			if(index <= 0)
				return null;
			else if(index % 2 != 0){
				BinaryHeapIndex right = new BinaryHeapIndex(index + 1);
				return right.exists() ? right : null;
			} else 
				return null;
		}

		@Override
		public BinaryHeapIndex child() {
			// TODO Auto-generated method stub
			if(index >= 0){
				BinaryHeapIndex child = new BinaryHeapIndex(((index + 1) << 1) - 1);
				return child.exists() ? child : null;
			}
			else
				return null;
		}

		@Override
		public int degree() {
			// TODO Auto-generated method stub
			BinaryHeapIndex child = this.child();
			if(child == null)
				return 0;
			else 
				return child.rightSibling() == null ? 1 : 2;
		}
		
	}
	
	private final class BinaryHeapIterator implements Iterator<E> {
		transient Iterator<E> it;
		
		private BinaryHeapIterator(){
			this.it = BinaryHeap.this.elements.iterator();
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return it != null && it.hasNext();
		}

		@Override
		public E next() {
			// TODO Auto-generated method stub
			return it.next();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}
		
	}

}
