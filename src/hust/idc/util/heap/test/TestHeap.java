package hust.idc.util.heap.test;

import hust.idc.util.heap.Heap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class TestHeap {
	
	static final Comparator<Integer> comp = new Comparator<Integer>(){
		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
			return o1.compareTo(o2);
		}
		
	};
	
	static final Comparator<WritableInt> wcomp = new Comparator<WritableInt>(){
		@Override
		public int compare(WritableInt o1, WritableInt o2) {
			// TODO Auto-generated method stub
			return o1.compareTo(o2);
		}
		
	};
	
	static int initWritableInt(Heap<WritableInt> heap){
		Random random = new Random();
		int size = random.nextInt(10) + 3;
		System.out.println("initialize: size = " + size);
		for(int i = 0; i < size; ++i){
			int elem = random.nextInt(100);
			heap.add(WritableInt.newInstance(elem));
			System.out.println(elem);
		}
		System.out.println("Heap Builded: " + heap);
		return size;
	}
	
	static int init(Heap<Integer> heap){
		return init(heap, 0);
	}
	
	static int init(Heap<Integer> heap, int minSize){
		assert heap != null;
		Random random = new Random();
		int size = Math.max(random.nextInt(10), minSize) + 3;
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
		System.out.println("Heap Size: " + heap.size());
		return size + 2;
	}

	static <E> List<E> toSortedList(Heap<E> heap) {
		assert heap != null;
		List<E> list = new ArrayList<E>(heap.size());
		while (!heap.isEmpty())
			list.add(heap.poll());
		return list;
	}

	static <E> void printSorted(Heap<E> heap) {
		printSorted(heap, System.out);
	}

	static <E> void printSorted(Heap<E> heap, PrintStream ps) {
		assert heap != null;
		ps.print("Sorted: [");
		while (!heap.isEmpty()){
			ps.print(heap.poll());
			ps.print(' ');
		}
		ps.println(']');
	}
	
	static <E> void testSerialization(Heap<E> heap){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(heap);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Heap size: " + heap.size() + ", Serialized bytes: " + bout.size());
		
		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		try {
			ObjectInputStream in = new ObjectInputStream(bin);
			@SuppressWarnings("unchecked")
			Heap<E> h = (Heap<E>) in.readObject();
			System.out.println("Deserialized: " + h);
			printSorted(h);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
