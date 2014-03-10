package hust.idc.util.matrix.test;

import java.util.Iterator;
import java.util.Map;

import hust.idc.util.matrix.HashMatrix;
import hust.idc.util.matrix.Matrix;
import hust.idc.util.matrix.Matrix.Entry;

public class TestHashMatrix {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Matrix<Integer, Integer, Integer> matrix = new HashMatrix<Integer, Integer, Integer>();
		
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 5; ++j){
				matrix.set(i, j, i + j);
			}
		}
		printMatrix(matrix);
		
		Iterator<Entry<Integer, Integer, Integer>> iterator = matrix.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Integer, Integer, Integer> entry = iterator.next();
			System.out.println("Entry: " + entry);
			if(entry.getValue() == 3)
				iterator.remove();
			else if(entry.getValue() == 4)
				entry.setValue(8);
		}
		printMatrix(matrix);
		
		System.out.println("Row map: " + matrix.rowMap(2));
		Iterator<Map.Entry<Integer, Integer>> iterator2 = matrix.rowMap(2).entrySet().iterator();
		while(iterator2.hasNext()){
			Map.Entry<Integer, Integer> entry = iterator2.next();
			System.out.println("Row map entry: " + entry);
			if(entry.getKey() == 3)
				iterator2.remove();
			else if(entry.getKey() == 0)
				entry.setValue(9);
		}
		printMatrix(matrix);
		
		matrix.set(8, 9, 3);
		printMatrix(matrix);
		

		matrix.set(0, 0, -3);
		printMatrix(matrix);
		
		matrix.clear();
		printMatrix(matrix);

	}
	
	private static void printMatrix(Matrix<? extends Integer, ? extends Integer, ? extends Integer> matrix){
		System.out.println(matrix);
		System.out.println("Size :" + matrix.size());
		System.out.println("Empty :" + matrix.isEmpty());
		System.out.println("Rows: " + matrix.rows());
		System.out.println("Columns: " + matrix.columns());
		System.out.println("Row key set: " + matrix.rowKeySet());
		System.out.println("Column key set: " + matrix.columnKeySet());
		System.out.println("Key pair set: " + matrix.keyPairSet());
		System.out.println("Values: " + matrix.values());
		System.out.println("Get: " + matrix.get(0, 7));
		System.out.println(matrix.containsKey(2, 0));
		System.out.println(matrix.containsRow(8));
		System.out.println(matrix.containsColumn(6));
		System.out.println(matrix.containsValue(8));
	}

}
