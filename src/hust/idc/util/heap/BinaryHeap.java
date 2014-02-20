package hust.idc.util.heap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BinaryHeap<E> extends AbstractHeap<E> {
	private ArrayList<E> elements;
	private final BinaryHeapIndex entry = new BinaryHeapIndex(0);
	
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
	
	public void ensureCapacity(int minCapacity){
		if(this.elements == null){
			this.elements = new ArrayList<E>(minCapacity);
		} else {
			this.elements.ensureCapacity(minCapacity);
		}
	}

	public void trimToSize(){
		if(this.elements != null)
			this.elements.trimToSize();
	}

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
	
	private class BinaryHeapIndex extends AbstractHeapIndex{
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
	
	private class BinaryHeapIterator implements Iterator<E>{
		private Iterator<E> it;
		
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
