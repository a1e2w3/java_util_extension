package hust.idc.util.pair;

public interface ImmutablePair<T, S> extends Pair<T, S> {
	
	@Override
	ImmutablePair<S, T> convertPair();

}
