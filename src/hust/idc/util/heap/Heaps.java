package hust.idc.util.heap;

import hust.idc.util.RandomAccess;

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

	private static final class DefaultComparator<E extends Comparable<? super E>>
			implements Comparator<E> {

		@Override
		public int compare(E o1, E o2) {
			// TODO Auto-generated method stub
			return o1.compareTo(o2);
		}

	}

	@SuppressWarnings("rawtypes")
	private static final Comparator<?> defaultComparator = new DefaultComparator();

	@SuppressWarnings("unchecked")
	static final <E extends Comparable<? super E>> Comparator<E> defaultComparator() {
		return (Comparator<E>) defaultComparator;
	}

	public static <E extends Comparable<? super E>> boolean makeHeap(E[] array) {
		return makeHeap(RandomAccess.<E>getInstance(array), Heaps.<E> defaultComparator());
	}

	public static <E extends Comparable<? super E>> boolean makeHeap(E[] array,
			int start, int end) {
		return makeHeap(RandomAccess.<E>getInstance(array), start, end, Heaps.<E> defaultComparator());
	}

	public static <E> boolean makeHeap(E[] array,
			Comparator<? super E> comparator) {
		return makeHeap(RandomAccess.<E>getInstance(array), comparator);
	}
	
	public static <E> boolean makeHeap(E[] array, int start, int end, 
			Comparator<? super E> comparator) {
		return makeHeap(RandomAccess.<E>getInstance(array), start, end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean makeHeap(List<E> list) {
		return makeHeap(RandomAccess.<E>getInstance(list), Heaps.<E> defaultComparator());
	}

	public static <E extends Comparable<? super E>> boolean makeHeap(List<E> list,
			int start, int end) {
		return makeHeap(RandomAccess.<E>getInstance(list), start, end, Heaps.<E> defaultComparator());
	}

	public static <E> boolean makeHeap(List<E> list,
			Comparator<? super E> comparator) {
		return makeHeap(RandomAccess.<E>getInstance(list), comparator);
	}
	
	public static <E> boolean makeHeap(List<E> list, int start, int end, 
			Comparator<? super E> comparator) {
		return makeHeap(RandomAccess.<E>getInstance(list), start, end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean makeHeap(RandomAccess<E> array) {
		return makeHeap(array, array.minIndex(), array.maxIndex(), Heaps.<E> defaultComparator());
	}
	
	public static <E extends Comparable<? super E>> boolean makeHeap(RandomAccess<E> array, int start, int end) {
		return makeHeap(array, start, end, Heaps.<E> defaultComparator());
	}
	
	public static <E> boolean makeHeap(RandomAccess<E> array, Comparator<? super E> comparator) {
		return makeHeap(array, array.minIndex(), array.maxIndex(), comparator);
	}

	public static <E> boolean makeHeap(RandomAccess<E> array, int start, int end, Comparator<? super E> comparator) {
		final int _start = Math.max(start, array.minIndex());
		final int _end = Math.min(end, array.maxIndex());
		if (_end - _start <= 1)
			return false;
		boolean modified = false;
		for (int i = ((_end - _start) << 1) + _start; i >= _start; --i) {
			modified |= heaplifyDown(array, i, _start, _end, comparator);
		}
		return modified;
	}

	private static <E> boolean heaplifyDown(RandomAccess<E> array, int index, int start, int end, Comparator<? super E> comparator) {
		int largestIdx = heaplifyAt(array, index, start, end, comparator);
		if (largestIdx == index) {
			return false;
		} else {
			heaplifyDown(array, largestIdx, start, end, comparator);
			return true;
		}
	}
	
	private static <E> boolean heaplifyUp(RandomAccess<E> array, int index, int start, int end, Comparator<? super E> comparator) {
		if (index <= start)
			return false;
		int parentIdx = ((index - 1 - start) >> 1) + start;
		int largestIdx = heaplifyAt(array, parentIdx, start, end, comparator);
		if (largestIdx != parentIdx) {
			heaplifyUp(array, parentIdx, start, end, comparator);
			return true;
		}
		return false;
	}

	private static <E> int heaplifyAt(RandomAccess<E> array, int index, int start, int end, Comparator<? super E> comparator) {
		int largestIdx = index;

		int childIdx = ((index + 1 - start) << 1) - 1 + start;
		if (childIdx < end) {
			if (compare(array.get(childIdx), array.get(largestIdx), comparator) > 0) {
				largestIdx = childIdx;
			}
			++childIdx;
			if (childIdx < end
					&& compare(array.get(childIdx), array.get(largestIdx), comparator) > 0) {
				largestIdx = childIdx;
			}
		}
		array.swap(index, largestIdx);
		return largestIdx;
	}

	private static <E> int compare(E o1, E o2, Comparator<? super E> comparator) {
		return comparator == null ? 0 : comparator.compare(o1, o2);
	}
	
	public static <E extends Comparable<? super E>> boolean pushHeap(E[] array) {
		return pushHeap(RandomAccess.<E>getInstance(array), Heaps.<E> defaultComparator());
	}

	public static <E extends Comparable<? super E>> boolean pushHeap(E[] array,
			int start, int end) {
		return pushHeap(RandomAccess.<E>getInstance(array), start, end, Heaps.<E> defaultComparator());
	}

	public static <E> boolean pushHeap(E[] array,
			Comparator<? super E> comparator) {
		return pushHeap(RandomAccess.<E>getInstance(array), comparator);
	}
	
	public static <E> boolean pushHeap(E[] array, int start, int end, 
			Comparator<? super E> comparator) {
		return pushHeap(RandomAccess.<E>getInstance(array), start, end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean pushHeap(List<E> list) {
		return pushHeap(RandomAccess.<E>getInstance(list), Heaps.<E> defaultComparator());
	}

	public static <E extends Comparable<? super E>> boolean pushHeap(List<E> list,
			int start, int end) {
		return pushHeap(RandomAccess.<E>getInstance(list), start, end, Heaps.<E> defaultComparator());
	}

	public static <E> boolean pushHeap(List<E> list,
			Comparator<? super E> comparator) {
		return pushHeap(RandomAccess.<E>getInstance(list), comparator);
	}
	
	public static <E> boolean pushHeap(List<E> list, int start, int end, 
			Comparator<? super E> comparator) {
		return pushHeap(RandomAccess.<E>getInstance(list), start, end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean pushHeap(RandomAccess<E> array) {
		return pushHeap(array, array.minIndex(), array.maxIndex(), Heaps.<E> defaultComparator());
	}
	
	public static <E extends Comparable<? super E>> boolean pushHeap(RandomAccess<E> array, int start, int end) {
		return pushHeap(array, start, end, Heaps.<E> defaultComparator());
	}
	
	public static <E> boolean pushHeap(RandomAccess<E> array,
			Comparator<? super E> comparator) {
		return pushHeap(array, array.minIndex(), array.maxIndex(), comparator);
	}
	
	public static <E> boolean pushHeap(RandomAccess<E> array, int start, int end, Comparator<? super E> comparator){
		final int _start = Math.max(start, array.minIndex());
		final int _end = Math.min(end, array.maxIndex());
		if (_end - _start <= 1)
			return false;
		return heaplifyUp(array, _end - 1, _start, _end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean popHeap(E[] array) {
		return popHeap(RandomAccess.<E>getInstance(array), Heaps.<E> defaultComparator());
	}

	public static <E extends Comparable<? super E>> boolean popHeap(E[] array,
			int start, int end) {
		return popHeap(RandomAccess.<E>getInstance(array), start, end, Heaps.<E> defaultComparator());
	}

	public static <E> boolean popHeap(E[] array,
			Comparator<? super E> comparator) {
		return popHeap(RandomAccess.<E>getInstance(array), comparator);
	}
	
	public static <E> boolean popHeap(E[] array, int start, int end, 
			Comparator<? super E> comparator) {
		return popHeap(RandomAccess.<E>getInstance(array), start, end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean popHeap(List<E> list) {
		return popHeap(RandomAccess.<E>getInstance(list), Heaps.<E> defaultComparator());
	}

	public static <E extends Comparable<? super E>> boolean popHeap(List<E> list,
			int start, int end) {
		return popHeap(RandomAccess.<E>getInstance(list), start, end, Heaps.<E> defaultComparator());
	}

	public static <E> boolean popHeap(List<E> list,
			Comparator<? super E> comparator) {
		return popHeap(RandomAccess.<E>getInstance(list), comparator);
	}
	
	public static <E> boolean popHeap(List<E> list, int start, int end, 
			Comparator<? super E> comparator) {
		return popHeap(RandomAccess.<E>getInstance(list), start, end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean popHeap(RandomAccess<E> array) {
		return popHeap(array, array.minIndex(), array.maxIndex(), Heaps.<E> defaultComparator());
	}
	
	public static <E extends Comparable<? super E>> boolean popHeap(RandomAccess<E> array, int start, int end) {
		return popHeap(array, start, end, Heaps.<E> defaultComparator());
	}
	
	public static <E> boolean popHeap(RandomAccess<E> array,
			Comparator<? super E> comparator) {
		return popHeap(array, array.minIndex(), array.maxIndex(), comparator);
	}
	
	public static <E> boolean popHeap(RandomAccess<E> array, int start, int end, Comparator<? super E> comparator){
		final int _start = Math.max(start, array.minIndex());
		final int _end = Math.min(end, array.maxIndex());
		if (_end - _start <= 1)
			return false;
		array.swap(_start, _end - 1);
		heaplifyDown(array, _start, _start, _end - 1, comparator);
		return true;
	}
	
	public static <E extends Comparable<? super E>> boolean sortHeap(E[] array) {
		return sortHeap(RandomAccess.<E>getInstance(array), Heaps.<E> defaultComparator());
	}

	public static <E extends Comparable<? super E>> boolean sortHeap(E[] array,
			int start, int end) {
		return sortHeap(RandomAccess.<E>getInstance(array), start, end, Heaps.<E> defaultComparator());
	}

	public static <E> boolean sortHeap(E[] array,
			Comparator<? super E> comparator) {
		return sortHeap(RandomAccess.<E>getInstance(array), comparator);
	}
	
	public static <E> boolean sortHeap(E[] array, int start, int end, 
			Comparator<? super E> comparator) {
		return sortHeap(RandomAccess.<E>getInstance(array), start, end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean sortHeap(List<E> list) {
		return sortHeap(RandomAccess.<E>getInstance(list), Heaps.<E> defaultComparator());
	}

	public static <E extends Comparable<? super E>> boolean sortHeap(List<E> list,
			int start, int end) {
		return sortHeap(RandomAccess.<E>getInstance(list), start, end, Heaps.<E> defaultComparator());
	}

	public static <E> boolean sortHeap(List<E> list,
			Comparator<? super E> comparator) {
		return sortHeap(RandomAccess.<E>getInstance(list), comparator);
	}
	
	public static <E> boolean sortHeap(List<E> list, int start, int end, 
			Comparator<? super E> comparator) {
		return sortHeap(RandomAccess.<E>getInstance(list), start, end, comparator);
	}
	
	public static <E extends Comparable<? super E>> boolean sortHeap(RandomAccess<E> array) {
		return sortHeap(array, array.minIndex(), array.maxIndex(), Heaps.<E> defaultComparator());
	}
	
	public static <E extends Comparable<? super E>> boolean sortHeap(RandomAccess<E> array, int start, int end) {
		return sortHeap(array, start, end, Heaps.<E> defaultComparator());
	}
	
	public static <E> boolean sortHeap(RandomAccess<E> array,
			Comparator<? super E> comparator) {
		return sortHeap(array, array.minIndex(), array.maxIndex(), comparator);
	}
	
	public static <E> boolean sortHeap(RandomAccess<E> array, int start, int end, Comparator<? super E> comparator){
		final int _start = Math.max(start, array.minIndex());
		int _end = Math.min(end, array.maxIndex());
		if (_end - _start <= 1)
			return false;
		while(_end - _start > 1){
			popHeap(array, _start, _end--, comparator);
		}
		return true;
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
