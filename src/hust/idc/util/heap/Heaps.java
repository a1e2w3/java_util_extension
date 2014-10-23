package hust.idc.util.heap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public final class Heaps {
	private Heaps() {
		// cannot be instantiate
	}
	
	public static <E extends Comparable<? super E>> boolean makeHeap(E[] array) {
		if (array == null || array.length <= 1)
			return false;
		return makeHeap(array, 0, array.length, new Comparator<E>() {

			@Override
			public int compare(E o1, E o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}

		});
	}

	public static <E extends Comparable<? super E>> boolean makeHeap(E[] array, int start, int end) {
		if (array == null || array.length <= 1)
			return false;
		return makeHeap(array, start, end, new Comparator<E>() {

			@Override
			public int compare(E o1, E o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}

		});
	}

	public static <E> boolean makeHeap(E[] array,
			Comparator<? super E> comparator) {
		if (array == null || array.length <= 1)
			return false;
		return makeHeap(array, 0, array.length, comparator);
	}

	public static <E> boolean makeHeap(E[] array, int start, int end,
			Comparator<? super E> comparator) {
		if (array == null)
			return false;
		int _start = Math.max(0, start);
		int _end = Math.min(end, array.length);
		if (_end - _start <= 1)
			return false;
		boolean modified = false;
		for (int i = ((_end - _start) << 1) + _start; i >= _start; --i) {
			modified |= heaplifyDown(array, i, _start, _end, comparator);
		}
		return modified;
	}

	private static <E> boolean heaplifyDown(E[] array, int index, int start,
			int end, Comparator<? super E> comparator) {
		int largestIdx = heaplifyAt(array, index, start, end, comparator);
		if (largestIdx == index) {
			return false;
		} else {
			heaplifyDown(array, largestIdx, start, end, comparator);
			return true;
		}
	}

	private static <E> int heaplifyAt(E[] array, int index, int start, int end,
			Comparator<? super E> comparator) {
		int largestIdx = index;

		int childIdx = ((index + 1 - start) << 1) - 1 + start;
		if (childIdx < end) {
			if (compare(array[childIdx], array[largestIdx], comparator) > 0) {
				largestIdx = childIdx;
			}
			++childIdx;
			if (childIdx < end
					&& compare(array[childIdx], array[largestIdx], comparator) > 0) {
				largestIdx = childIdx;
			}
		}
		swap(array, index, largestIdx);
		return largestIdx;
	}

	private static <E> int compare(E o1, E o2, Comparator<? super E> comparator) {
		return comparator == null ? 0 : comparator.compare(o1, o2);
	}

	private static <E> boolean swap(E[] array, int idx1, int idx2) {
		if (idx1 == idx2 || array[idx1] == array[idx2]) {
			return false;
		} else {
			final E tmp = array[idx1];
			array[idx1] = array[idx2];
			array[idx2] = tmp;
			return true;
		}
	}

	public static <E> Heap<E> synchronizedHeap(Heap<E> heap) {
		return new SynchronizedHeap<E>(heap);
	}

	static <E> Heap<E> synchronizedHeap(Heap<E> heap, Object mutex) {
		return new SynchronizedHeap<E>(heap, mutex);
	}

	static class SynchronizedHeap<E> extends Queues.SynchronizedQueue<E>
			implements Heap<E>, Serializable {

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
		public Comparator<? super E> comparator() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return heap.comparator();
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
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

}
