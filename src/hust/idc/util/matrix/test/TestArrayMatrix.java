package hust.idc.util.matrix.test;

import hust.idc.util.matrix.ArrayMatrix;
import hust.idc.util.matrix.Matrix;
import hust.idc.util.matrix.Matrix.Entry;

import java.util.Iterator;
import java.util.Map;

public class TestArrayMatrix extends TestMatrix {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Matrix<Integer, Integer, Integer> matrix = new ArrayMatrix<Integer, Integer, Integer>();

		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 5; ++j) {
				matrix.put(i, j, i + j);
			}
		}
		printMatrix(matrix);

		Iterator<Entry<Integer, Integer, Integer>> iterator = matrix.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<Integer, Integer, Integer> entry = iterator.next();
			System.out.println("Entry: " + entry);
			if (entry.getValue() == 3)
				iterator.remove();
			else if (entry.getValue() == 4)
				entry.setValue(8);
		}
		printMatrix(matrix);

		System.out.println("Row map: " + matrix.rowMap(2));
		Iterator<Map.Entry<Integer, Integer>> iterator2 = matrix.rowMap(2)
				.entrySet().iterator();
		while (iterator2.hasNext()) {
			Map.Entry<Integer, Integer> entry = iterator2.next();
			System.out.println("Row map entry: " + entry);
			if (entry.getKey() == 3)
				iterator2.remove();
			else if (entry.getKey() == 0)
				entry.setValue(9);
		}
		printMatrix(matrix);

		matrix.put(8, 9, 3);
		printMatrix(matrix);

		matrix.put(0, 0, -3);
		printMatrix(matrix);

		matrix.rowMap(2).clear();
		printMatrix(matrix);

		matrix.columnMap(2).clear();
		printMatrix(matrix);

		matrix.rowMap(2).put(0, -2);
		printMatrix(matrix);

		matrix.columnMap(2).put(0, -1);
		printMatrix(matrix);

		matrix.clear();
		printMatrix(matrix);
	}

}
