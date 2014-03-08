package hust.idc.util.pair;

public abstract class AbstractPair<T, S> implements Pair<T, S> {
	protected AbstractPair(){
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

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getFirst() == null) ? 0 : getFirst().hashCode());
		result = prime * result + ((getSecond() == null) ? 0 : getSecond().hashCode());
		return result;
	}
	
	protected static boolean eq(Object obj1, Object obj2){
		return obj1 == null ? (obj2 == null) : obj1.equals(obj2);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pair<?, ?>)) {
			return false;
		}
		
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return eq(getFirst(), other.getFirst()) && eq(getSecond(), other.getSecond());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("Pair[%s,%s]", 
				this.getFirst() == null ? "null" : this.getFirst().toString(), 
				this.getSecond() == null ? "null" : this.getSecond().toString());
	}

}
