package hust.idc.util.pair;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class Pairs {
	
	@SuppressWarnings("rawtypes")
	public static final hust.idc.util.pair.ImmutableUnorderedPair EMPTY_PAIR = new EmptyPair();
	
	private Pairs() {

	}
	
	@SuppressWarnings("unchecked")
	public static final <E> ImmutableUnorderedPair<E> emptyPair() {
		return (ImmutableUnorderedPair<E>) EMPTY_PAIR;
	}
	
	private static final class EmptyPair<E> extends AbstractImmutableUnorderedPair<E> implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		EmptyPair() {
			
		}
		
		@Override
		public final E getFirst() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public final E getSecond() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public hust.idc.util.pair.ImmutableUnorderedPair<E> convertPair() {
			// TODO Auto-generated method stub
			return this;
		}

		@Override
		public boolean contains(E value) {
			// TODO Auto-generated method stub
			return value == null;
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if (obj == null)
				return false;
			if (this == obj)
				return true;
			if (! (obj instanceof Pair<?, ?>))
				return false;
			try {
				@SuppressWarnings("unchecked")
				Pair<E, E> other = (Pair<E, E>) obj;
				return other.getFirst() == null && other.getSecond() == null;
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				return false;
			}
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString();
		}
		
	}

	public static <T, S> Pair<T, S> makePair(T first, S second) {
		return new ObjectPair<T, S>(first, second);
	}
	
	public static IntPair makePair(int first, int second) {
		return new IntPair(first, second);
	}
	
	public static BooleanPair makePair(boolean first, boolean second) {
		return new BooleanPair(first, second);
	}

	public static <T> hust.idc.util.pair.UnorderedPair<T> makeUnorderedPair(
			T first, T second) {
		return toUnorderedPair(makePair(first, second));
	}

	public static <T> hust.idc.util.pair.UnorderedPair<T> makeUnorderedPair(
			T element) {
		return toUnorderedPair(makePair(element, element));
	}
	
	public static hust.idc.util.pair.UnorderedPair<Integer> makeUnorderedPair(
			int first, int second) {
		return toUnorderedPair(makePair(first, second));
	}

	public static hust.idc.util.pair.UnorderedPair<Integer> makeUnorderedPair(
			int element) {
		return toUnorderedPair(makePair(element, element));
	}
	
	public static hust.idc.util.pair.UnorderedPair<Boolean> makeUnorderedPair(
			boolean first, boolean second) {
		return toUnorderedPair(makePair(first, second));
	}

	public static hust.idc.util.pair.UnorderedPair<Boolean> makeUnorderedPair(
			boolean element) {
		return toUnorderedPair(makePair(element, element));
	}

	public static <K, V> KeyValue<K, V> makeKeyValue(K key, V value) {
		return toKeyValue(makePair(key, value));
	}

	public static <T, S> hust.idc.util.pair.ImmutablePair<T, S> makeImmutablePair(T first, S second) {
		return toImmutablePair(makePair(first, second));
	}
	
	public static hust.idc.util.pair.ImmutablePair<Integer, Integer> makeImmutablePair(int first, int second) {
		return toImmutablePair(makePair(first, second));
	}
	
	public static hust.idc.util.pair.ImmutablePair<Boolean, Boolean> makeImmutablePair(boolean first, boolean second) {
		return BooleanPair.getImmutableBooleanPair(first, second);
	}

	public static <T> hust.idc.util.pair.ImmutableUnorderedPair<T> makeImmutableUnorderedPair(
			T first, T second) {
		return toImmutableUnorderedPair(makeUnorderedPair(first, second));
	}

	public static <T> hust.idc.util.pair.ImmutableUnorderedPair<T> makeImmutableUnorderedPair(
			T element) {
		return toImmutableUnorderedPair(makeUnorderedPair(element));
	}
	
	public static hust.idc.util.pair.ImmutableUnorderedPair<Boolean> makeImmutableUnorderedPair(
			boolean first, boolean second) {
		return BooleanPair.getImmutableUnorderedBooleanPair(first, second);
	}

	public static hust.idc.util.pair.ImmutableUnorderedPair<Boolean> makeImmutableUnorderedPair(
			boolean element) {
		return BooleanPair.getImmutableUnorderedBooleanPair(element);
	}

	public static <T, S> hust.idc.util.pair.ImmutablePair<T, S> toImmutablePair(
			final Pair<T, S> pair) {
		if (pair instanceof hust.idc.util.pair.ImmutablePair)
			return (hust.idc.util.pair.ImmutablePair<T, S>) pair;
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
			super();
			this.pair = Objects.requireNonNull(pair);
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
			super();
			this.pair = Objects.requireNonNull(pair);
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

		@Override
		public E setFirst(E first) {
			// TODO Auto-generated method stub
			return pair.setFirst(first);
		}

		@Override
		public E setSecond(E second) {
			// TODO Auto-generated method stub
			return pair.setSecond(second);
		}

	}

	public static <E> hust.idc.util.pair.ImmutableUnorderedPair<E> toImmutableUnorderedPair(
			Pair<E, E> pair) {
		if (pair instanceof hust.idc.util.pair.ImmutableUnorderedPair)
			return (hust.idc.util.pair.ImmutableUnorderedPair<E>) pair;
		else
			return new ImmutableUnorderedPair<E>(pair);
	}

	static class ImmutableUnorderedPair<E> extends
			AbstractImmutableUnorderedPair<E> implements hust.idc.util.pair.ImmutableUnorderedPair<E>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5811054726908944189L;
		final Pair<E, E> pair;

		ImmutableUnorderedPair(Pair<E, E> pair) {
			super();
			this.pair = Objects.requireNonNull(pair);
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

	public static <K, V> KeyValue<K, V> toKeyValue(Pair<K, V> pair) {
		if (pair instanceof KeyValue)
			return (KeyValue<K, V>) pair;
		else
			return new KeyValuePair<K, V>(pair);
	}

	static class KeyValuePair<K, V> extends AbstractKeyValue<K, V> implements
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8758198514905670083L;

		final Pair<K, V> pair;

		KeyValuePair(Pair<K, V> pair) {
			super();
			this.pair = Objects.requireNonNull(pair);
		}

		@Override
		public K getKey() {
			// TODO Auto-generated method stub
			return pair.getFirst();
		}

		@Override
		public V getValue() {
			// TODO Auto-generated method stub
			return pair.getSecond();
		}

		@Override
		public V setValue(V value) {
			// TODO Auto-generated method stub
			return pair.setSecond(value);
		}

	}

	public static <T, S> Pair<T, S> synchronizedPair(Pair<T, S> pair) {
		return new SynchronizedPair<T, S>(pair);
	}

	public static <T, S> Pair<T, S> synchronizedPair(Pair<T, S> pair,
			Object mutex) {
		return new SynchronizedPair<T, S>(pair, mutex);
	}

	public static <E> hust.idc.util.pair.UnorderedPair<E> synchronizedPair(
			hust.idc.util.pair.UnorderedPair<E> pair) {
		return new SynchronizedUnorderedPair<E>(pair);
	}

	public static <E> hust.idc.util.pair.UnorderedPair<E> synchronizedPair(
			hust.idc.util.pair.UnorderedPair<E> pair, Object mutex) {
		return new SynchronizedUnorderedPair<E>(pair, mutex);
	}

	static abstract class AbstractSynchronizedPair<T, S> implements Pair<T, S>,
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5738869567629524764L;
		final Object mutex;
		final Pair<T, S> pair;

		public AbstractSynchronizedPair(Pair<T, S> pair) {
			this(pair, pair);
		}

		public AbstractSynchronizedPair(Pair<T, S> pair, Object mutex) {
			super();
			this.pair = Objects.requireNonNull(pair);
			this.mutex = Objects.requireNonNull(mutex);
		}

		@Override
		public T getFirst() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.getFirst();
			}
		}

		@Override
		public S getSecond() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.getSecond();
			}
		}

		@Override
		public T setFirst(T first) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.setFirst(first);
			}
		}

		@Override
		public S setSecond(S second) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.setSecond(second);
			}
		}

		@Override
		public Pair<S, T> convertPair() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return synchronizedPair(pair.convertPair(), mutex);
			}
		}

		@Override
		public boolean equalsWithOrder(
				Pair<? extends T, ? extends S> anotherPair) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.equalsWithOrder(anotherPair);
			}
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.hashCode();
			}
		}

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.equals(obj);
			}
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.toString();
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}

	}

	static class SynchronizedPair<T, S> extends AbstractSynchronizedPair<T, S>
			implements Pair<T, S>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3620230880445202242L;

		public SynchronizedPair(Pair<T, S> pair) {
			super(pair, pair);
		}

		public SynchronizedPair(Pair<T, S> pair, Object mutex) {
			super(pair, mutex);
		}

	}

	static class SynchronizedUnorderedPair<E> extends
			AbstractSynchronizedPair<E, E> implements
			hust.idc.util.pair.UnorderedPair<E>, Pair<E, E>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4933082494505760115L;
		final hust.idc.util.pair.UnorderedPair<E> pair;

		public SynchronizedUnorderedPair(
				hust.idc.util.pair.UnorderedPair<E> pair) {
			super(pair);
			// TODO Auto-generated constructor stub
			this.pair = pair;
		}

		public SynchronizedUnorderedPair(
				hust.idc.util.pair.UnorderedPair<E> pair, Object mutex) {
			super(pair, mutex);
			// TODO Auto-generated constructor stub
			this.pair = pair;
		}

		@Override
		public boolean contains(E value) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return pair.contains(value);
			}
		}

		@Override
		public hust.idc.util.pair.UnorderedPair<E> convertPair() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return synchronizedPair(pair.convertPair(), mutex);
			}
		}

	}

	static boolean eq(Object obj1, Object obj2) {
		return obj1 == null ? (obj2 == null) : (obj1 == obj2 || obj1
				.equals(obj2));
	}

}
