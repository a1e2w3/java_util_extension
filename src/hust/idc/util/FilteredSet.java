package hust.idc.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class FilteredSet<E> extends AbstractSet<E> implements Collection<E>,
		Set<E> {
	final Set<E> set;
	final Filter<? super E> filter;

	public FilteredSet(Set<E> set, Filter<? super E> filter) {
		super();
		this.set = Objects.requireNonNull(set);
		this.filter = filter;
	}

	@SuppressWarnings("unchecked")
	boolean accept(Object element) {
		try {
			return this.filter == null || this.filter.accept((E) element);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new Iterator<E>() {
			Iterator<E> it = set.iterator();
			E next = null;
			boolean removable = false;

			private void advance() {
				while (it.hasNext()) {
					if (FilteredSet.this.filter.accept(next = it.next())) {
						removable = true;
						return;
					}
				}
				next = null;
				removable = false;
			}

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				if (next != null)
					return true;
				else
					advance();
				return next != null;
			}

			@Override
			public E next() {
				// TODO Auto-generated method stub
				if (next == null)
					advance();
				if (next == null)
					throw new NoSuchElementException();
				E current = next;
				next = null;
				return current;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				if (!removable)
					throw new IllegalStateException();
				it.remove();
				removable = false;
			}
		};
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		if (this.filter == null)
			return this.set.size();

		int count = 0;
		for (Iterator<E> it = this.set.iterator(); it.hasNext();) {
			if (this.accept(it.next()))
				++count;
		}
		return count;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if (this.filter == null) {
			this.set.clear();
		} else {
			for (Iterator<E> it = this.set.iterator(); it.hasNext();) {
				if (this.accept(it.next()))
					it.remove();
			}
		}
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		if (this.filter == null)
			return this.set.isEmpty();
		Iterator<E> it = this.set.iterator();
		while (it.hasNext()) {
			if (this.accept(it.next()))
				return false;
		}
		return true;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return this.accept(o) && this.set.contains(o);
	}

	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		return this.accept(e) && this.set.add(e);
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return this.accept(o) && this.set.remove(o);
	}

}
