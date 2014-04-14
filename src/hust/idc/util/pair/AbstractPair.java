package hust.idc.util.pair;

public abstract class AbstractPair<T, S> implements Pair<T, S> {
	protected AbstractPair() {
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

	protected transient volatile Pair<S, T> convertPair;

	@Override
	public Pair<S, T> convertPair() {
		if (convertPair == null) {
			convertPair = new AbstractPair<S, T>() {

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
	public int hashCode() {
		// TODO Auto-generated method stub
		final int prime = 31;
		int result = 1;
		result += ((getFirst() == null) ? 0 : getFirst().hashCode());
		result += ((getSecond() == null) ? 0 : getSecond().hashCode());
		return result * prime;
	}

	protected static boolean eq(Object obj1, Object obj2) {
		return obj1 == null ? (obj2 == null) : obj1.equals(obj2);
	}

	@Override
	public boolean equalsWithOrder(Pair<? extends T, ? extends S> anotherPair) {
		// TODO Auto-generated method stub
		return anotherPair != null && eq(getFirst(), anotherPair.getFirst())
				&& eq(getSecond(), anotherPair.getSecond());
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
		return eq(getFirst(), other.getFirst())
				&& eq(getSecond(), other.getSecond());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("Pair[%s,%s]", this.getFirst() == null ? "null"
				: this.getFirst().toString(), this.getSecond() == null ? "null"
				: this.getSecond().toString());
	}

}
