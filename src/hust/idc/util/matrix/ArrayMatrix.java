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
		Matrix<RK, CK, V>, Cloneable, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 78725161200911574L;

	transient ArrayList<Head<RK>> rowHeads;
	transient ArrayList<Head<CK>> columnHeads;
	transient ArrayMatrix<RK, CK, V>.Entry[][] entrys;
	int size;

	transient volatile int modCount = 0;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	public ArrayMatrix() {
		this(10);
	}

	public ArrayMatrix(int initialDemension) {
		this(initialDemension, initialDemension);
	}

	public ArrayMatrix(int initialRows, int initialColumns) {
		super();
		if (initialRows < 0)
			throw new IllegalArgumentException("Illegal Rows: " + initialRows);
		if (initialColumns < 0)
			throw new IllegalArgumentException("Illegal Columns: "
					+ initialColumns);

		int rowCapacity = Math.min(MAXIMUM_CAPACITY, initialRows);
		int columnCapacity = Math.min(MAXIMUM_CAPACITY, initialColumns);
		this.initArrays(rowCapacity, columnCapacity);
	}

	public ArrayMatrix(
			Matrix<? extends RK, ? extends CK, ? extends V> otherMatrix) {
		super();
		int row = otherMatrix == null ? 0 : otherMatrix.rows();
		int column = otherMatrix == null ? 0 : otherMatrix.columns();
		int initialRows = Math.max(10, row + (row << 1));
		int initialColumns = Math.max(10, column + (column << 1));

		this.initArrays(initialRows, initialColumns);

		this.putAll(otherMatrix, false);
		// modCount = 0;
	}

	@SuppressWarnings("unchecked")
	void initArrays(int rowCapacity, int columnCapacity) {
		rowHeads = new ArrayList<Head<RK>>(rowCapacity);
		columnHeads = new ArrayList<Head<CK>>(columnCapacity);
		entrys = new ArrayMatrix.Entry[rowCapacity][columnCapacity];
	}

	public void trimToSize() {
		this.rowHeads.trimToSize();
		this.columnHeads.trimToSize();

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
		return rowHeads.size();
	}

	@Override
	public int columns() {
		// TODO Auto-generated method stub
		return columnHeads.size();
	}

	int rowCapacity() {
		return entrys.length;
	}

	int columnCapacity() {
		if (0 == entrys.length)
			return 0;
		return entrys[0].length;
	}

	boolean checkIndexOutOfCapacity(int row, int column) {
		return row < 0 || row >= rowCapacity() || column < 0
				|| column >= columnCapacity();
	}

	boolean checkIndexOutOfSize(int row, int column) {
		return row < 0 || row >= rows() || column < 0 || column >= columns();
	}

	Head<RK> rowHead(Object row) {
		if (null == row) {
			for (int i = 0; i < rows(); ++i) {
				Head<RK> rowHead = rowHeads.get(i);
				if (null == rowHead.getKey())
					return rowHead;
			}
		} else {
			for (int i = 0; i < rows(); ++i) {
				Head<RK> rowHead = rowHeads.get(i);
				if (row.equals(rowHead.getKey()))
					return rowHead;
			}
		}
		return null;
	}

	Head<CK> columnHead(Object column) {
		if (null == column) {
			for (int i = 0; i < columns(); ++i) {
				Head<CK> columnHead = columnHeads.get(i);
				if (null == columnHead.getKey())
					return columnHead;
			}
		} else {
			for (int i = 0; i < columns(); ++i) {
				Head<CK> columnHead = columnHeads.get(i);
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
				Head<RK> rowHead = rowHeads.get(i);
				if (null == rowHead.getKey())
					return rowHead;
			}
		} else {
			for (; i < rows(); ++i) {
				Head<RK> rowHead = rowHeads.get(i);
				if (row.equals(rowHead.getKey()))
					return rowHead;
			}
		}
		Head<RK> rowHead = new Head<RK>(row, i);
		rowHeads.add(rowHead);
		return rowHead;
	}

	private Head<CK> addColumnIfNotExists(CK column) {
		int i = 0;
		if (null == column) {
			for (; i < columns(); ++i) {
				Head<CK> rowHead = columnHeads.get(i);
				if (null == rowHead.getKey())
					return rowHead;
			}
		} else {
			for (; i < columns(); ++i) {
				Head<CK> rowHead = columnHeads.get(i);
				if (column.equals(rowHead.getKey()))
					return rowHead;
			}
		}
		Head<CK> columnHead = new Head<CK>(column, i);
		columnHeads.add(columnHead);
		return columnHead;
	}

	@Override
	public V put(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		return put(row, column, value, true);
	}

	@Override
	public void putAll(Matrix<? extends RK, ? extends CK, ? extends V> matrix) {
		// TODO Auto-generated method stub
		putAll(matrix, true);
	}

	private V put(RK row, CK column, V value, boolean resize) {
		Head<RK> rowHead = this.addRowIfNotExists(row);
		return setValueInRow(rowHead, column, value, resize);
	}

	private void putAll(Matrix<? extends RK, ? extends CK, ? extends V> matrix,
			boolean resize) {
		// TODO Auto-generated method stub
		Iterator<? extends Matrix.Entry<? extends RK, ? extends CK, ? extends V>> entryIt = matrix
				.entrySet().iterator();
		while (entryIt.hasNext()) {
			Matrix.Entry<? extends RK, ? extends CK, ? extends V> entry = entryIt
					.next();
			put(entry.getRowKey(), entry.getColumnKey(), entry.getValue(),
					resize);
		}
	}

	private V setValueInRow(Head<RK> rowHead, CK column, V value, boolean resize) {
		if (null == rowHead)
			return null;
		Head<CK> columnHead = addColumnIfNotExists(column);
		return this.setValue(rowHead, columnHead, value, resize);
	}

	private V setValueInColumn(Head<CK> columnHead, RK row, V value,
			boolean resize) {
		if (null == columnHead)
			return null;
		Head<RK> rowHead = addRowIfNotExists(row);
		return this.setValue(rowHead, columnHead, value, resize);
	}

	private V setValue(Head<RK> rowHead, Head<CK> columnHead, V value,
			boolean resize) {
		if (resize && checkIndexOutOfCapacity(rowHead.index, columnHead.index)) {
			ensureCapacity(rowHead.index + 1, columnHead.index + 1);
		}

		if (entrys[rowHead.index][columnHead.index] == null) {
			++modCount;
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
		if (!rowHead.disposed())
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
		if (!columnHead.disposed())
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
			this.rowHeads.get(i).dispose();
			this.rowHeads.remove(i);
		}
		for (int j = columns() - 1; j >= 0; --j) {
			this.columnHeads.get(j).dispose();
			this.columnHeads.remove(j);
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
		rowHeads.remove(rowHead.index);
		for (int i = rowHead.index; i < rows(); ++i) {
			Head<RK> head = rowHeads.get(i);
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
		columnHeads.remove(columnHead.index);
		for (int i = columnHead.index; i < columns(); ++i) {
			Head<CK> head = columnHeads.get(i);
			head.index = i;

			for (int j = 0; j < rows(); ++j) {
				entrys[j][i] = entrys[j][i + 1];
				entrys[j][i + 1] = null;
			}
		}
		++modCount;
		columnHead.dispose();
	}

	/**
	 * Returns a shallow copy of this <tt>ArrayMatrix</tt> instance: the keys
	 * and values themselves are not cloned.
	 * 
	 * @return a shallow copy of this matrix
	 */
	@Override
	public ArrayMatrix<RK, CK, V> clone() {
		// TODO Auto-generated method stub
		try {
			ArrayMatrix<RK, CK, V> v = (ArrayMatrix<RK, CK, V>) super.clone();
			v.size = 0;
			v.initArrays(this.rowCapacity(), this.columnCapacity());
			v.putAll(this, false);
			v.modCount = 0;
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			e.printStackTrace();
			throw new InternalError();
		}
	}

	@Override
	void clearViews() {
		super.clearViews();
		rowKeySet = null;
		columnKeySet = null;
		entrySet = null;
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
						ListIterator<Head<RK>> iterator = ArrayMatrix.this.rowHeads
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
					return ArrayMatrix.this.rowHeads.size();
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
						ListIterator<Head<CK>> iterator = ArrayMatrix.this.columnHeads
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
					return ArrayMatrix.this.columnHeads.size();
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

	private abstract class MapView<K1, K2> extends AbstractMap<K2, V> {
		transient volatile Head<K1> head;
		final transient K1 viewKey;

		// View
		transient volatile Set<Map.Entry<K2, V>> entrySet = null;

		MapView(K1 key, Head<K1> head) {
			this.viewKey = key;
			this.head = head;
		}

		final int modCount() {
			return headNotExists() ? -1 : head.modCount;
		}

		final boolean headNotExists() {
			if (head != null && head.disposed())
				head = null;
			return head == null;
		}
		
		@Override
		public int size() {
			// TODO Auto-generated method stub
			validateHead();
			return head == null ? 0 : head.size;
		}

		@Override
		public Set<Map.Entry<K2, V>> entrySet() {
			validateHead();
			if (entrySet == null) {
				entrySet = new AbstractSet<Map.Entry<K2, V>>() {

					@Override
					public Iterator<Map.Entry<K2, V>> iterator() {
						// TODO Auto-generated method stub
						validateHead();
						return entryIterator();
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return MapView.this.size();
					}

				};
			}
			return entrySet;
		}

		void validateHead() {
			if (headNotExists()) {
				resetHead();
				if (head != null && head.viewMap == null)
					head.viewMap = this;
			}
		}

		abstract Iterator<Map.Entry<K2, V>> entryIterator();
		
		abstract void resetHead();

	}

	private class RowMapView extends MapView<RK, CK> {

		private RowMapView(RK key, Head<RK> head) {
			super(key, head);
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			validateHead();
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
			validateHead();
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
			validateHead();
			if (null == head) {
				head = ArrayMatrix.this.addRowIfNotExists(viewKey);
				head.viewMap = this;
			}
			return ArrayMatrix.this.setValueInRow(head, key, value, true);
		}

		@Override
		Iterator<java.util.Map.Entry<CK, V>> entryIterator() {
			// TODO Auto-generated method stub
			return new Iterator<Map.Entry<CK, V>>() {

				private int currentColumn = -1;
				private int nextColumn = -1;

				private int expectedModCount = RowMapView.this.modCount();

				final void checkModCount() {
					if (expectedModCount != RowMapView.this.modCount())
						throw new ConcurrentModificationException();
				}

				final boolean advance() {
					checkModCount();
					if(RowMapView.this.headNotExists()){
						return false;
					} else if (currentColumn < 0) {
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
					return nextColumn >= 0 && nextColumn < columns();
				}

				@Override
				public boolean hasNext() {
					// TODO Auto-generated method stub
					if (nextColumn < 0)
						return advance();
					else
						return nextColumn < columns();
				}

				@Override
				public Map.Entry<CK, V> next() {
					// TODO Auto-generated method stub
					if (nextColumn < 0 && !advance())
						throw new NoSuchElementException();
					if (nextColumn > columns()
							|| entrys[head.index][nextColumn] == null)
						throw new NoSuchElementException();
					currentColumn = nextColumn;
					nextColumn = -1;
					return entrys[head.index][currentColumn].rowMapEntry();
				}

				@Override
				public void remove() {
					// TODO Auto-generated method stub
					checkModCount();
					if (currentColumn < 0
							|| entrys[head.index][currentColumn] == null)
						throw new IllegalStateException();
					ArrayMatrix.this.removeElementAt(head.index, currentColumn);
					if (head.disposed()) {
						head = null;
						currentColumn = nextColumn = -1;
					}
					expectedModCount = RowMapView.this.modCount();
				}

			};
		}

		@Override
		void resetHead() {
			// TODO Auto-generated method stub
			head = ArrayMatrix.this.rowHead(viewKey);
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

	private class ColumnMapView extends MapView<CK, RK> {

		private ColumnMapView(CK key, Head<CK> head) {
			super(key, head);
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			validateHead();
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
			validateHead();
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
			validateHead();
			if (null == head) {
				head = ArrayMatrix.this.addColumnIfNotExists(viewKey);
				head.viewMap = this;
			}
			return ArrayMatrix.this.setValueInColumn(head, key, value, true);
		}

		@Override
		Iterator<java.util.Map.Entry<RK, V>> entryIterator() {
			// TODO Auto-generated method stub
			return new Iterator<Map.Entry<RK, V>>() {

				private int currentRow = -1;
				private int nextRow = -1;

				private int expectedModCount = ColumnMapView.this.modCount();

				private void checkModCount() {
					if (expectedModCount != ColumnMapView.this.modCount())
						throw new ConcurrentModificationException();
				}

				private boolean getNext() {
					checkModCount();
					if(ColumnMapView.this.headNotExists()){
						return false;
					} else if (currentRow < 0) {
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
					expectedModCount = ColumnMapView.this.modCount();
				}

			};
		}

		@Override
		void resetHead() {
			// TODO Auto-generated method stub
			head = ArrayMatrix.this.columnHead(viewKey);
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

	private final class EntrySet extends AbstractSet<Matrix.Entry<RK, CK, V>> {
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

	private final class EntryIterator implements
			Iterator<Matrix.Entry<RK, CK, V>> {
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

		private void clearNext() {
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
				if (rowHead.disposed()) {
					columnIndex = -1;
					clearNext();
				}
				if (columnHead.disposed()) {
					if (columnIndex >= 0)
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
		private transient Head<RK> rowHead;
		private transient Head<CK> columnHead;

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
		void dispose() {
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
		transient volatile int modCount;

		// View
		protected transient volatile Map<?, V> viewMap = null;

		private Head(K key, int index) {
			this.key = key;
			this.index = index;
		}

		private int increaseSize(int incr) {
			if (incr == 0)
				return size;
			++modCount;
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
			modCount = -1;
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

	/**
	 * Save the state of the <tt>ArrayMatrix</tt> instance to a stream (that is,
	 * serialize it).
	 * 
	 * @serialData The length of the array backing the <tt>ArrayMatrix</tt>
	 *             instance is emitted (int), followed by all of its keys and
	 *             values (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();

		// Write out array length
		s.writeInt(this.rowCapacity());
		s.writeInt(this.columnCapacity());

		// Write row keys
		s.writeInt(rows());
		for (int i = 0; i < rowHeads.size(); ++i) {
			s.writeObject(rowHeads.get(i).getKey());
		}

		// Write column keys
		s.writeInt(columns());
		for (int i = 0; i < columnHeads.size(); ++i) {
			s.writeObject(columnHeads.get(i).getKey());
		}

		// Write out all values in the proper order.
		for (int i = 0; i < rows(); ++i) {
			for (int j = 0; j < columns(); ++j) {
				boolean hasEntry = null != entrys[i][j];
				s.writeBoolean(hasEntry);
				if (hasEntry)
					s.writeObject(entrys[i][j].getValue());
			}
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}

	}

	/**
	 * Reconstitute the <tt>ArrayMatrix</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();

		// Read in array length and allocate array
		int rowCapacity = s.readInt();
		int columnCapacity = s.readInt();

		this.initArrays(rowCapacity, columnCapacity);

		// read row keys
		int rows = s.readInt();
		for (int i = 0; i < rows; ++i) {
			RK rowKey = (RK) s.readObject();
			rowHeads.add(new Head<RK>(rowKey, i));
		}

		// read column keys
		int columns = s.readInt();
		for (int i = 0; i < columns; ++i) {
			CK columnKey = (CK) s.readObject();
			columnHeads.add(new Head<CK>(columnKey, i));
		}

		// Read in all elements in the proper order.
		for (int i = 0; i < rows; i++) {
			Head<RK> rowHead = rowHeads.get(i);
			for (int j = 0; j < columns; ++j) {
				boolean hasEntry = s.readBoolean();
				if (hasEntry) {
					V value = (V) s.readObject();
					entrys[i][j] = new Entry(value, rowHead, columnHeads.get(j));
				}
			}
		}
	}

}
