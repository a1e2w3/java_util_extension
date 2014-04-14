package hust.idc.util.heap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Heaps {
	private Heaps() {
	}

	public static <E> Heap<E> synchronizedHeap(Heap<E> heap) {
		return new SynchronizedHeap<E>(heap);
	}

	static <E> Heap<E> synchronizedHeap(Heap<E> heap, Object mutex) {
		return new SynchronizedHeap<E>(heap, mutex);
	}

	static class SynchronizedHeap<E> implements Heap<E>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1677871718453984661L;
		final Heap<E> heap;
		final Object mutex;

		SynchronizedHeap(Heap<E> h) {
			if (h == null)
				throw new NullPointerException();
			this.heap = h;
			this.mutex = this;
		}

		SynchronizedHeap(Heap<E> h, Object mutex) {
			if (h == null)
				throw new NullPointerException();
			this.heap = h;
			this.mutex = mutex;
		}

		@Override
		public List<E> sort() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.sort();
			}
		}

		@Override
		public boolean rebuild() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.rebuild();
			}
		}

		@Override
		public boolean offer(E e) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.offer(e);
			}
		}

		@Override
		public E poll() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.poll();
			}
		}

		@Override
		public E peek() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.peek();
			}
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.remove(o);
			}
		}

		@Override
		public Iterator<E> iterator() {
			// TODO Auto-generated method stub
			return heap.iterator();
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.size();
			}
		}

		public boolean add(E e) {
			if (offer(e))
				return true;
			else
				throw new IllegalStateException("Queue full");
		}

		public E remove() {
			E x = poll();
			if (x != null)
				return x;
			else
				throw new NoSuchElementException();
		}

		public E element() {
			E x = peek();
			if (x != null)
				return x;
			else
				throw new NoSuchElementException();
		}

		public boolean addAll(Collection<? extends E> c) {
			synchronized (mutex) {
				return heap.addAll(c);
			}
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.isEmpty();
			}
		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.contains(o);
			}
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.toArray();
			}
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.toArray(a);
			}
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.containsAll(c);
			}
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.removeAll(c);
			}
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.retainAll(c);
			}
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				heap.clear();
			}
		}

		@Override
		public void setComparator(Comparator<? super E> comparator) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				heap.setComparator(comparator);
			}
		}

		@Override
		public Comparator<? super E> getComparator() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.getComparator();
			}
		}

		@Override
		public boolean replace(E oldElement, E newElement) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.replace(oldElement, newElement);
			}
		}

		@Override
		public boolean increaseElement(E oldElement, E newElement) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.increaseElement(oldElement, newElement);
			}
		}

		@Override
		public boolean decreaseElement(E oldElement, E newElement) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.decreaseElement(oldElement, newElement);
			}
		}

		@Override
		public boolean heaplifyAt(E element) throws NoSuchElementException {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.heaplifyAt(element);
			}
		}

		@Override
		public List<E> sortAndClear() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.sortAndClear();
			}
		}

        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {s.defaultWriteObject();}
        }
	}

}
