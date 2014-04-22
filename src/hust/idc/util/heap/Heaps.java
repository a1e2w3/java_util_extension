package hust.idc.util.heap;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

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
