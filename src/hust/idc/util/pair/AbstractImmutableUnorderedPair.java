package hust.idc.util.pair;

public abstract class AbstractImmutableUnorderedPair<E> extends
		AbstractUnorderedPair<E> implements UnorderedPair<E>, ImmutablePair<E, E>, Pair<E, E> {

	@Override
	public E getFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E getSecond() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public final E setFirst(E first) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public final E setSecond(E second) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
