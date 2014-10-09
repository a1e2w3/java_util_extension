package hust.idc.util.pair;

import java.io.Serializable;
import java.util.Objects;

public class IntPair extends AbstractPair<Integer, Integer> implements Pair<Integer, Integer>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8575750960028425145L;
	volatile int first, second;
	
	public IntPair() {
		this(0, 0);
	}
	
	public IntPair(int first, int second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public final Integer getFirst() {
		// TODO Auto-generated method stub
		return this.first;
	}

	@Override
	public final Integer getSecond() {
		// TODO Auto-generated method stub
		return this.second;
	}

	public int setFirst(int first) {
		// TODO Auto-generated method stub
		int old = this.first;
		this.first = first;
		return old;
	}

	public int setSecond(int second) {
		// TODO Auto-generated method stub
		int old = this.second;
		this.second = second;
		return old;
	}
	
	@Override
	public final Integer setFirst(Integer first) {
		// TODO Auto-generated method stub
		return this.setFirst(Objects.requireNonNull(first).intValue());
	}

	@Override
	public final Integer setSecond(Integer second) {
		// TODO Auto-generated method stub
		return this.setSecond(Objects.requireNonNull(second).intValue());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("[%d, %d]", first, second);
	}

}
