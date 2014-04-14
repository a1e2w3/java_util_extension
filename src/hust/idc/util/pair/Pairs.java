package hust.idc.util.pair;

import java.io.Serializable;

public class Pairs {
	private Pairs() {

	}

	public static <T, S> ImmutablePair<T, S> toImmutablePair(
			final Pair<T, S> pair) {
		if (pair instanceof ImmutablePair)
			return (ImmutablePair<T, S>) pair;
		else
			return new ImmutablePair<T, S>(pair);
	}

	static class ImmutablePair<T, S> extends AbstractImmutablePair<T, S>
			implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6601603169129121628L;
		final Pair<T, S> pair;

		ImmutablePair(Pair<T, S> pair) {
			if (pair == null)
				throw new NullPointerException();
			this.pair = pair;
		}

		@Override
		public T getFirst() {
			// TODO Auto-generated method stub
			return pair.getFirst();
		}

		@Override
		public S getSecond() {
			// TODO Auto-generated method stub
			return pair.getSecond();
		}
	}

	public static <E> hust.idc.util.pair.UnorderedPair<E> toUnorderedPair(
			Pair<E, E> pair) {
		if (pair instanceof hust.idc.util.pair.UnorderedPair)
			return (hust.idc.util.pair.UnorderedPair<E>) pair;
		else
			return new UnorderedPair<E>(pair);
	}

	static class UnorderedPair<E> extends AbstractUnorderedPair<E> implements
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4233429716784951376L;
		final Pair<E, E> pair;

		UnorderedPair(Pair<E, E> pair) {
			if (pair == null)
				throw new NullPointerException();
			this.pair = pair;
		}

		@Override
		public E getFirst() {
			// TODO Auto-generated method stub
			return pair.getFirst();
		}

		@Override
		public E getSecond() {
			// TODO Auto-generated method stub
			return pair.getSecond();
		}
	}

	public static <E> Pair<E, E> toImmutableUnorderedPair(Pair<E, E> pair) {
		if (pair instanceof AbstractImmutableUnorderedPair)
			return (AbstractImmutableUnorderedPair<E>) pair;
		else
			return new ImmutableUnorderedPair<E>(pair);
	}

	static class ImmutableUnorderedPair<E> extends
			AbstractImmutableUnorderedPair<E> implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5811054726908944189L;
		final Pair<E, E> pair;
		
		ImmutableUnorderedPair(Pair<E, E> pair){
			if (pair == null)
				throw new NullPointerException();
			this.pair = pair;
		}

		@Override
		public E getFirst() {
			// TODO Auto-generated method stub
			return pair.getFirst();
		}

		@Override
		public E getSecond() {
			// TODO Auto-generated method stub
			return pair.getSecond();
		}
	}

}
