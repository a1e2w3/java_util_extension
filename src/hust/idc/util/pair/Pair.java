package hust.idc.util.pair;

public interface Pair<T, S> {
	T getFirst();
	
	S getSecond();
	
	T setFirst(T first);
	
	S setSecond(S second);
	
	Pair<S, T> convertPair();
	
	boolean equals(Object o);
	
	int hashCode();
	
	boolean equalsWithOrder(Pair<? extends T, ? extends S> anotherPair);

}
