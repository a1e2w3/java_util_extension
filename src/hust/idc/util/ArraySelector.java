package hust.idc.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ArraySelector {
	public static <T> T select(T[] a, Comparator<? super T> c, int order){
		if(a == null)
			return null;
		return select(a, c, 0, a.length, order);
	}
	
	public static <T> T select(T[] a, Comparator<? super T> c, int fromIndex, int endIndex, int order){
		if(a == null)
			return null;
		T[] cux = Arrays.copyOf(a, a.length);
		return selectInternal(cux, c, fromIndex, endIndex, order);
	}
	
	public static <T> T min(T[] a, Comparator<? super T> c){
		if(a == null)
			return null;
		return min(a, c, 0, a.length);
	}
	
	public static <T> T min(T[] a, Comparator<? super T> c, int fromIndex, int endIndex){
		if(a == null)
			return null;
		checkIndex(a.length, fromIndex, endIndex);
		int minIndex = fromIndex;
		for(int i = fromIndex + 1; i < endIndex; ++i){
			if(c.compare(a[minIndex], a[i]) > 0)
				minIndex = i;
		}
		return a[minIndex];
	}
	
	public static <T> T max(T[] a, Comparator<? super T> c){
		if(a == null)
			return null;
		return max(a, c, 0, a.length);
	}
	
	public static <T> T max(T[] a, Comparator<? super T> c, int fromIndex, int endIndex){
		if(a == null)
			return null;
		checkIndex(a.length, fromIndex, endIndex);
		int maxIndex = fromIndex;
		for(int i = fromIndex + 1; i < endIndex; ++i){
			if(c.compare(a[maxIndex], a[i]) < 0)
				maxIndex = i;
		}
		return a[maxIndex];
	}
	
	private static <T> T selectInternal(T[] a, Comparator<? super T> c, int fromIndex, int endIndex, int order){
		if(order <= 0)
			return null;
		if(a == null)
			return null;
		checkIndex(a.length, fromIndex, endIndex);
		if(order > endIndex - fromIndex)
			throw new IllegalArgumentException(order + " > " + endIndex + " - " + fromIndex);
		
		int pivot = randomizedPartition(a, c, fromIndex, endIndex);
		int k = pivot - fromIndex + 1;
		
		if(k == order)
			return a[pivot];
		else if(k > order)
			return select(a, c, fromIndex, pivot, order);
		else
			return select(a, c, pivot + 1, endIndex, order - k);
	}
	
	private static <T> int randomizedPartition(T[] a, Comparator<? super T> c, int fromIndex, int endIndex){
		Random random = new Random();
		int i = random.nextInt(endIndex - fromIndex) + fromIndex;
		exchangeElement(a, i, endIndex - 1);
		return partition(a, c, fromIndex, endIndex);
	}
	
	private static <T> int partition(T[] a, Comparator<? super T> c, int fromIndex, int endIndex){
		T pivot = a[endIndex - 1];
		int i = fromIndex - 1;
		for(int j = fromIndex; j < endIndex - 1; ++j){
			if(c.compare(a[j], pivot) <= 0){
				exchangeElement(a, ++i, j);
			}
		}
		exchangeElement(a, ++i, endIndex - 1);
		return i;
	}
	
	private static boolean exchangeElement(Object[] a, int i1, int i2){
		if(i1 == i2)
			return false;
		Object temp = a[i1];
		a[i1] = a[i2];
		a[i2] = temp;
		return true;
	}
	
	private static void checkIndex(int length, int from, int end){
		if(length <= 0)
			throw new IndexOutOfBoundsException("length less than 1: " + length);
		if(from < 0 || from >= length)
			throw new IndexOutOfBoundsException("length: " + length + ", from: " + from);
		if(end <= 0 || end > length)
			throw new IndexOutOfBoundsException("length: " + length + ", end: " + end);
		if(from >= end)
			throw new IndexOutOfBoundsException("from: " + from + ", end: " + end);
	}
	
	public static void main(String[] args){
		Integer[] array = {1, 3, 7, 8, 2, 5, 3, 4};
		Comparator<Integer> c = new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		};
		
		Integer elem = select(array, c, 4);
		System.out.println(elem);
	}

}
