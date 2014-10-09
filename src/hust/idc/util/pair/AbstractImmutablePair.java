package hust.idc.util.pair;

public abstract class AbstractImmutablePair<T, S> extends AbstractPair<T, S> implements
		ImmutablePair<T, S>, Pair<T, S> {
	protected AbstractImmutablePair(){
		super();
	}

	@Override
	public abstract T getFirst();

	@Override
	public abstract S getSecond();

	@Override
	public final T setFirst(T first) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("unsupported operation: setFirst");
	}

	@Override
	public final S setSecond(S second) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("unsupported operation: setSecond");
	}
	
	@Override
	public ImmutablePair<S, T> convertPair() {
		if (convertPair == null) {
			convertPair = new AbstractImmutablePair<S, T>() {

				@Override
				public S getFirst() {
					// TODO Auto-generated method stub
					return AbstractImmutablePair.this.getSecond();
				}

				@Override
				public T getSecond() {
					// TODO Auto-generated method stub
					return AbstractImmutablePair.this.getFirst();
				}

			};
		}
		return (ImmutablePair<S, T>)convertPair;
	}

}
