package hust.idc.util.heap.test;

import hust.idc.util.heap.FibonacciHeap;

import java.util.Iterator;

public class TestFibonacciHeap extends TestHeap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FibonacciHeap<Integer> heap = new FibonacciHeap<Integer>(comp);
		init(heap, HEAP_SIZE);
		
		System.out.println("Iterate: ");
		Iterator<Integer> it = heap.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		printSorted(heap.clone());
		
		System.out.println("Extract Max: " + heap.poll());
		printSorted(heap.clone());
		
		System.out.println("Heap Contains Element 35? " + heap.contains(35));
		System.out.println("Heap Contains Element 100? " + heap.contains(100));

		heap.decreaseElement(35, 30);
		System.out.println("Decrease : 35 ¡ú 30 " + heap);
		printSorted(heap.clone());
		heap.increaseElement(35, 40);
		System.out.println("Increase : 35 ¡ú 40 " + heap);
		printSorted(heap.clone());
		
		heap.remove(30);
		System.out.println("Remove : 30 " + heap);
		printSorted(heap.clone());
		
		System.out.println("Heap Contains Element 30? " + heap.contains(30));
		System.out.println("Heap Contains Element 100? " + heap.contains(100));
		
		testSerialization(heap);
		
//		heap.clear();
//		heap.rebuild();
//		System.out.println("Heap Rebuilded: " + heap);
//		System.out.println("Heap Sort: " + heap.sort());
		Iterator<Integer> it2 = heap.iterator();
		while(it2.hasNext()){
			System.out.println("Remove " + it2.next() + " in Heap : " + heap);
			it2.remove();
		}
		System.out.println("Clear: " + heap);
		printSorted(heap.clone());

	}

}
