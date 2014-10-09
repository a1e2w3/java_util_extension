package hust.idc.util.pair;

import java.io.Serializable;
import java.util.Objects;

public class BooleanPair extends AbstractPair<Boolean, Boolean> implements Pair<Boolean, Boolean> , Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5662618644970434677L;

	volatile byte value = 0x00;
	
	static final transient byte FIRST_MASK = 0x01;
	static final transient byte SECOND_MASK = 0x02;
	
	public BooleanPair() {
		super();
		//[false, false]
	}
	
	public BooleanPair(boolean first, boolean second) {
		super();
		if (first)  this.value |= FIRST_MASK;
		if (second) this.value |= SECOND_MASK;
	}
	
	private BooleanPair(byte value) {
		super();
		this.value = value;
	}

	@Override
	public final Boolean getFirst() {
		// TODO Auto-generated method stub
		return getFirstInternal();
	}
	
	private final boolean getFirstInternal() {
		return (this.value & FIRST_MASK) > 0;
	}

	@Override
	public final Boolean getSecond() {
		// TODO Auto-generated method stub
		return getSecondInternal();
	}
	
	private final boolean getSecondInternal() {
		// TODO Auto-generated method stub
		return (this.value & SECOND_MASK) > 0;
	}

	@Override
	public final Boolean setFirst(Boolean first) {
		// TODO Auto-generated method stub
		return this.setFirst(Objects.requireNonNull(first).booleanValue());
	}

	@Override
	public final Boolean setSecond(Boolean second) {
		// TODO Auto-generated method stub
		return this.setSecond(Objects.requireNonNull(second).booleanValue());
	}
	
	public boolean setFirst(boolean first) {
		boolean old = this.getFirstInternal();
		if (first) 
			this.setFirstBit();
		else 
			this.resetFirstBit();
		return old;
	}

	private final byte setFirstBit() {
		return this.value |= FIRST_MASK;
	}
	
	private final byte resetFirstBit() {
		return this.value &= SECOND_MASK;
	}
	
	public boolean setSecond(boolean second) {
		// TODO Auto-generated method stub
		boolean old = this.getSecondInternal();
		if (second)
			this.setSecondBit();
		else
			this.resetSecondBit();
		return old;
	}

	private final byte setSecondBit() {
		return this.value |= SECOND_MASK;
	}
	
	private final byte resetSecondBit() {
		return this.value &= FIRST_MASK;
	}
	
	public static final ImmutableBooleanPair TRUE_TRUE = new ImmutableBooleanPair((byte) 0x03){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public ImmutableBooleanPair convertPair() {
			// TODO Auto-generated method stub
			return this;
		}
		
	};
	public static final ImmutableBooleanPair TRUE_FALSE = new ImmutableBooleanPair((byte) 0x01){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public ImmutableBooleanPair convertPair() {
			// TODO Auto-generated method stub
			return FALSE_TRUE;
		}
		
	};
	public static final ImmutableBooleanPair FALSE_TRUE = new ImmutableBooleanPair((byte) 0x02){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public ImmutableBooleanPair convertPair() {
			// TODO Auto-generated method stub
			return TRUE_FALSE;
		}
		
	};
	public static final ImmutableBooleanPair FALSE_FALSE = new ImmutableBooleanPair((byte) 0x00){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public ImmutableBooleanPair convertPair() {
			// TODO Auto-generated method stub
			return this;
		}
		
	};

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("[%b,%b]", this.getFirstInternal(), this.getSecondInternal());
	}
	
	public static ImmutableBooleanPair getImmutableBooleanPair(boolean first, boolean second){
		if (first)
			return second ? TRUE_TRUE : TRUE_FALSE;
		else
			return second ? FALSE_TRUE : FALSE_FALSE;
	}

	private static abstract class ImmutableBooleanPair extends BooleanPair implements ImmutablePair<Boolean, Boolean>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2119386163057683175L;

		private ImmutableBooleanPair(byte value){
			super(value);
		}
		
		@Override
		public final boolean setFirst(boolean first) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("unsupported operation: setFirst");
		}

		@Override
		public final boolean setSecond(boolean second) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("unsupported operation: setSecond");
		}
		
		public abstract ImmutableBooleanPair convertPair();
	}
	
	public static final ImmutableUnorderedBooleanPair UNORDERED_TRUE_TRUE = new ImmutableUnorderedBooleanPair((byte) 0x03){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean contains(boolean value) {
			// TODO Auto-generated method stub
			return value;
		}

		@Override
		public ImmutableUnorderedBooleanPair convertPair() {
			// TODO Auto-generated method stub
			return this;
		}
		
	};
	public static final ImmutableUnorderedBooleanPair UNORDERED_TRUE_FALSE = new ImmutableUnorderedBooleanPair((byte) 0x01){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean contains(boolean value) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public ImmutableUnorderedBooleanPair convertPair() {
			// TODO Auto-generated method stub
			return UNORDERED_FALSE_TRUE;
		}
		
	};
	public static final ImmutableUnorderedBooleanPair UNORDERED_FALSE_TRUE = new ImmutableUnorderedBooleanPair((byte) 0x02){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean contains(boolean value) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public ImmutableUnorderedBooleanPair convertPair() {
			// TODO Auto-generated method stub
			return UNORDERED_TRUE_FALSE;
		}
		
	};
	public static final ImmutableUnorderedBooleanPair UNORDERED_FALSE_FALSE = new ImmutableUnorderedBooleanPair((byte) 0x00){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean contains(boolean value) {
			// TODO Auto-generated method stub
			return !value;
		}

		@Override
		public ImmutableUnorderedBooleanPair convertPair() {
			// TODO Auto-generated method stub
			return this;
		}
		
	};
	
	public static ImmutableUnorderedBooleanPair getImmutableUnorderedBooleanPair(boolean first, boolean second){
		if (first)
			return second ? UNORDERED_TRUE_TRUE : UNORDERED_TRUE_FALSE;
		else
			return second ? UNORDERED_FALSE_TRUE : UNORDERED_FALSE_FALSE;
	}
	
	public static ImmutableUnorderedBooleanPair getImmutableUnorderedBooleanPair(boolean value){
		return value ? UNORDERED_TRUE_TRUE : UNORDERED_FALSE_FALSE;
	}
	
	private static abstract class ImmutableUnorderedBooleanPair extends ImmutableBooleanPair implements ImmutableUnorderedPair<Boolean>, Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private ImmutableUnorderedBooleanPair(byte value){
			super(value);
		}
		
		@Override
		public final boolean contains(Boolean value){
			return value != null && this.contains(value.booleanValue());
		}
		
		public abstract boolean contains(boolean value);
		
		public abstract ImmutableUnorderedBooleanPair convertPair();

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof UnorderedPair<?>)) {
				if (! (obj instanceof Pair<?, ?>))
					return false;
				Pair<?, ?> other = (Pair<?, ?>) obj;
				return Pairs.eq(getFirst(), other.getFirst()) && Pairs.eq(getSecond(), other.getSecond());
			} else {
				UnorderedPair<?> other = (UnorderedPair<?>) obj;
				return (Pairs.eq(getFirst(), other.getFirst()) && Pairs.eq(getSecond(), other.getSecond())) 
						|| (Pairs.eq(getFirst(), other.getSecond()) && Pairs.eq(getSecond(), other.getFirst()));
			}
		}
		
		/* (non-Javadoc)
		 * @see css.util.Pair#hashCode()
		 */
		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			final int prime = 31;
			int result = (this.value & FIRST_MASK);
			result += ((this.value & SECOND_MASK) >> 1);
			return result * prime;
		}
		
	}
	
}
