package hust.idc.util.pair;

import java.io.Serializable;



public class UnorderedPair<T> extends AbstractPair<T, T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3410190707122490395L;
	
	private T first, second;

	public UnorderedPair(){
		this(null, null);
	}
	
	public UnorderedPair(T value){
		this(value, value);
	}
	
	public UnorderedPair(T first, T second){
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public T getFirst() {
		// TODO Auto-generated method stub
		return first;
	}

	@Override
	public T getSecond() {
		// TODO Auto-generated method stub
		return second;
	}

	@Override
	public T setFirst(T first) {
		// TODO Auto-generated method stub
		return this.first = first;
	}

	@Override
	public T setSecond(T second) {
		// TODO Auto-generated method stub
		return this.second = second;
	}
	
	public boolean contains(T value){
		if(value == null)
			return this.getFirst() == null || this.getSecond() == null;
		else
			return this.getFirst().equals(value) || this.getSecond().equals(value);
	}

	/* (non-Javadoc)
	 * @see css.util.Pair#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pair<?, ?>)) {
			return false;
		}
		if(!(obj instanceof UnorderedPair<?>)){
			return ((Pair<?, ?>) obj).equals(this);
		}
		UnorderedPair<?> other = (UnorderedPair<?>) obj;
		return (eq(getFirst(), other.getFirst()) && eq(getSecond(), other.getSecond())) 
				|| (eq(getFirst(), other.getSecond()) && eq(getSecond(), other.getFirst()));
	}

	/* (non-Javadoc)
	 * @see css.util.Pair#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		final int prime = 31;
		int result = (this.getFirst() == null) ? 0 : this.getFirst().hashCode();
		result += (this.getSecond() == null) ? 0 : this.getSecond().hashCode();
		return result * prime;
	}

}