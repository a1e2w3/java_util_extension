package hust.idc.util.pair;

public abstract class AbstractImmutablePair<T, S> extends AbstractPair<T, S> implements
		Pair<T, S> {
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
		throw new UnsupportedOperationException();
	}

	@Override
	public final S setSecond(S second) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	

}
