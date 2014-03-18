package hust.idc.util.matrix;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ArrayMatrix<RK, CK, V> extends AbstractMatrix<RK, CK, V> implements
		Matrix<RK, CK, V> {
	private ArrayList<Head<RK>> rowKeys;
	private ArrayList<Head<CK>> columnKeys;
	private ArrayMatrix<RK, CK, V>.Entry[][] entrys;
	private int size;

	private volatile int modCount = 0;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30.
	 */
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	public ArrayMatrix() {
		this(10);
	}

	public ArrayMatrix(int initialDemension) {
		this(initialDemension, initialDemension);
	}

	@SuppressWarnings("unchecked")
	public ArrayMatrix(int initialRows, int initialColumns) {
		super();
		if (initialRows < 0)
			throw new IllegalArgumentException("Illegal Rows: " + initialRows);
		if (initialColumns < 0)
			throw new IllegalArgumentException("Illegal Columns: "
					+ initialColumns);

		int rowCapacity = Math.min(MAXIMUM_CAPACITY, initialRows);
		int columnCapacity = Math.min(MAXIMUM_CAPACITY, initialColumns);
		rowKeys = new ArrayList<Head<RK>>(rowCapacity);
		columnKeys = new ArrayList<Head<CK>>(columnCapacity);
		entrys = new ArrayMatrix.Entry[rowCapacity][columnCapacity];
	}

	@SuppressWarnings("unchecked")
	public ArrayMatrix(
			Matrix<? extends RK, ? extends CK, ? extends V> otherMatrix) {
		super();
		int row = otherMatrix == null ? 0 : otherMatrix.rows();
		int column = otherMatrix == null ? 0 : otherMatrix.columns();
		int initialRows = Math.max(10, row + (row << 1));
		int initialColumns = Math.max(10, column + (column << 1));

		rowKeys = new ArrayList<Head<RK>>(initialRows);
		columnKeys = new ArrayList<Head<CK>>(initialColumns);
		entrys = new ArrayMatrix.Entry[initialRows][initialColumns];

		this.putAll(otherMatrix);
		// modCount = 0;
	}

	public void trimToSize() {
		this.rowKeys.trimToSize();
		this.columnKeys.trimToSize();

		if (this.rows() < this.rowCapacity()
				|| this.columns() < this.columnCapacity()) {
			@SuppressWarnings("unchecked")
			Entry[][] newEntrys = new ArrayMatrix.Entry[this.rows()][];
			for (int i = 0; i < rows(); ++i) {
				newEntrys[i] = Arrays.copyOf(this.entrys[i], this.columns());
				this.entrys[i] = null;
			}
			this.entrys = newEntrys;
		}
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
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

	private int rowCapacity() {
		return entrys.length;
	}

	private int columnCapacity() {
		if (0 == entrys.length)
			return 0;
		return entrys[0].length;
	}

	private boolean checkIndexOutOfCapacity(int row, int column) {
		return row < 0 || row >= rowCapacity() || column < 0
				|| column >= columnCapacity();
	}

	private boolean checkIndexOutOfSize(int row, int column) {
		return row < 0 || row >= rows() || column < 0 || column >= columns();
	}

	private Head<RK> rowHead(Object row) {
		if (null == row) {
			for (int i = 0; i < rows(); ++i) {
				Head<RK> rowHead = rowKeys.get(i);
				if (null == rowHead.getKey())
					return rowHead;
			}
		} else {
			for (int i = 0; i < rows(); ++i) {
				Head<RK> rowHead = rowKeys.get(i);
				if (row.equals(rowHead.getKey()))
					return rowHead;
			}
		}
		return null;
	}

	private Head<CK> columnHead(Object column) {
		if (null == column) {
			for (int i = 0; i < columns(); ++i) {
				Head<CK> columnHead = columnKeys.get(i);
				if (null == columnHead.getKey())
					return columnHead;
			}
		} else {
			for (int i = 0; i < columns(); ++i) {
				Head<CK> columnHead = columnKeys.get(i);
				if (column.equals(columnHead.getKey()))
					return columnHead;
			}
		}
		return null;
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		return null != rowHead(row);
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		return null != columnHead(column);
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		Head<RK> rowHead = rowHead(row);
		if (null == rowHead)
			return false;
		Head<CK> columnHead = columnHead(column);
		if (null == columnHead)
			return false;
		return null != entrys[rowHead.index][columnHead.index];
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		Head<RK> rowHead = rowHead(row);
		if (null == rowHead)
			return null;
		Head<CK> columnHead = columnHead(column);
		if (null == columnHead)
			return null;
		return null == entrys[rowHead.index][columnHead.index] ? null
				: entrys[rowHead.index][columnHead.index].getValue();
	}

	private Head<RK> addRowIfNotExists(RK row) {
		int i = 0;
		if (null == row) {
			for (; i < rows(); ++i) {
				Head<RK> rowHead = rowKeys.get(i);
				if (null == rowHead.getKey())
					return rowHead;
			}
		} else {
			for (; i < rows(); ++i) {
				Head<RK> rowHead = rowKeys.get(i);
				if (row.equals(rowHead.getKey()))
					return rowHead;
			}
		}
		Head<RK> rowHead = new Head<RK>(row, i);
		rowKeys.add(rowHead);
		return rowHead;
	}

	private Head<CK> addColumnIfNotExists(CK column) {
		int i = 0;
		if (null == column) {
			for (; i < columns(); ++i) {
				Head<CK> rowHead = columnKeys.get(i);
				if (null == rowHead.getKey())
					return rowHead;
			}
		} else {
			for (; i < columns(); ++i) {
				Head<CK> rowHead = columnKeys.get(i);
				if (column.equals(rowHead.getKey()))
					return rowHead;
			}
		}
		Head<CK> columnHead = new Head<CK>(column, i);
		columnKeys.add(columnHead);
		return columnHead;
	}

	@Override
	public V put(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		Head<RK> rowHead = this.addRowIfNotExists(row);
		return setValueInRow(rowHead, column, value);
	}

	private V setValueInRow(Head<RK> rowHead, CK column, V value) {
		if (null == rowHead)
			return null;
		Head<CK> columnHead = addColumnIfNotExists(column);
		return this.setValue(rowHead, columnHead, value);
	}

	private V setValueInColumn(Head<CK> columnHead, RK row, V value) {
		if (null == columnHead)
			return null;
		Head<RK> rowHead = addRowIfNotExists(row);
		return this.setValue(rowHead, columnHead, value);
	}

	private V setValue(Head<RK> rowHead, Head<CK> columnHead, V value) {
		if (checkIndexOutOfCapacity(rowHead.index, columnHead.index)) {
			ensureCapacity(rowHead.index + 1, columnHead.index + 1);
		}

		++modCount;
		if (entrys[rowHead.index][columnHead.index] == null) {
			entrys[rowHead.index][columnHead.index] = new Entry(value, rowHead,
					columnHead);
			++size;
			rowHead.increaseSize(1);
			columnHead.increaseSize(1);
			return null;
		} else {
			return entrys[rowHead.index][columnHead.index].setValue(value);
		}
	}

	public void ensureCapacity(int minDemension) {
		ensureCapacity(minDemension, minDemension);
	}

	@SuppressWarnings("unchecked")
	public void ensureCapacity(int minRow, int minColumn) {
		if (minRow <= this.rowCapacity() && minColumn <= this.columnCapacity())
			return;
		int oldRows = Math.max(this.rows(), 7);
		int oldColumns = Math.max(this.columns(), 7);
		int newRowCapacity = Math.min(MAXIMUM_CAPACITY,
				Math.max(oldRows + (oldRows >> 1), minRow));
		int newColumnCapacity = Math.min(MAXIMUM_CAPACITY,
				Math.max(oldColumns + (oldColumns >> 1), minColumn));

		Entry[][] newEntrys = new ArrayMatrix.Entry[newRowCapacity][];
		int i = 0;
		for (; i < rows(); ++i) {
			newEntrys[i] = Arrays.copyOf(this.entrys[i], newColumnCapacity);
			this.entrys[i] = null;
		}
		for (; i < newEntrys.length; ++i) {
			newEntrys[i] = new ArrayMatrix.Entry[newColumnCapacity];
		}
		this.entrys = newEntrys;
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		Head<RK> rowHead = rowHead(row);
		if (null == rowHead)
			return null;
		Head<CK> columnHead = columnHead(column);
		if (null == columnHead)
			return null;
		return removeElementAt(rowHead.index, rowHead.index);
	}

	@Override
	public void removeRow(Object row) {
		// TODO Auto-generated method stub
		Head<RK> rowHead = rowHead(row);
		if (null == rowHead)
			return;
		for (int i = 0; i < columns(); ++i) {
			this.removeElementAt(rowHead.index, i);
		}
		if(!rowHead.disposed())
			this.removeEmptyRow(rowHead);
	}

	@Override
	public void removeColumn(Object column) {
		// TODO Auto-generated method stub
		Head<CK> columnHead = columnHead(column);
		if (null == columnHead)
			return;
		for (int i = 0; i < rows(); ++i) {
			this.removeElementAt(i, columnHead.index);
		}
		if(!columnHead.disposed())
			this.removeEmptyColumn(columnHead);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		for (int i = 0; i < rows(); ++i) {
			for (int j = 0; j < columns(); ++j) {
				if (this.entrys[i][j] != null) {
					this.entrys[i][j].dispose();
					this.entrys[i][j] = null;
				}
			}
		}
		for (int i = rows() - 1; i >= 0; --i) {
			this.rowKeys.get(i).dispose();
			this.rowKeys.remove(i);
		}
		for (int j = columns() - 1; j >= 0; --j) {
			this.columnKeys.get(j).dispose();
			this.columnKeys.remove(j);
		}
		size = 0;
		++modCount;
	}

	private V removeElementAt(int row, int column) {
		if (this.entrys[row][column] == null) {
			return null;
		} else {
			++modCount;
			--size;
			Entry entry = this.entrys[row][column];
			this.entrys[row][column] = null;
			if (entry.rowHead.increaseSize(-1) == 0) {
				// remove row head
				this.removeEmptyRow(entry.rowHead);
			}
			if (entry.columnHead.increaseSize(-1) == 0) {
				// remove column head
				this.removeEmptyColumn(entry.columnHead);
			}
			V oldValue = entry.getValue();
			entry.dispose();
			return oldValue;
		}
	}

	private void removeEmptyRow(Head<RK> rowHead) {
		if (null == rowHead)
			return;
		rowKeys.remove(rowHead.index);
		for (int i = rowHead.index; i < rows(); ++i) {
			Head<RK> head = rowKeys.get(i);
			head.index = i;

			for (int j = 0; j < columns(); ++j) {
				entrys[i][j] = entrys[i + 1][j];
				entrys[i + 1][j] = null;
			}
		}
		++modCount;
		rowHead.dispose();
	}

	private void removeEmptyColumn(Head<CK> columnHead) {
		if (null == columnHead)
			return;
		columnKeys.remove(columnHead.index);
		for (int i = columnHead.index; i < columns(); ++i) {
			Head<CK> head = columnKeys.get(i);
			head.index = i;

			for (int j = 0; j < rows(); ++j) {
				entrys[j][i] = entrys[j][i + 1];
				entrys[j][i + 1] = null;
			}
		}
		++modCount;
		columnHead.dispose();
	}

	// View
	protected transient volatile Set<Matrix.Entry<RK, CK, V>> entrySet = null;
	protected transient volatile Set<RK> rowKeySet = null;
	protected transient volatile Set<CK> columnKeySet = null;

	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		if (rowKeySet == null) {
			rowKeySet = new AbstractSet<RK>() {

				@Override
				public Iterator<RK> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<RK>() {
						ListIterator<Head<RK>> iterator = ArrayMatrix.this.rowKeys
								.listIterator();
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
							return iterator.next().getKey();
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
					return ArrayMatrix.this.rowKeys.size();
				}

			};
		}
		return rowKeySet;
	}

	@Override
	public Set<CK> columnKeySet() {
		// TODO Auto-generated method stub
		if (columnKeySet == null) {
			columnKeySet = new AbstractSet<CK>() {

				@Override
				public Iterator<CK> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<CK>() {
						ListIterator<Head<CK>> iterator = ArrayMatrix.this.columnKeys
								.listIterator();
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
							return iterator.next().getKey();
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
					return ArrayMatrix.this.columnKeys.size();
				}

			};
		}
		return columnKeySet;
	}

	@Override
	protected int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		Head<RK> rowHead = this.rowHead(row);
		return rowHead == null ? 0 : rowHead.size;
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		Head<CK> columnHead = this.columnHead(column);
		return columnHead == null ? 0 : columnHead.size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<CK, V> rowMap(RK row) {
		// TODO Auto-generated method stub
		Head<RK> head = ArrayMatrix.this.rowHead(row);
		if (null == head)
			return new RowMapView(row, head);

		if (null == head.viewMap) {
			head.viewMap = new RowMapView(row, head);
		}
		return (Map<CK, V>) head.viewMap;
	}

	private class RowMapView extends AbstractMap<CK, V> {
		private Head<RK> head;
		private RK row;

		private RowMapView(RK key, Head<RK> head) {
			this.row = key;
			this.head = head;
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			if (null == head)
				return false;

			Head<CK> columnHead = ArrayMatrix.this.columnHead(key);
			if (null == columnHead)
				return false;

			return null != ArrayMatrix.this.entrys[head.index][columnHead.index];
		}

		@Override
		public V get(Object key) {
			// TODO Auto-generated method stub
			if (null == head)
				return null;

			Head<CK> columnHead = ArrayMatrix.this.columnHead(key);
			if (null == columnHead)
				return null;

			return null == ArrayMatrix.this.entrys[head.index][columnHead.index] ? null
					: ArrayMatrix.this.entrys[head.index][columnHead.index]
							.getValue();
		}

		@Override
		public V put(CK key, V value) {
			// TODO Auto-generated method stub
			if (null == head || head.disposed()) {
				head = ArrayMatrix.this.addRowIfNotExists(row);
				head.viewMap = this;
			}
			return ArrayMatrix.this.setValueInRow(head, key, value);
		}

		// View
		protected transient volatile Set<Map.Entry<CK, V>> entrySet = null;

		@Override
		public Set<Map.Entry<CK, V>> entrySet() {
			// TODO Auto-generated method stub
			if (null == entrySet) {
				entrySet = new AbstractSet<Map.Entry<CK, V>>() {

					@Override
					public Iterator<Map.Entry<CK, V>> iterator() {
						// TODO Auto-generated method stub
						return new Iterator<Map.Entry<CK, V>>() {

							private int currentColumn = -1;
							private int nextColumn = -1;

							private int expectedModCount = ArrayMatrix.this.modCount;

							private void checkModCount() {
								if (expectedModCount != ArrayMatrix.this.modCount)
									throw new ConcurrentModificationException();
							}

							private boolean getNext() {
								checkModCount();
								if (currentColumn < 0) {
									if (null == head)
										return false;
									for (int i = 0; i < columns(); ++i) {
										if (entrys[head.index][i] != null) {
											nextColumn = i;
											break;
										}
									}
								} else {
									for (int i = currentColumn + 1; i < columns(); ++i) {
										if (entrys[head.index][i] != null) {
											nextColumn = i;
											break;
										}
									}
								}
								return nextColumn >= 0
										&& nextColumn < columns();
							}

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								if (nextColumn < 0)
									return getNext();
								else
									return nextColumn < columns();
							}

							@Override
							public Map.Entry<CK, V> next() {
								// TODO Auto-generated method stub
								if (nextColumn < 0 && !getNext())
									throw new NoSuchElementException();
								if (nextColumn > columns()
										|| entrys[head.index][nextColumn] == null)
									throw new NoSuchElementException();
								currentColumn = nextColumn;
								nextColumn = -1;
								return entrys[head.index][currentColumn]
										.rowMapEntry();
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								checkModCount();
								if (currentColumn < 0
										|| entrys[head.index][currentColumn] == null)
									throw new IllegalStateException();
								ArrayMatrix.this.removeElementAt(head.index,
										currentColumn);
								if (head.disposed()) {
									head = null;
									currentColumn = nextColumn = -1;
								}
								expectedModCount = ArrayMatrix.this.modCount;
							}

						};
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return head == null ? 0 : head.size;
					}

				};
			}
			return entrySet;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return head == null ? 0 : head.size;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<RK, V> columnMap(CK column) {
		// TODO Auto-generated method stub
		Head<CK> head = ArrayMatrix.this.columnHead(column);
		if (null == head)
			return new ColumnMapView(column, head);
		if (null == head.viewMap) {
			head.viewMap = new ColumnMapView(column, head);
		}
		return (Map<RK, V>) head.viewMap;
	}

	private class ColumnMapView extends AbstractMap<RK, V> {
		private Head<CK> head;
		private CK column;

		private ColumnMapView(CK key, Head<CK> head) {
			this.column = key;
			this.head = head;
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			if (null == head)
				return false;

			Head<RK> rowHead = ArrayMatrix.this.rowHead(key);
			if (null == rowHead)
				return false;

			return null != ArrayMatrix.this.entrys[rowHead.index][head.index];
		}

		@Override
		public V get(Object key) {
			// TODO Auto-generated method stub
			if (null == head)
				return null;

			Head<RK> rowHead = ArrayMatrix.this.rowHead(key);
			if (null == rowHead)
				return null;

			return null == ArrayMatrix.this.entrys[rowHead.index][head.index] ? null
					: ArrayMatrix.this.entrys[rowHead.index][head.index]
							.getValue();
		}

		@Override
		public V put(RK key, V value) {
			// TODO Auto-generated method stub
			if (null == head || head.disposed()) {
				head = ArrayMatrix.this.addColumnIfNotExists(column);
				head.viewMap = this;
			}
			return ArrayMatrix.this.setValueInColumn(head, key, value);
		}

		// View
		protected transient volatile Set<Map.Entry<RK, V>> entrySet = null;

		@Override
		public Set<Map.Entry<RK, V>> entrySet() {
			// TODO Auto-generated method stub
			if (null == entrySet) {
				entrySet = new AbstractSet<Map.Entry<RK, V>>() {

					@Override
					public Iterator<Map.Entry<RK, V>> iterator() {
						// TODO Auto-generated method stub
						return new Iterator<Map.Entry<RK, V>>() {

							private int currentRow = -1;
							private int nextRow = -1;

							private int expectedModCount = ArrayMatrix.this.modCount;

							private void checkModCount() {
								if (expectedModCount != ArrayMatrix.this.modCount)
									throw new ConcurrentModificationException();
							}

							private boolean getNext() {
								checkModCount();
								if (currentRow < 0) {
									if (null == head)
										return false;
									for (int i = 0; i < rows(); ++i) {
										if (entrys[i][head.index] != null) {
											nextRow = i;
											break;
										}
									}
								} else {
									for (int i = currentRow + 1; i < rows(); ++i) {
										if (entrys[i][head.index] != null) {
											nextRow = i;
											break;
										}
									}
								}
								return nextRow >= 0 && nextRow < rows();
							}

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								if (nextRow < 0)
									return getNext();
								else
									return nextRow < rows();
							}

							@Override
							public Map.Entry<RK, V> next() {
								// TODO Auto-generated method stub
								if (nextRow < 0 && !getNext())
									throw new NoSuchElementException();
								if (nextRow > rows()
										|| entrys[nextRow][head.index] == null)
									throw new NoSuchElementException();
								currentRow = nextRow;
								nextRow = -1;
								return entrys[currentRow][head.index]
										.columnMapEntry();
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								checkModCount();
								if (currentRow < 0
										|| entrys[currentRow][head.index] == null)
									throw new IllegalStateException();
								ArrayMatrix.this.removeElementAt(currentRow,
										head.index);
								if (head.disposed()) {
									head = null;
									currentRow = nextRow = -1;
								}
								expectedModCount = ArrayMatrix.this.modCount;
							}

						};
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return head == null ? 0 : head.size;
					}

				};
			}
			return entrySet;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return head == null ? 0 : head.size;
		}
	}

	@Override
	public Set<Matrix.Entry<RK, CK, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {
			entrySet = new EntrySet();
		}
		return entrySet;
	}

	private class EntrySet extends AbstractSet<Matrix.Entry<RK, CK, V>> {
		private EntrySet() {
			super();
		}

		@Override
		public Iterator<Matrix.Entry<RK, CK, V>> iterator() {
			// TODO Auto-generated method stub
			return new EntryIterator();
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return ArrayMatrix.this.size;
		}

	}

	private class EntryIterator implements Iterator<Matrix.Entry<RK, CK, V>> {
		private int expectedModCount = ArrayMatrix.this.modCount;
		private boolean entryRemoved = false;
		private int rowIndex = 0, columnIndex = -1;
		private int nextRowIndex = -1, nextColumnIndex = -1;

		private EntryIterator() {
			this.expectedModCount = ArrayMatrix.this.modCount;
			this.getNextEntryIndex();
		}

		private void checkModified() {
			if (expectedModCount != ArrayMatrix.this.modCount)
				throw new ConcurrentModificationException();
		}

		private boolean nextEntryHasGot() {
			return nextRowIndex >= 0 && nextColumnIndex >= 0;
		}

		private boolean getNextEntryIndex() {
			checkModified();
			nextRowIndex = rowIndex;
			nextColumnIndex = columnIndex;
			do {
				if (nextColumnIndex + 1 >= ArrayMatrix.this.columns()) {
					++nextRowIndex;
					nextColumnIndex = 0;
				} else {
					++nextColumnIndex;
				}
			} while (!ArrayMatrix.this.checkIndexOutOfSize(nextRowIndex,
					nextColumnIndex) && nextEntry() == null);
			return !ArrayMatrix.this.checkIndexOutOfSize(nextRowIndex,
					nextColumnIndex);
		}

		private Matrix.Entry<RK, CK, V> nextEntry() {
			return ArrayMatrix.this.entrys[nextRowIndex][nextColumnIndex];
		}
		
		private void clearNext(){
			nextRowIndex = nextColumnIndex = -1;
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			if (nextEntryHasGot())
				return !ArrayMatrix.this.checkIndexOutOfSize(nextRowIndex,
						nextColumnIndex);
			else
				return getNextEntryIndex();
		}

		@Override
		public Matrix.Entry<RK, CK, V> next() {
			// TODO Auto-generated method stub
			if (!nextEntryHasGot()) {
				if (!getNextEntryIndex())
					throw new NoSuchElementException();
			} else if (ArrayMatrix.this.checkIndexOutOfSize(nextRowIndex,
					nextColumnIndex)) {
				throw new NoSuchElementException();
			}

			rowIndex = nextRowIndex;
			columnIndex = nextColumnIndex;
			clearNext();
			this.getNextEntryIndex();
			entryRemoved = false;
			return ArrayMatrix.this.entrys[rowIndex][columnIndex];
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			checkModified();
			if (!entryRemoved) {
				Head<RK> rowHead = ArrayMatrix.this.entrys[rowIndex][columnIndex].rowHead;
				Head<CK> columnHead = ArrayMatrix.this.entrys[rowIndex][columnIndex].columnHead;
				ArrayMatrix.this.removeElementAt(rowIndex, columnIndex);
				if(rowHead.disposed()){
					columnIndex = -1;
					clearNext();
				}
				if(columnHead.disposed()){
					if(columnIndex >= 0)
						--columnIndex;
					clearNext();
				}
				entryRemoved = true;
				this.expectedModCount = ArrayMatrix.this.modCount;
			} else {
				throw new IllegalStateException();
			}
		}

	}

	private class Entry extends AbstractEntry<RK, CK, V> implements
			Matrix.Entry<RK, CK, V> {
		private V value;
		private Head<RK> rowHead;
		private Head<CK> columnHead;

		private Entry(V value, Head<RK> rowHead, Head<CK> columnHead) {
			this.value = value;
			this.rowHead = rowHead;
			this.columnHead = columnHead;
		}

		@Override
		public RK getRowKey() {
			// TODO Auto-generated method stub
			return this.rowHead.getKey();
		}

		@Override
		public CK getColumnKey() {
			// TODO Auto-generated method stub
			return this.columnHead.getKey();
		}

		@Override
		public V getValue() {
			// TODO Auto-generated method stub
			return this.value;
		}

		@Override
		public V setValue(V value) {
			// TODO Auto-generated method stub
			V old = this.value;
			this.value = value;
			return old;
		}

		@Override
		protected void dispose() {
			super.dispose();
			this.value = null;
			this.rowHead = null;
			this.columnHead = null;
		}

	}

	private class Head<K> {
		private K key;
		private int index;
		private int size = 0;

		// View
		protected transient volatile Map<?, V> viewMap = null;

		private Head(K key, int index) {
			this.key = key;
			this.index = index;
		}

		private int increaseSize(int incr) {
			size = Math.max(0, size + incr);
			return size;
		}

		private K getKey() {
			return this.key;
		}

		private void dispose() {
			key = null;
			index = -1;
			size = 0;

			viewMap = null;
		}

		private boolean disposed() {
			return index < 0;
		}

		@Override
		public String toString() {
			return "Head [key=" + key + ", index=" + index + ", size=" + size
					+ "]";
		}
	}

}
