package hust.idc.util;

import java.util.Comparator;
import java.util.Random;

/**
 * 
 * @author WangCong
 * 
 */
public class ArraySelector {
	public static <T extends Comparable<? super T>> T select(T[] a, int order) {
		if (a == null)
			throw new NullPointerException();
		return select(a, 0, a.length, order);
	}

	public static <T extends Comparable<? super T>> T select(T[] a,
			int fromIndex, int endIndex, int order) {
		if (a == null)
			throw new NullPointerException();
		return selectInternal(a, fromIndex, endIndex, order);
	}

	public static <T> T select(T[] a, Comparator<? super T> c, int order) {
		if (a == null)
			throw new NullPointerException();
		return select(a, c, 0, a.length, order);
	}

	public static <T> T select(T[] a, Comparator<? super T> c, int fromIndex,
			int endIndex, int order) {
		if (a == null)
			throw new NullPointerException();
		return selectInternal(a, c, fromIndex, endIndex, order);
	}

//	public static int select(int[] a, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return select(a, 0, a.length, order);
//	}
//
//	public static int select(int[] a, int fromIndex, int endIndex, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return selectInternal(a, fromIndex, endIndex, order).intValue();
//	}
//
//	public static long select(long[] a, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return select(a, 0, a.length, order);
//	}
//
//	public static long select(long[] a, int fromIndex, int endIndex, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return selectInternal(a, fromIndex, endIndex, order).longValue();
//	}
//
//	public static short select(short[] a, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return select(a, 0, a.length, order);
//	}
//
//	public static short select(short[] a, int fromIndex, int endIndex, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return selectInternal(a, fromIndex, endIndex, order).shortValue();
//	}
//
//	public static float select(float[] a, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return select(a, 0, a.length, order);
//	}
//
//	public static float select(float[] a, int fromIndex, int endIndex, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return selectInternal(a, fromIndex, endIndex, order).floatValue();
//	}
//
//	public static double select(double[] a, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return select(a, 0, a.length, order);
//	}
//
//	public static double select(double[] a, int fromIndex, int endIndex, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return selectInternal(a, fromIndex, endIndex, order).doubleValue();
//	}
//
//	public static byte select(byte[] a, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return select(a, 0, a.length, order);
//	}
//
//	public static byte select(byte[] a, int fromIndex, int endIndex, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return selectInternal(a, fromIndex, endIndex, order).byteValue();
//	}
//
//	public static char select(char[] a, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return select(a, 0, a.length, order);
//	}
//
//	public static char select(char[] a, int fromIndex, int endIndex, int order) {
//		if (a == null)
//			throw new NullPointerException();
//		return selectInternal(a, fromIndex, endIndex, order).charValue();
//	}
//
//	private static int selectInternal(int[] a, int fromIndex, int endIndex, int order) {
//		if (order <= 0)
//			throw new ArrayIndexOutOfBoundsException(
//					"Array index out of bounds. order less than or equal with 0: "
//							+ order);
//		if (a == null)
//			throw new NullPointerException();
//		checkIndex(a.length, fromIndex, endIndex);
//		if (order > endIndex - fromIndex)
//			throw new IllegalArgumentException(order + " > " + endIndex + " - "
//					+ fromIndex);
//
//		int pivot = randomizedPartition(a, fromIndex, endIndex);
//		int k = pivot - fromIndex + 1;
//
//		if (k == order)
//			return a[pivot];
//		else if (k > order)
//			return select(a, fromIndex, pivot, order);
//		else
//			return select(a, pivot + 1, endIndex, order - k);
//	}
//
//	private static long selectInternal(long[] a, int fromIndex, int endIndex, int order) {
//		if (order <= 0)
//			throw new ArrayIndexOutOfBoundsException(
//					"Array index out of bounds. order less than or equal with 0: "
//							+ order);
//		if (a == null)
//			throw new NullPointerException();
//		checkIndex(a.length, fromIndex, endIndex);
//		if (order > endIndex - fromIndex)
//			throw new IllegalArgumentException(order + " > " + endIndex + " - "
//					+ fromIndex);
//
//		int pivot = randomizedPartition(a, fromIndex, endIndex);
//		int k = pivot - fromIndex + 1;
//
//		if (k == order)
//			return a[pivot];
//		else if (k > order)
//			return select(a, fromIndex, pivot, order);
//		else
//			return select(a, pivot + 1, endIndex, order - k);
//	}
//
//	private static short selectInternal(short[] a, int fromIndex, int endIndex, int order) {
//		if (order <= 0)
//			throw new ArrayIndexOutOfBoundsException(
//					"Array index out of bounds. order less than or equal with 0: "
//							+ order);
//		if (a == null)
//			throw new NullPointerException();
//		checkIndex(a.length, fromIndex, endIndex);
//		if (order > endIndex - fromIndex)
//			throw new IllegalArgumentException(order + " > " + endIndex + " - "
//					+ fromIndex);
//
//		int pivot = randomizedPartition(a, fromIndex, endIndex);
//		int k = pivot - fromIndex + 1;
//
//		if (k == order)
//			return a[pivot];
//		else if (k > order)
//			return select(a, fromIndex, pivot, order);
//		else
//			return select(a, pivot + 1, endIndex, order - k);
//	}
//
//	private static float selectInternal(float[] a, int fromIndex, int endIndex, int order) {
//		if (order <= 0)
//			throw new ArrayIndexOutOfBoundsException(
//					"Array index out of bounds. order less than or equal with 0: "
//							+ order);
//		if (a == null)
//			throw new NullPointerException();
//		checkIndex(a.length, fromIndex, endIndex);
//		if (order > endIndex - fromIndex)
//			throw new IllegalArgumentException(order + " > " + endIndex + " - "
//					+ fromIndex);
//
//		int pivot = randomizedPartition(a, fromIndex, endIndex);
//		int k = pivot - fromIndex + 1;
//
//		if (k == order)
//			return a[pivot];
//		else if (k > order)
//			return select(a, fromIndex, pivot, order);
//		else
//			return select(a, pivot + 1, endIndex, order - k);
//	}
//
//	private static double selectInternal(double[] a, int fromIndex, int endIndex, int order) {
//		if (order <= 0)
//			throw new ArrayIndexOutOfBoundsException(
//					"Array index out of bounds. order less than or equal with 0: "
//							+ order);
//		if (a == null)
//			throw new NullPointerException();
//		checkIndex(a.length, fromIndex, endIndex);
//		if (order > endIndex - fromIndex)
//			throw new IllegalArgumentException(order + " > " + endIndex + " - "
//					+ fromIndex);
//
//		int pivot = randomizedPartition(a, fromIndex, endIndex);
//		int k = pivot - fromIndex + 1;
//
//		if (k == order)
//			return a[pivot];
//		else if (k > order)
//			return select(a, fromIndex, pivot, order);
//		else
//			return select(a, pivot + 1, endIndex, order - k);
//	}
//
//	private static char selectInternal(char[] a, int fromIndex, int endIndex, int order) {
//		if (order <= 0)
//			throw new ArrayIndexOutOfBoundsException(
//					"Array index out of bounds. order less than or equal with 0: "
//							+ order);
//		if (a == null)
//			throw new NullPointerException();
//		checkIndex(a.length, fromIndex, endIndex);
//		if (order > endIndex - fromIndex)
//			throw new IllegalArgumentException(order + " > " + endIndex + " - "
//					+ fromIndex);
//
//		int pivot = randomizedPartition(a, fromIndex, endIndex);
//		int k = pivot - fromIndex + 1;
//
//		if (k == order)
//			return a[pivot];
//		else if (k > order)
//			return select(a, fromIndex, pivot, order);
//		else
//			return select(a, pivot + 1, endIndex, order - k);
//	}
//
//	private static byte selectInternal(byte[] a, int fromIndex, int endIndex, int order) {
//		if (order <= 0)
//			throw new ArrayIndexOutOfBoundsException(
//					"Array index out of bounds. order less than or equal with 0: "
//							+ order);
//		if (a == null)
//			throw new NullPointerException();
//		checkIndex(a.length, fromIndex, endIndex);
//		if (order > endIndex - fromIndex)
//			throw new IllegalArgumentException(order + " > " + endIndex + " - "
//					+ fromIndex);
//
//		int pivot = randomizedPartition(a, fromIndex, endIndex);
//		int k = pivot - fromIndex + 1;
//
//		if (k == order)
//			return a[pivot];
//		else if (k > order)
//			return select(a, fromIndex, pivot, order);
//		else
//			return select(a, pivot + 1, endIndex, order - k);
//	}

	private static <T extends Comparable<? super T>> T selectInternal(T[] a,
			int fromIndex, int endIndex, int order) {
		if (order <= 0)
			throw new ArrayIndexOutOfBoundsException(
					"Array index out of bounds. order less than or equal with 0: "
							+ order);
		if (a == null)
			throw new NullPointerException();
		checkIndex(a.length, fromIndex, endIndex);
		if (order > endIndex - fromIndex)
			throw new IllegalArgumentException(order + " > " + endIndex + " - "
					+ fromIndex);

		int pivot = randomizedPartition(a, fromIndex, endIndex);
		int k = pivot - fromIndex + 1;

		if (k == order)
			return a[pivot];
		else if (k > order)
			return select(a, fromIndex, pivot, order);
		else
			return select(a, pivot + 1, endIndex, order - k);
	}

	private static <T> T selectInternal(T[] a, Comparator<? super T> c,
			int fromIndex, int endIndex, int order) {
		if (order <= 0)
			throw new ArrayIndexOutOfBoundsException(
					"Array index out of bounds. order less than or equal with 0: "
							+ order);
		if (a == null)
			throw new NullPointerException();
		checkIndex(a.length, fromIndex, endIndex);
		if (order > endIndex - fromIndex)
			throw new IllegalArgumentException(order + " > " + endIndex + " - "
					+ fromIndex);

		int pivot = randomizedPartition(a, c, fromIndex, endIndex);
		int k = pivot - fromIndex + 1;

		if (k == order)
			return a[pivot];
		else if (k > order)
			return select(a, c, fromIndex, pivot, order);
		else
			return select(a, c, pivot + 1, endIndex, order - k);
	}

	private static <T> int randomizedPartition(T[] a, Comparator<? super T> c,
			int fromIndex, int endIndex) {
		Random random = new Random();
		int i = random.nextInt(endIndex - fromIndex) + fromIndex;
		exchangeElement(a, i, endIndex - 1);
		return partition(a, c, fromIndex, endIndex);
	}

	private static <T extends Comparable<? super T>> int randomizedPartition(
			T[] a, int fromIndex, int endIndex) {
		Random random = new Random();
		int i = random.nextInt(endIndex - fromIndex) + fromIndex;
		exchangeElement(a, i, endIndex - 1);
		return partition(a, fromIndex, endIndex);
	}

	private static <T> int partition(T[] a, Comparator<? super T> c,
			int fromIndex, int endIndex) {
		T pivot = a[endIndex - 1];
		int i = fromIndex - 1;
		for (int j = fromIndex; j < endIndex - 1; ++j) {
			if (c.compare(a[j], pivot) <= 0) {
				exchangeElement(a, ++i, j);
			}
		}
		exchangeElement(a, ++i, endIndex - 1);
		return i;
	}

	private static <T extends Comparable<? super T>> int partition(T[] a,
			int fromIndex, int endIndex) {
		T pivot = a[endIndex - 1];
		int i = fromIndex - 1;
		for (int j = fromIndex; j < endIndex - 1; ++j) {
			if (a[j] == null) {
				if (pivot != null && pivot.compareTo(a[j]) >= 0) {
					exchangeElement(a, ++i, j);
				} else {
					// do nothing
				}
			} else if(a[j].compareTo(pivot) <= 0){
					exchangeElement(a, ++i, j);
			} else {
				// do nothing
			}
		}
		exchangeElement(a, ++i, endIndex - 1);
		return i;
	}

	private static boolean exchangeElement(Object[] a, int i1, int i2) {
		if (i1 == i2)
			return false;
		Object temp = a[i1];
		a[i1] = a[i2];
		a[i2] = temp;
		return true;
	}
	
	public static <T extends Comparable<? super T>> T min(T[] a) {
		if (a == null)
			throw new NullPointerException();
		return min(a, 0, a.length);
	}

	public static <T extends Comparable<? super T>> T min(T[] a, int fromIndex, int endIndex) {
		if (a == null)
			throw new NullPointerException();
		checkIndex(a.length, fromIndex, endIndex);
		int minIndex = fromIndex;
		for (int i = fromIndex + 1; i < endIndex; ++i) {
			if(a[i] == null){
				if(a[minIndex] != null && a[minIndex].compareTo(a[i]) > 0) {
					minIndex = i;
				} else {
					// do nothing
				}
			} else if (a[i].compareTo(a[minIndex]) < 0) {
				minIndex = i;
			} else {
				// do nothing
			}
		}
		return a[minIndex];
	}

	public static <T> T min(T[] a, Comparator<? super T> c) {
		if (a == null)
			throw new NullPointerException();
		return min(a, c, 0, a.length);
	}

	public static <T> T min(T[] a, Comparator<? super T> c, int fromIndex,
			int endIndex) {
		if (a == null)
			throw new NullPointerException();
		checkIndex(a.length, fromIndex, endIndex);
		int minIndex = fromIndex;
		for (int i = fromIndex + 1; i < endIndex; ++i) {
			if (c.compare(a[minIndex], a[i]) > 0)
				minIndex = i;
		}
		return a[minIndex];
	}

	public static <T extends Comparable<? super T>> T max(T[] a) {
		if (a == null)
			throw new NullPointerException();
		return max(a, 0, a.length);
	}

	public static <T extends Comparable<? super T>> T max(T[] a, int fromIndex,
			int endIndex) {
		if (a == null)
			throw new NullPointerException();
		checkIndex(a.length, fromIndex, endIndex);
		int maxIndex = fromIndex;
		for (int i = fromIndex + 1; i < endIndex; ++i) {
			if(a[i] == null){
				if(a[maxIndex] != null && a[maxIndex].compareTo(a[i]) < 0) {
					maxIndex = i;
				} else {
					// do nothing
				}
			} else if (a[i].compareTo(a[maxIndex]) > 0) {
				maxIndex = i;
			} else {
				// do nothing
			}
		}
		return a[maxIndex];
	}

	public static <T> T max(T[] a, Comparator<? super T> c) {
		if (a == null)
			throw new NullPointerException();
		return max(a, c, 0, a.length);
	}

	public static <T> T max(T[] a, Comparator<? super T> c, int fromIndex,
			int endIndex) {
		if (a == null)
			throw new NullPointerException();
		checkIndex(a.length, fromIndex, endIndex);
		int maxIndex = fromIndex;
		for (int i = fromIndex + 1; i < endIndex; ++i) {
			if (c.compare(a[maxIndex], a[i]) < 0)
				maxIndex = i;
		}
		return a[maxIndex];
	}

	private static void checkIndex(int length, int from, int end) {
		if (length <= 0)
			throw new IndexOutOfBoundsException("length less than 1: " + length);
		if (from < 0 || from >= length)
			throw new IndexOutOfBoundsException("length: " + length
					+ ", from: " + from);
		if (end <= 0 || end > length)
			throw new IndexOutOfBoundsException("length: " + length + ", end: "
					+ end);
		if (from >= end)
			throw new IndexOutOfBoundsException("from: " + from + ", end: "
					+ end);
	}

	public static void main(String[] args) {
		Integer[] array = { 1, 3, 7, 8, 2, 5, 3, 4 };
		Comparator<Integer> c = new Comparator<Integer>() {
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
