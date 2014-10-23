package hust.idc.util.heap.test;

import hust.idc.util.heap.BinaryHeap;
import hust.idc.util.heap.Heaps;

public class TestHeaps {

	static <E> void printArray(E[] array) {
		if (array == null) {
			System.out.println("null");
		} else {
			System.out.print("[");
			for (int i = 0; i < array.length - 1; ++i) {
				System.out.print(array[i]);
				System.out.print(", ");
			}
			System.out.print(array[array.length - 1]);
			System.out.println("]");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WritableInt[] array = new WritableInt[10];
		for (int i = 0; i < array.length; ++i) {
			array[i] = new WritableInt(i);
		}
		
		BinaryHeap<WritableInt> heap = new BinaryHeap<WritableInt>(array);
		System.out.println(heap);

		printArray(array);
		
		Heaps.makeHeap(array, 2, 7);
		printArray(array);
		
		Heaps.makeHeap(array);
		printArray(array);
	}

}
