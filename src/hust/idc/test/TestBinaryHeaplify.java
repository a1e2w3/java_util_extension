package hust.idc.test;

import hust.idc.util.heap.BinaryHeap;
import hust.idc.util.heap.Heap;

import java.util.Comparator;
import java.util.Random;

public class TestBinaryHeaplify {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Heap<WritableInt> intHeap = new BinaryHeap<WritableInt>(new Comparator<WritableInt>(){
			@Override
			public int compare(WritableInt o1, WritableInt o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		});
		
		Random random = new Random();
		int size = random.nextInt(10) + 3;
		System.out.println("initialize: size = " + size);
		for(int i = 0; i < size; ++i){
			int elem = random.nextInt(100);
			intHeap.add(WritableInt.newInstance(elem));
			System.out.println(elem);
		}
		
		WritableInt element = WritableInt.newInstance(35);
		intHeap.add(element);
		intHeap.add(WritableInt.newInstance(30));
		System.out.println("35");
		System.out.println("Original heap: ");
		System.out.println("Heap Builded: " + intHeap);
		System.out.println("Heap Sorted: " + intHeap.sort());
		element.set(200);

		System.out.println("35¡ú200, without heaplify: ");
		System.out.println("Heap Builded: " + intHeap);
		System.out.println("Heap Sorted: " + intHeap.sort());
		intHeap.heaplifyAt(element);
		System.out.println("35¡ú200, heaplified: ");
		System.out.println("Heap Builded: " + intHeap);
		System.out.println("Heap Sorted: " + intHeap.sort());
		
		element.set(-1);
		System.out.println("200¡ú-1, without heaplify: ");
		System.out.println("Heap Builded: " + intHeap);
		System.out.println("Heap Sorted: " + intHeap.sort());
		intHeap.heaplifyAt(element);
		System.out.println("200¡ú-1, heaplified: ");
		System.out.println("Heap Builded: " + intHeap);
		System.out.println("Heap Sorted: " + intHeap.sort());

	}

}
