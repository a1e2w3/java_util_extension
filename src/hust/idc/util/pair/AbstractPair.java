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
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("Pair[%s,%s]", this.getFirst() == null ? "null"
				: this.getFirst().toString(), this.getSecond() == null ? "null"
				: this.getSecond().toString());
	}

}
