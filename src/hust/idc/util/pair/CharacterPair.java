package hust.idc.util.pair;

import java.io.Serializable;
import java.util.Objects;

public class CharacterPair extends AbstractPair<Character, Character> implements Pair<Character, Character>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4039701925882164081L;

	volatile int code = 0x00000000;
	
	static final int FIRST_MASK = 0x0000ffff;
	static final int SECOND_MASK = 0xffff0000;
	
	public CharacterPair() {
		super();
	}
	
	public CharacterPair(char first, char second) {
		super();
		code |= first;
		code |= (second << 16);
	}
	
	@Override
	public final Character getFirst() {
		// TODO Auto-generated method stub
		return getFirstInternal();
	}

	@Override
	public final Character getSecond() {
		// TODO Auto-generated method stub
		return getSecondInternal();
	}
	
	private final char getFirstInternal(){
		return (char) code;
	}
	
	private final char getSecondInternal(){
		return (char) (code >> 16);
	}

	@Override
	public final Character setFirst(Character first) {
		// TODO Auto-generated method stub
		return this.setFirst(Objects.requireNonNull(first).charValue());
	}
	
	public char setFirst(char first) {
		char old = this.getFirstInternal();
		this.code &= SECOND_MASK;
		this.code |= first;
		return old;
	}

	@Override
	public final Character setSecond(Character second) {
		// TODO Auto-generated method stub
		return this.setSecond(Objects.requireNonNull(second).charValue());
	}
	
	public char setSecond(char second){
		char old = this.getSecondInternal();
		this.code &= FIRST_MASK;
		this.code |= (second << 16);
		return old;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("[%c,%c]", this.getFirstInternal(), this.getSecondInternal());
	}

}
