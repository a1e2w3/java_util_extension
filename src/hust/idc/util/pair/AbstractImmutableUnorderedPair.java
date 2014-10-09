package hust.idc.util.pair;

public abstract class AbstractImmutableUnorderedPair<E> extends
		AbstractUnorderedPair<E> implements ImmutableUnorderedPair<E> {

	@Override
	public abstract E getFirst();

	@Override
	public abstract E getSecond();
	
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
	
	@Override
	public ImmutableUnorderedPair<E> convertPair() {
		if (convertPair == null) {
			convertPair = new AbstractImmutableUnorderedPair<E>() {

				@Override
				public E getFirst() {
					// TODO Auto-generated method stub
					return AbstractImmutableUnorderedPair.this.getSecond();
				}

				@Override
				public E getSecond() {
					// TODO Auto-generated method stub
					return AbstractImmutableUnorderedPair.this.getFirst();
				}

			};
		}
		return (ImmutableUnorderedPair<E>)convertPair;
	}

}
