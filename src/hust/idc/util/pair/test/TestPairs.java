package hust.idc.util.pair.test;

import hust.idc.util.pair.Pairs;

public class TestPairs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println(Pairs.makePair(2, 3));
		System.out.println(Pairs.makeUnorderedPair(2, 3));
		System.out.println(Pairs.makeImmutableUnorderedPair(2, 3));
		
		System.out.println(Pairs.makePair('a', 'b'));
		System.out.println(Pairs.makeUnorderedPair('a'));
		System.out.println(Pairs.makeImmutableUnorderedPair('c'));

	}

}
