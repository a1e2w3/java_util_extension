package hust.idc.util.heap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class HeapSortor {
	public static <E> List<E> sort(Collection<? extends E> elements, Comparator<? super E> comparator){
		Heap<E> heap = new BinaryHeap<E>(elements, comparator);
		return heap.sortAndClear();
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
		System.out.println("Heap Sord: " + sort(c, new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		}));

	}

}
