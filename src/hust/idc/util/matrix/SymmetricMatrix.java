package hust.idc.util.matrix;

public interface SymmetricMatrix<K, V> extends Matrix<K, K, V> {
	
	int dimension();
	
	boolean equals(Object o);
	
	int hashCode();

	static interface SymmetricMatrixEntry<K, V> extends Matrix.Entry<K, K, V>{

		boolean equals(Object o);
		
		int hashCode();
	}
}
