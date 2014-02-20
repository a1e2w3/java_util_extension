package hust.idc.test;

import hust.idc.util.heap.BinomialHeap;
import hust.idc.util.heap.Heap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class TestBinomialHeap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Heap<Integer> heap = new BinomialHeap<Integer>(new Comparator<Integer>(){
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
		System.out.println("Heap Size: " + heap.size());
		
		System.out.println("Iterate: ");
		Iterator<Integer> it = heap.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		
		System.out.println("Extract Max: " + heap.poll());
		System.out.println("Heap Sort: " + heap.sort());
		
		System.out.println("Heap Contains Element 35? " + heap.contains(35));
		System.out.println("Heap Contains Element 100? " + heap.contains(100));

		heap.decreaseElement(35, 30);
		System.out.println("Decrease : 35 ¡ú 30");
		System.out.println("Heap Sort: " + heap.sort());
		heap.increaseElement(35, 40);
		System.out.println("Increase : 35 ¡ú 40");
		System.out.println("Heap Sort: " + heap.sort());

		System.out.println("Remove 30");
		heap.remove(30);
		System.out.println("Heap : " + heap);
		
		System.out.println("Heap Contains Element 30? " + heap.contains(30));
		System.out.println("Heap Contains Element 100? " + heap.contains(100));
		
		heap.clear();
//		it = heap.iterator();
//		while(it.hasNext()){
//			System.out.println("Remove " + it.next() + " in Heap : " + heap);
//			it.remove();
////			System.out.println("Heap Sort: " + heap.sort());
////			try{
////				it.remove();
////			} catch(Throwable th){
////				th.printStackTrace();
////			}
//		}
		System.out.println("Clear: " + heap);

	}

}
