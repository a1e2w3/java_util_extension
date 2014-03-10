package hust.idc.util.matrix;

public interface SymmetricMatrix<K, V> extends Matrix<K, K, V> {

	static interface SymmetricMatrixEntry<K, V> extends Matrix.Entry<K, K, V>{
		
	}
}
