package hust.idc.util;

import java.util.ListIterator;

public interface RandomAccessIterator<E> extends ListIterator<E> {
	
	boolean canAdvance(int n);
	
	E advance(int n);

}
