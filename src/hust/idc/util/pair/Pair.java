package hust.idc.util.pair;

public interface Pair<T, S> {
	T getFirst();
	
	S getSecond();
	
	T setFirst(T first);
	
	S setSecond(S second);
	
	boolean equals(Object o);
	
	int hashCode();

}
