package hust.idc.util.pair;

public abstract class AbstractPair<T, S> implements Pair<T, S> {
	public AbstractPair() {
		super();
	}
	
	AbstractPair(Pair<S, T> convertPair){
		this();
		this.convertPair = convertPair;
	}

	@Override
	public abstract T getFirst();

	@Override
	public abstract S getSecond();

	@Override
	public T setFirst(T first) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public S setSecond(S second) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	transient volatile Pair<S, T> convertPair;

	@Override
	public Pair<S, T> convertPair() {
		if (convertPair == null) {
			convertPair = new AbstractPair<S, T>(this) {

				@Override
				public S getFirst() {
					// TODO Auto-generated method stub
					return AbstractPair.this.getSecond();
				}

				@Override
				public T getSecond() {
					// TODO Auto-generated method stub
					return AbstractPair.this.getFirst();
				}

				@Override
				public S setFirst(S first) {
					// TODO Auto-generated method stub
					return AbstractPair.this.setSecond(first);
				}

				@Override
				public T setSecond(T second) {
					// TODO Auto-generated method stub
					return AbstractPair.this.setFirst(second);
				}

			};
		}
		return convertPair;
	}

	@Override
	public boolean equalsWithOrder(Pair<? extends T, ? extends S> anotherPair) {
		// TODO Auto-generated method stub
		if (anotherPair == this)
			return true;
		return anotherPair != null && Pairs.eq(getFirst(), anotherPair.getFirst())
				&& Pairs.eq(getSecond(), anotherPair.getSecond());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("[%s,%s]", toString(this.getFirst()), toString(this.getSecond()));
	}
	
	String toString(Object obj) {
		if (obj == null)
			return "null";
		if (obj == this)
			return "this pair";
		return obj.toString();
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		final int prime = 31;
		int result = 1;
		result = result * prime + ((getFirst() == null) ? 0 : getFirst().hashCode());
		result = result * prime + ((getSecond() == null) ? 0 : getSecond().hashCode());
		return result * prime;
	}
	
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

		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Pairs.eq(getFirst(), other.getFirst())
				&& Pairs.eq(getSecond(), other.getSecond());
	}

}
