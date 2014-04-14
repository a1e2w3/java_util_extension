package hust.idc.util.pair;

public abstract class AbstractUnorderedPair<E> extends AbstractPair<E, E>
		implements UnorderedPair<E>, Pair<E, E> {
	
	protected AbstractUnorderedPair(){
		super();
	}

	@Override
	public abstract E getFirst();

	@Override
	public abstract E getSecond();
	
	@Override
	public boolean contains(E value){
		if(value == null)
			return this.getFirst() == null || this.getSecond() == null;
		else
			return this.getFirst().equals(value) || this.getSecond().equals(value);
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
		if (!(obj instanceof UnorderedPair<?>)) {
			return false;
		}
		UnorderedPair<?> other = (UnorderedPair<?>) obj;
		return (eq(getFirst(), other.getFirst()) && eq(getSecond(), other.getSecond())) 
				|| (eq(getFirst(), other.getSecond()) && eq(getSecond(), other.getFirst()));
	}

}
