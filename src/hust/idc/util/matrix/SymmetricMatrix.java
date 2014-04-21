package hust.idc.util.matrix;

import java.util.Map;
import java.util.Set;

public interface SymmetricMatrix<K, V> extends Matrix<K, K, V> {
	
	int dimension();
	
	boolean containsRowOrColumn(Object key);
	
	void removeKey(K key);
	
	Set<K> keySet();
	
	Map<K, V> keyMap(K key);
	
	boolean equals(Object o);
	
	int hashCode();

	static interface SymmetricMatrixEntry<K, V> extends Matrix.Entry<K, K, V>{

		boolean equals(Object o);
		
		int hashCode();
	}
}
