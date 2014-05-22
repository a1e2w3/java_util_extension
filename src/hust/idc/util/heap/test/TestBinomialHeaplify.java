package hust.idc.util.heap.test;

import hust.idc.util.heap.BinomialHeap;
import hust.idc.util.heap.Heap;

public class TestBinomialHeaplify extends TestHeap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Heap<WritableInt> intHeap = new BinomialHeap<WritableInt>(wcomp);
		initWritableInt(intHeap);
		
		WritableInt element = WritableInt.newInstance(35);
		intHeap.add(element);
		intHeap.add(WritableInt.newInstance(30));
		System.out.println("35");
		System.out.println("Original heap: ");
		System.out.println("Heap Builded: " + intHeap);
		element.set(200);

		System.out.println("35¡ú200, without heaplify: ");
		System.out.println("Heap Builded: " + intHeap);
		intHeap.heaplifyAt(element);
		System.out.println("35¡ú200, heaplified: ");
		System.out.println("Heap Builded: " + intHeap);
		
		element.set(-1);
		System.out.println("200¡ú-1, without heaplify: ");
		System.out.println("Heap Builded: " + intHeap);
		intHeap.heaplifyAt(element);
		System.out.println("200¡ú-1, heaplified: ");
		System.out.println("Heap Builded: " + intHeap);

	}

}
