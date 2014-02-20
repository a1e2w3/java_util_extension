package hust.idc.test;

import hust.idc.util.heap.BinaryHeap;
import hust.idc.util.heap.Heap;

import java.util.Comparator;
import java.util.Random;

public class TestBinaryHeap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Heap<Integer> heap = new BinaryHeap<Integer>(new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		});
		
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
		System.out.println("Heap Builded: " + heap);
		System.out.println("Heap Sort: " + heap.sort());
		
		System.out.println("Extract Max: " + heap.poll());
		System.out.println("Heap Sort: " + heap.sort());
		
		System.out.println("Heap Contains Element 35? " + heap.contains(35));
		System.out.println("Heap Contains Element 100? " + heap.contains(100));

		heap.decreaseElement(35, 30);
		System.out.println("Heap Sort: " + heap.sort());
		heap.increaseElement(35, 40);
		System.out.println("Heap Sort: " + heap.sort());
		
		heap.remove(30);
		System.out.println("Heap Sort: " + heap.sort());
		
		System.out.println("Heap Contains Element 30? " + heap.contains(30));
		System.out.println("Heap Contains Element 100? " + heap.contains(100));
		
		heap.clear();
		System.out.println("Clear: " + heap);
	}

}
