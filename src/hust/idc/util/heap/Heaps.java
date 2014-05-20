package hust.idc.util.heap;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class Heaps {
	private Heaps() {
		// cannot be instantiate 
	}

	public static <E> Heap<E> synchronizedHeap(Heap<E> heap) {
		return new SynchronizedHeap<E>(heap);
	}

	static <E> Heap<E> synchronizedHeap(Heap<E> heap, Object mutex) {
		return new SynchronizedHeap<E>(heap, mutex);
	}
	
	/**
	 * @serial include
	 */
	private static class SynchronizedCollection<E> implements Collection<E>,
			Serializable {
		private static final long serialVersionUID = 3053995032091335093L;

		final Collection<E> c; // Backing Collection
		final Object mutex; // Object on which to synchronize

		@SuppressWarnings("unused")
		SynchronizedCollection(Collection<E> c) {
			this.c = Objects.requireNonNull(c);
			mutex = this;
		}

		SynchronizedCollection(Collection<E> c, Object mutex) {
			this.c = Objects.requireNonNull(c);
			this.mutex = mutex;
		}

		public int size() {
			synchronized (mutex) {
				return c.size();
			}
		}

		public boolean isEmpty() {
			synchronized (mutex) {
				return c.isEmpty();
			}
		}

		public boolean contains(Object o) {
			synchronized (mutex) {
				return c.contains(o);
			}
		}

		public Object[] toArray() {
			synchronized (mutex) {
				return c.toArray();
			}
		}

		public <T> T[] toArray(T[] a) {
			synchronized (mutex) {
				return c.toArray(a);
			}
		}

		public Iterator<E> iterator() {
			return c.iterator(); // Must be manually synched by user!
		}

		public boolean add(E e) {
			synchronized (mutex) {
				return c.add(e);
			}
		}

		public boolean remove(Object o) {
			synchronized (mutex) {
				return c.remove(o);
			}
		}

		public boolean containsAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.containsAll(coll);
			}
		}

		public boolean addAll(Collection<? extends E> coll) {
			synchronized (mutex) {
				return c.addAll(coll);
			}
		}

		public boolean removeAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.removeAll(coll);
			}
		}

		public boolean retainAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.retainAll(coll);
			}
		}

		public void clear() {
			synchronized (mutex) {
				c.clear();
			}
		}

		public String toString() {
			synchronized (mutex) {
				return c.toString();
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

	static class SynchronizedHeap<E> extends Queues.SynchronizedQueue<E> implements Heap<E>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1677871718453984661L;
		final Heap<E> heap;

		SynchronizedHeap(Heap<E> h) {
			super(h);
			this.heap = h;
		}

		SynchronizedHeap(Heap<E> h, Object mutex) {
			super(h, mutex);
			this.heap = h;
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

        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {s.defaultWriteObject();}
        }
        
    	protected transient volatile Collection<HeapEntry<E>> entrys = null;
    	protected transient volatile Collection<HeapEntry<E>> roots = null;

		@Override
		public Collection<HeapEntry<E>> entrys() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				if (entrys == null)
					entrys = new SynchronizedCollection<HeapEntry<E>>(heap.entrys(), mutex);
				return entrys;
			}
		}

		@Override
		public Collection<hust.idc.util.heap.Heap.HeapEntry<E>> roots() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				if (roots == null)
					roots = new SynchronizedCollection<HeapEntry<E>>(heap.roots(), mutex);
				return roots;
			}
		}
	}

}
