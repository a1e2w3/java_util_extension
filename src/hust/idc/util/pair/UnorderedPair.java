package hust.idc.util.pair;

public interface UnorderedPair<E> extends Pair<E, E> {
	
	UnorderedPair<E> convertPair();
	
	boolean contains(E value);
	
	boolean equals(Object obj);
	
	int hashCode();

}
