package hust.idc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Empty Iterators for Collections
 * @author Wang Cong
 *
 * @param <E>
 */
public class EmptyIterator<E> implements Iterator<E> {
	@SuppressWarnings({ "rawtypes" })
	private static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();
	
	private EmptyIterator(){
		super();
	}
	
	@SuppressWarnings("unchecked")
	public static final <E> EmptyIterator<E> getInstance(){
		return (EmptyIterator<E>) EMPTY_ITERATOR;
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E next() {
		// TODO Auto-generated method stub
		throw new NoSuchElementException(); 
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new IllegalStateException();
	}
}
