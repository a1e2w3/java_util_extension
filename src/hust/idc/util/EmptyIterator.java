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
