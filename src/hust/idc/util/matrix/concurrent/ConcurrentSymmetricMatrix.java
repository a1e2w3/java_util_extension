package hust.idc.util.matrix.concurrent;

import hust.idc.util.matrix.SymmetricMatrix;

public interface ConcurrentSymmetricMatrix<K, V> extends
		SymmetricMatrix<K, V>, ConcurrentMatrix<K, K, V> {

}
