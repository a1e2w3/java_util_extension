package hust.idc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UnorderedPair<T> extends Pair<T, T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3410190707122490395L;

	public UnorderedPair(){
		super();
	}
	
	public UnorderedPair(T value){
		super(value, value);
	}
	
	public UnorderedPair(T first, T second){
		super(first, second);
	}
	
	public boolean contains(T value){
		if(value == null)
			return false;
		else
			return this.getFirst().equals(value) || this.getSecond().equals(value);
	}

	/* (non-Javadoc)
	 * @see css.util.Pair#equals(java.lang.Object)
	 */
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
		if(!(obj instanceof UnorderedPair<?>)){
			return ((Pair<?, ?>) obj).equals(this);
		}
		UnorderedPair<?> other = (UnorderedPair<?>) obj;
		if(this.getFirst() == null){
			if(other.getFirst() == null){
				if(this.getSecond() == null)
					return other.getSecond() == null;
				else
					return this.getSecond().equals(other.getSecond());
			} else if(other.getSecond() == null){
				return other.getFirst().equals(this.getSecond());
			} else {
				return false;
			}
		} else if(this.getSecond() == null){
			if(other.getFirst() == null){
				return this.getFirst().equals(other.getSecond());
			} else if(other.getSecond() == null){
				return this.getFirst().equals(other.getSecond());
			} else {
				return false;
			}
		} else {
			return (this.getFirst().equals(other.getFirst()) && this.getSecond().equals(other.getSecond())) || 
				(this.getFirst().equals(other.getSecond()) && this.getSecond().equals(other.getFirst()));
		}
	}

	/* (non-Javadoc)
	 * @see css.util.Pair#clone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Pair<T, T> clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		if(this.getFirst() instanceof Serializable){
			try{
				//将对象写到流里
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ObjectOutputStream oo = new ObjectOutputStream(bo);
				oo.writeObject(this);
				//从流里读出来
				ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
				ObjectInputStream oi = new ObjectInputStream(bi);
				return (UnorderedPair<T>) (oi.readObject());
			} catch(Exception e){
				// try to clone directly
			} 
		} 
		
		return (UnorderedPair<T>) super.clone();
	}

	/* (non-Javadoc)
	 * @see css.util.Pair#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		final int prime = 31;
		int result = (this.getFirst() == null) ? 0 : this.getFirst().hashCode();
		result += (this.getSecond() == null) ? 0 : this.getSecond().hashCode();
		return result * prime;
	}

}