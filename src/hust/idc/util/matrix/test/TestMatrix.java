package hust.idc.util.matrix.test;

import hust.idc.util.matrix.Matrix;

public abstract class TestMatrix {
	
	protected static void printMatrix(Matrix<Integer, Integer, Integer> matrix){
		System.out.println();
		System.out.println("*****************************************");
		System.out.println("Matrix: " + matrix);
		System.out.println("Size :" + matrix.size());
		System.out.println("Empty :" + matrix.isEmpty());
		System.out.println("Rows: " + matrix.rows());
		System.out.println("Columns: " + matrix.columns());
		System.out.println("Row key set: " + matrix.rowKeySet());
		System.out.println("Column key set: " + matrix.columnKeySet());
		System.out.println("Key pair set: " + matrix.keyPairSet());
		System.out.println("Row map: " + matrix.rowMap(2));
		System.out.println("Column map: " + matrix.columnMap(2));
		System.out.println("Values: " + matrix.values());
		System.out.println("Get: " + matrix.get(0, 7));
		System.out.println(matrix.containsKey(2, 0));
		System.out.println(matrix.containsRow(8));
		System.out.println(matrix.containsColumn(6));
		System.out.println(matrix.containsValue(8));
	}
}
