package hust.idc.util.pair;

public abstract class AbstractUnorderedPair<E> extends AbstractPair<E, E>
		implements UnorderedPair<E>, Pair<E, E> {
	
	public AbstractUnorderedPair(){
		super();
	}
	
	AbstractUnorderedPair(UnorderedPair<E> convertPair){
		super(convertPair);
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

	@Override
	public UnorderedPair<E> convertPair() {
		// TODO Auto-generated method stub
		if (convertPair == null) {
			convertPair = new AbstractUnorderedPair<E>(this) {

				@Override
				public E getFirst() {
					// TODO Auto-generated method stub
					return AbstractUnorderedPair.this.getSecond();
				}

				@Override
				public E getSecond() {
					// TODO Auto-generated method stub
					return AbstractUnorderedPair.this.getFirst();
				}

				@Override
				public E setFirst(E first) {
					// TODO Auto-generated method stub
					return AbstractUnorderedPair.this.setSecond(first);
				}

				@Override
				public E setSecond(E second) {
					// TODO Auto-generated method stub
					return AbstractUnorderedPair.this.setFirst(second);
				}

			};
		}
		return (UnorderedPair<E>) convertPair;
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
			if (! (obj instanceof Pair<?, ?>))
				return false;
			Pair<?, ?> other = (Pair<?, ?>) obj;
			return Pairs.eq(getFirst(), other.getFirst()) && Pairs.eq(getSecond(), other.getSecond());
		} else {
			UnorderedPair<?> other = (UnorderedPair<?>) obj;
			return (Pairs.eq(getFirst(), other.getFirst()) && Pairs.eq(getSecond(), other.getSecond())) 
					|| (Pairs.eq(getFirst(), other.getSecond()) && Pairs.eq(getSecond(), other.getFirst()));
		}
	}

}
