package hust.idc.util.heap;

import hust.idc.util.RandomAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class HeapSortor {
	
	private HeapSortor(){}
	
	public static <E extends Comparable<? super E>> List<E> sort(Collection<? extends E> elements){
		return sort(new ArrayList<E>(elements));
	}
	
	public static <E> List<E> sort(Collection<? extends E> elements, Comparator<? super E> comparator){
		return sort(new ArrayList<E>(elements), comparator);
	}
	
	public static <E extends Comparable<? super E>> RandomAccess<E> sort(RandomAccess<E> elements){
		Heaps.makeHeap(elements);
		Heaps.sortHeap(elements);
		return elements;
	}
	
	public static <E extends Comparable<? super E>> RandomAccess<E> sort(RandomAccess<E> elements, int start, int end){
		Heaps.makeHeap(elements, start, end);
		Heaps.sortHeap(elements, start, end);
		return elements;
	}
	
	public static <E> RandomAccess<E> sort(RandomAccess<E> elements, Comparator<? super E> comparator){
		Heaps.makeHeap(elements, comparator);
		Heaps.sortHeap(elements, comparator);
		return elements;
	}
	
	public static <E> RandomAccess<E> sort(RandomAccess<E> elements, int start, int end, Comparator<? super E> comparator){
		Heaps.makeHeap(elements, start, end, comparator);
		Heaps.sortHeap(elements, start, end, comparator);
		return elements;
	}
	
	public static <E extends Comparable<? super E>> List<E> sort(List<E> elements){
		Heaps.makeHeap(elements);
		Heaps.sortHeap(elements);
		return elements;
	}
	
	public static <E extends Comparable<? super E>> List<E> sort(List<E> elements, int start, int end){
		Heaps.makeHeap(elements, start, end);
		Heaps.sortHeap(elements, start, end);
		return elements;
	}
	
	public static <E> List<E> sort(List<E> elements, Comparator<? super E> comparator){
		Heaps.makeHeap(elements, comparator);
		Heaps.sortHeap(elements, comparator);
		return elements;
	}
	
	public static <E> List<E> sort(List<E> elements, int start, int end, Comparator<? super E> comparator){
		Heaps.makeHeap(elements, start, end, comparator);
		Heaps.sortHeap(elements, start, end, comparator);
		return elements;
	}
	
	public static <E extends Comparable<? super E>> E[] sort(E[] array){
		Heaps.makeHeap(array);
		Heaps.sortHeap(array);
		return array;
	}
	
	public static <E extends Comparable<? super E>> E[] sort(E[] array, int start, int end){
		Heaps.makeHeap(array, start, end);
		Heaps.sortHeap(array, start, end);
		return array;
	}
	
	public static <E> E[] sort(E[] array, Comparator<? super E> comparator){
		Heaps.makeHeap(array, comparator);
		Heaps.sortHeap(array, comparator);
		return array;
	}
	
	public static <E> E[] sort(E[] array, int start, int end, Comparator<? super E> comparator){
		Heaps.makeHeap(array, start, end, comparator);
		Heaps.sortHeap(array, start, end, comparator);
		return array;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random random = new Random();
		int size = random.nextInt(10) + 3;

		Collection<Integer> c = new ArrayList<Integer>(size);
		System.out.println("initialize: size = " + size);
		for(int i = 0; i < size; ++i){
			int elem = random.nextInt(100);
			c.add(new Integer(elem));
			System.out.println("element " + i + ": " + elem);
		}
		System.out.println("Heap Sorted: " + sort(c, new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		}));

	}

}
