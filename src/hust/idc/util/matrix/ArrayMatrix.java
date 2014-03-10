package hust.idc.util.matrix;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class ArrayMatrix<RK, CK, V> extends AbstractMatrix<RK, CK, V> implements
		Matrix<RK, CK, V> {
	private ArrayList<RK> rowKeys;
	private ArrayList<CK> columnKeys;
	private Entry<RK, CK, V>[][] entrys;
	private int size = 0;
	
	private int modCount = 0;
	
	public ArrayMatrix(){
		this(10);
	}
	
	public ArrayMatrix(int initialDemension){
		this(initialDemension, initialDemension);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayMatrix(int initialRows, int initialColumns){
		super();
        if (initialRows < 0)
            throw new IllegalArgumentException("Illegal Rows: "+
            		initialRows);
        if (initialColumns < 0)
            throw new IllegalArgumentException("Illegal Columns: "+
            		initialColumns);
        
		rowKeys = new ArrayList<RK>(initialRows);
		columnKeys = new ArrayList<CK>(initialColumns);
		entrys = new Entry[initialRows][initialColumns];
	}

	@Override
	public int rows() {
		// TODO Auto-generated method stub
		return rowKeys.size();
	}

	@Override
	public int columns() {
		// TODO Auto-generated method stub
		return columnKeys.size();
	}
	
	private int rowCapacity(){
		return entrys.length;
	}
	
	private int columnCapacity(){
		if(0 == entrys.length)
			return 0;
		return entrys[0].length;
	}
	
	private boolean checkIndexOutOfCapacity(int row, int column){
		return row >= 0 || row < rowCapacity()
				|| column >= 0 || column < columnCapacity();
	}
	
	private boolean checkIndexOutOfSize(int row, int column){
		return row >= 0 || row < rows()
				|| column >= 0 || column < columns();
	}
	
	private int rowIndex(Object row){
		return rowKeys.indexOf(row);
	}
	
	private int columnIndex(Object column){
		return columnKeys.indexOf(column);
	}

	@Override
	public V set(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		int rowIndex = rowIndex(row);
		int columnIndex = columnIndex(column);
		
		if(rowIndex < 0){
			rowKeys.add(row);
			rowIndex = rows() - 1;
		}
		
		if(columnIndex < 0){
			columnKeys.add(column);
			rowIndex = columns() - 1;
		}
		
		if(checkIndexOutOfCapacity(rowIndex, columnIndex)){
			ensureCapacity(rowIndex + 1, columnIndex + 1);
		}
		
		V oldValue = this.getValueAt(rowIndex, columnIndex);
		entrys[rowIndex][columnIndex] = new SimpleEntry<RK, CK, V>(row, column, value);
		++modCount;
		return oldValue;
	}
	
	private V getValueAt(int row, int column){
		try{
			return entrys[row][column].getValue();
		} catch(Throwable th){
			return null;
		}
	}
	
	public void ensureCapacity(int minDemension){
		ensureCapacity(minDemension, minDemension);
	}
	
	public void ensureCapacity(int minRow, int minColumn){
		if(minRow <= this.rowCapacity() && minColumn <= this.columnCapacity())
			return ;
	}

	// View
	protected transient volatile Set<Entry<RK, CK, V>> entrySet = null;
	protected transient volatile Set<RK> rowKeySet = null;
	protected transient volatile Set<CK> columnKeySet = null;
	
	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		if(rowKeySet == null){
			rowKeySet = new AbstractSet<RK>(){

				@Override
				public Iterator<RK> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<RK>(){
						ListIterator<RK> iterator = ArrayMatrix.this.rowKeys.listIterator();
						int index = -1;

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return iterator.hasNext();
						}

						@Override
						public RK next() {
							// TODO Auto-generated method stub
							index = iterator.nextIndex();
							return iterator.next();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							ArrayMatrix.this.removeRow(index);
						}
						
					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return ArrayMatrix.this.rows();
				}
				
			};
		}
		return rowKeySet;
	}

	@Override
	public Set<CK> columnKeySet() {
		// TODO Auto-generated method stub
		if(columnKeySet == null){
			columnKeySet = new AbstractSet<CK>(){

				@Override
				public Iterator<CK> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<CK>(){
						ListIterator<CK> iterator = ArrayMatrix.this.columnKeys.listIterator();
						int index = -1;

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return iterator.hasNext();
						}

						@Override
						public CK next() {
							// TODO Auto-generated method stub
							index = iterator.nextIndex();
							return iterator.next();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							ArrayMatrix.this.removeColumn(index);
						}
						
					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return ArrayMatrix.this.rows();
				}
				
			};
		}
		return columnKeySet;
	}
	
	@Override
	public void removeRow(Object row) {
		// TODO Auto-generated method stub
		this.removeRow(rowIndex(row));
	}

	@Override
	public void removeColumn(Object column) {
		// TODO Auto-generated method stub
		this.removeColumn(columnIndex(column));
	}

	private void removeRow(int index){
		++modCount;
	}
	
	private void removeColumn(int index){
		++modCount;
	}
	
	private V removeElementAt(int row, int column){
		V oldValue = this.getValueAt(row, column);
		this.entrys[row][column] = null;
		++modCount;
		return oldValue;
	}

	@Override
	protected int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		int rowIndex = this.rowKeys.indexOf(row);
		if(rowIndex < 0 || rowIndex >= rows())
			return 0;
		
		int count = 0;
		for(int i = 0; i < columns(); ++i){
			if(entrys[rowIndex][i] != null)
				++count;
		}
		return count;
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		int columnIndex = this.columnKeys.indexOf(column);
		if(columnIndex < 0 || columnIndex >= columns())
			return 0;
		
		int count = 0;
		for(int i = 0; i < rows(); ++i){
			if(entrys[i][columnIndex] != null)
				++count;
		}
		return count;
	}

	@Override
	public Set<Entry<RK, CK, V>> entrySet() {
		// TODO Auto-generated method stub
		if(entrySet == null){
			entrySet = new EntrySet();
		}
		return entrySet;
	}
	
	private class EntrySet extends AbstractSet<Entry<RK, CK, V>> {
		private EntrySet(){
			super();
		}

		@Override
		public Iterator<Entry<RK, CK, V>> iterator() {
			// TODO Auto-generated method stub
			return new EntryIterator();
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return ArrayMatrix.this.size;
		}
		
	}
	
	private class EntryIterator implements Iterator<Entry<RK, CK, V>> {
		private int expectedModCount;
		private boolean entryRemoved;
		private int rowIndex = 0, columnIndex = 0;
		private int nextRowIndex = 0, nextColumnIndex = 0;
		
		private EntryIterator(){
			this.expectedModCount = ArrayMatrix.this.modCount;
			this.getNextEntryIndex();
		}
		
		private void checkModified(){
			if(expectedModCount != ArrayMatrix.this.modCount)
				throw new ConcurrentModificationException();
		}
		
		private void getNextArrayIndex(){
			if(columnIndex >= ArrayMatrix.this.columns()){
				++nextRowIndex;
				nextColumnIndex = 0;
			} else {
				++nextColumnIndex;
			}
		}
		
		private void getNextEntryIndex(){
			while(!ArrayMatrix.this.checkIndexOutOfSize(nextRowIndex, nextColumnIndex)
					&& nextEntry() == null){
				this.getNextArrayIndex();
			}
		}
		
		private Entry<RK, CK, V> nextEntry(){
			return ArrayMatrix.this.entrys[nextRowIndex][nextColumnIndex];
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return !ArrayMatrix.this.checkIndexOutOfSize(nextRowIndex, columnIndex);
		}

		@Override
		public hust.idc.util.matrix.Matrix.Entry<RK, CK, V> next() {
			// TODO Auto-generated method stub
			if(ArrayMatrix.this.checkIndexOutOfSize(nextRowIndex, columnIndex))
				throw new NoSuchElementException();
			
			checkModified();
			Entry<RK, CK, V> e = this.nextEntry();
			rowIndex = nextRowIndex;
			columnIndex = nextColumnIndex;
			this.getNextEntryIndex();
			return e;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			if(!entryRemoved){
				checkModified();
				removeElementAt(rowIndex, columnIndex);
				entryRemoved = true;
				this.expectedModCount = ArrayMatrix.this.modCount;
			} else {
				throw new IllegalStateException();
			}
		}
		
	}
	
	private static class Head{
		int index = UNASSIGNED;
		int size = 0;
		
		private static final int UNASSIGNED = -1;
		
		private Head(){
			
		}
		
		private int getIndex(){
			return index;
		}
		
		private void assignIndex(int index){
			this.index = index;
		}
		
		private void removeIndexAssignment(){
			this.index = UNASSIGNED;
		}
		
		private int increaseSize(int incr){
			size = Math.max(0, size + incr);
			return size;
		}
	}

}
