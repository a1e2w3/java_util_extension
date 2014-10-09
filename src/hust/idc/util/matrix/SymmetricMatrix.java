package hust.idc.util.matrix;

import hust.idc.util.pair.UnorderedPair;

import java.util.Map;
import java.util.Set;

public interface SymmetricMatrix<K, V> extends Matrix<K, K, V> {
	
	int dimension();
	
	boolean containsRowOrColumn(Object key);
	
	void removeRowAndColumn(K key);
	
	Set<K> keySet();
	
	Map<K, V> keyMap(K key);

	static interface SymmetricMatrixEntry<K, V> extends Matrix.Entry<K, K, V>{
		
		@Override
		UnorderedPair<K> getKeyPair();
	}
}
