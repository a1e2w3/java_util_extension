package hust.idc.util.pair;

public interface ImmutableUnorderedPair<E> extends Pair<E, E>,
		ImmutablePair<E, E>, UnorderedPair<E> {
	
	@Override
	ImmutableUnorderedPair<E> convertPair();

}
