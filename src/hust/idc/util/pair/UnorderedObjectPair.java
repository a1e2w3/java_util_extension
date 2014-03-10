package hust.idc.util.pair;

import java.io.Serializable;

public class UnorderedObjectPair<E> extends AbstractUnorderedPair<E> implements Pair<E, E>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3410190707122490395L;
	
	private E first, second;

	public UnorderedObjectPair(){
		this(null, null);
	}
	
	public UnorderedObjectPair(E value){
		this(value, value);
	}
	
	public UnorderedObjectPair(E first, E second){
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public E getFirst() {
		// TODO Auto-generated method stub
		return first;
	}

	@Override
	public E getSecond() {
		// TODO Auto-generated method stub
		return second;
	}

	@Override
	public E setFirst(E first) {
		// TODO Auto-generated method stub
		E old = this.first;
		this.first = first;
		return old;
	}

	@Override
	public E setSecond(E second) {
		// TODO Auto-generated method stub
		E old = this.second;
		this.second = second;
		return old;
	}

}