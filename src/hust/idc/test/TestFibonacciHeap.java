package hust.idc.test;

import hust.idc.util.heap.FibonacciHeap;
import hust.idc.util.heap.Heap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class TestFibonacciHeap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Heap<Integer> heap = new FibonacciHeap<Integer>(new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		});
		
		heap.add(0);
		Random random = new Random();
		int size = random.nextInt(10) + 3;
		System.out.println("initialize: size = " + size);
		for(int i = 0; i < size; ++i){
			int elem = random.nextInt(100);
			heap.add(new Integer(elem));
			System.out.println(elem);
		}
		
		heap.add(new Integer(35));
		System.out.println("35");
		heap.add(new Integer(35));
		System.out.println("35");
		heap.add(new Integer(35));
		System.out.println("35");
		
		System.out.println("Heap Builded: " + heap);
		System.out.println("Heap Size: " + heap.size());
		System.out.println("Heap Sort: " + heap.sort());
		
		Heap<Integer> cloneHeap = ((FibonacciHeap<Integer>)heap).clone();
		System.out.println("Heap Copy: " + cloneHeap);
		System.out.println("Copy Heap Size: " + cloneHeap.size());
		System.out.println("Copy Heap Sort: " + cloneHeap.sort());
		
		System.out.println("Iterate: ");
		Iterator<Integer> it = heap.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		
		System.out.println("Extract Max: " + heap.poll());
		System.out.println("Heap Rebuilded: " + heap);
		System.out.println("Heap Sort: " + heap.sort());
		
		System.out.println("Heap Contains Element 35? " + heap.contains(35));
		System.out.println("Heap Contains Element 100? " + heap.contains(100));

		heap.decreaseElement(35, 30);
		System.out.println("Decrease : 35 ¡ú 30");
//		System.out.println("Heap Rebuilded: " + heap);
		System.out.println("Heap Sort: " + heap.sort());
		heap.increaseElement(35, 40);
		System.out.println("Increase : 35 ¡ú 40");
//		System.out.println("Heap Rebuilded: " + heap);
		System.out.println("Heap Sort: " + heap.sort());
		
		heap.remove(30);
		System.out.println("Remove : 30");
		System.out.println("Heap Rebuilded: " + heap);
		System.out.println("Heap Sort: " + heap.sort());
		
		System.out.println("Heap Contains Element 30? " + heap.contains(30));
		System.out.println("Heap Contains Element 100? " + heap.contains(100));
		
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

	}

}
