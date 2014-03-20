package hust.idc.util.matrix;

import hust.idc.util.pair.Pair;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class LinkedMatrix<RK, CK, V> extends AbstractMatrix<RK, CK, V>
		implements Matrix<RK, CK, V>, Cloneable, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1787082352531372102L;
	transient HeadNode<RK> rowHeadsEntry;
	transient HeadNode<CK> columnHeadsEntry;
	transient int size, rows, columns;

	transient volatile int modCount = 0;

	public LinkedMatrix() {
		super();
		rowHeadsEntry = null;
		columnHeadsEntry = null;
		size = 0;
		rows = columns = 0;
	}

	public LinkedMatrix(
			Matrix<? extends RK, ? extends CK, ? extends V> otherMatrix) {
		this();
		this.putAll(otherMatrix);
		// modCount = 0;
	}

	public LinkedMatrix(
			Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> otherMatrix) {
		this();
		this.putAll(otherMatrix);
		// modCount = 0;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public int rows() {
		// TODO Auto-generated method stub
		return rows;
	}

	@Override
	public int columns() {
		// TODO Auto-generated method stub
		return columns;
	}

	HeadNode<RK> rowHead(Object row) {
		HeadNode<RK> head = rowHeadsEntry;
		if (row == null) {
			while (head != null) {
				if (head.getKey() == null)
					return head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (row.equals(head.getKey()))
					return head;
				head = head.next;
			}
		}
		return null;
	}

	HeadNode<CK> columnHead(Object column) {
		HeadNode<CK> head = columnHeadsEntry;
		if (column == null) {
			while (head != null) {
				if (head.getKey() == null)
					return head;
				head = head.next;
			}
		} else {
			while (head != null) {
				if (column.equals(head.getKey()))
					return head;
				head = head.next;
			}
		}
		return null;
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		return rowHead(row) != null;
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		return columnHead(column) != null;
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		return getEntry(row, column) != null;
	}

	EntryNode getEntry(Object row, Object column) {
		HeadNode<RK> rowHead = rowHead(row);
		if (rowHead == null)
			return null;

		EntryNode node = rowHead.entry();
		if (column == null) {
			while (node != null) {
				if (node.getColumnKey() == null)
					return node;
				node = node.right;
			}
		} else {
			while (node != null) {
				if (column.equals(node.getColumnKey()))
					return node;
				node = node.right;
			}
		}
		return null;
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		EntryNode node = getEntry(row, column);
		return node == null ? null : node.getValue();
	}

	@Override
	public V put(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		return setValueInRow(addRowIfNotExists(row), column, value);
	}

	private V setValueInRow(HeadNode<RK> rowHead, CK column, V value) {
		if (rowHead == null)
			return null;
		HeadNode<CK> columnHead = addColumnIfNotExists(column);
		return this.setValue(rowHead, columnHead, value);
	}

	private V setValueInColumn(HeadNode<CK> columnHead, RK row, V value) {
		if (columnHead == null)
			return null;
		HeadNode<RK> rowHead = addRowIfNotExists(row);
		return this.setValue(rowHead, columnHead, value);
	}

	private V setValue(HeadNode<RK> rowHead, HeadNode<CK> columnHead, V value) {
		if (rowHead == null || columnHead == null)
			return null;

		++modCount;
		EntryNode rowCur = rowHead.entry(), left = null;
		while (rowCur != null && rowCur.columnHead().index < columnHead.index) {
			left = rowCur;
			rowCur = rowCur.right;
		}

		EntryNode columnCur = columnHead.entry(), up = null;
		while (columnCur != null && columnCur.rowHead().index < rowHead.index) {
			up = columnCur;
			columnCur = columnCur.lower;
		}

		if (rowCur == null || rowCur.columnHead().index > columnHead.index) {
			if (columnCur == null || columnCur.rowHead().index > rowHead.index) {
				EntryNode newNode = new EntryNode(value, rowHead, columnHead);
				newNode.left = left;
				newNode.right = rowCur;
				newNode.upper = up;
				newNode.lower = columnCur;

				// link left and right
				if (left == null)
					rowHead.entry = newNode;
				else
					left.right = newNode;
				if (rowCur != null)
					rowCur.left = newNode;

				// link up and down
				if (up == null)
					columnHead.entry = newNode;
				else
					up.lower = newNode;
				if (columnCur != null)
					columnCur.upper = newNode;

				rowHead.addSize(1);
				columnHead.addSize(1);
				++size;
				return null;
			} else {
				if (left == null)
					rowHead.entry = columnCur;
				else
					left.right = columnCur;
				if (rowCur != null)
					rowCur.left = columnCur;

				columnCur.rowHead = rowHead;
				columnCur.left = left;
				columnCur.right = rowCur;
				return columnCur.setValue(value);
			}
		} else {
			if (columnCur == rowCur) {
				return rowCur.setValue(value);
			} else {
				if (up == null)
					columnHead.entry = rowCur;
				else
					up.lower = rowCur;
				if (columnCur != null)
					columnCur.upper = rowCur;

				rowCur.columnHead = columnHead;
				rowCur.lower = columnCur;
				rowCur.upper = up;
				return rowCur.setValue(value);
			}
		}
	}

	private HeadNode<RK> addRowIfNotExists(RK row) {
		if (rowHeadsEntry == null) {
			rowHeadsEntry = new HeadNode<RK>(row, 0);
			rows = 1;
			return rowHeadsEntry;
		}

		HeadNode<RK> head = rowHeadsEntry;
		if (row == null) {
			if (head.getKey() == null)
				return head;
			while (head.next != null) {
				head = head.next;
				if (head.getKey() == null)
					return head;
			}
		} else {
			if (row.equals(head.getKey()))
				return head;
			while (head.next != null) {
				head = head.next;
				if (row.equals(head.getKey()))
					return head;
			}
		}

		head.next = new HeadNode<RK>(row, head.index + 1);
		head.next.prev = head;
		++rows;
		return head.next;
	}

	private HeadNode<CK> addColumnIfNotExists(CK column) {
		if (columnHeadsEntry == null) {
			columnHeadsEntry = new HeadNode<CK>(column, 0);
			columns = 1;
			return columnHeadsEntry;
		}

		HeadNode<CK> head = columnHeadsEntry;
		if (column == null) {
			if (head.getKey() == null)
				return head;
			while (head.next != null) {
				head = head.next;
				if (head.getKey() == null)
					return head;
			}
		} else {
			if (column.equals(head.getKey()))
				return head;
			while (head.next != null) {
				head = head.next;
				if (column.equals(head.getKey()))
					return head;
			}
		}

		head.next = new HeadNode<CK>(column, head.index + 1);
		head.next.prev = head;
		++columns;
		return head.next;
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		return this.removeNode(getEntry(row, column));
	}

	private V removeNode(EntryNode node) {
		if (node == null)
			return null;

		if (node.left == null)
			node.rowHead.entry = node.right;
		else
			node.left.right = node.right;
		if (node.right != null)
			node.right.left = node.left;

		if (node.upper == null)
			node.columnHead.entry = node.lower;
		else
			node.upper.lower = node.lower;
		if (node.lower != null)
			node.lower.upper = node.upper;

		if (node.rowHead.addSize(-1) == 0) {
			this.removeRowHead(node.rowHead);
		}
		if (node.columnHead.addSize(-1) == 0) {
			this.removeColumnHead(node.columnHead);
		}
		V value = node.value;
		node.dispose();
		++modCount;
		--size;
		return value;
	}

	@Override
	public void removeRow(RK row) {
		// TODO Auto-generated method stub
		this.removeRow(rowHead(row));
	}

	@Override
	public void removeColumn(CK column) {
		// TODO Auto-generated method stub
		this.removeColumn(columnHead(column));
	}

	private void removeRow(HeadNode<RK> rowHead) {
		if (rowHead == null)
			return;
		EntryNode node = rowHead.entry(), next = null;
		while (node != null) {
			next = node.right;
			this.removeNode(node);
			node = next;
		}
		++modCount;
		if (!rowHead.disposed())
			this.removeRowHead(rowHead);
	}

	private void removeColumn(HeadNode<CK> columnHead) {
		if (columnHead == null)
			return;
		EntryNode node = columnHead.entry(), next = null;
		while (node != null) {
			next = node.lower;
			this.removeNode(node);
			node = next;
		}
		++modCount;
		if (!columnHead.disposed())
			this.removeColumnHead(columnHead);
	}

	private void removeRowHead(HeadNode<RK> rowHead) {
		if (rowHead == null)
			return;

		if (rowHead.prev == null)
			rowHeadsEntry = rowHead.next;
		else
			rowHead.prev.next = rowHead.next;
		if (rowHead.next != null)
			rowHead.next.prev = rowHead.prev;
		rowHead.dispose();
		--rows;
	}

	private void removeColumnHead(HeadNode<CK> columnHead) {
		if (columnHead == null)
			return;

		if (columnHead.prev == null)
			columnHeadsEntry = columnHead.next;
		else
			columnHead.prev.next = columnHead.next;
		if (columnHead.next != null)
			columnHead.next.prev = columnHead.prev;
		columnHead.dispose();
		--columns;
	}

	/**
	 * Returns a shallow copy of this <tt>LinkedMatrix</tt> instance: the keys and
	 * values themselves are not cloned.
	 * 
	 * @return a shallow copy of this matrix
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LinkedMatrix<RK, CK, V> clone() {
		// TODO Auto-generated method stub
		try {
			LinkedMatrix<RK, CK, V> v = (LinkedMatrix<RK, CK, V>) super.clone();
			v.clearViews();
			v.size = v.rows = v.columns = 0;
			v.rowHeadsEntry = null;
			v.columnHeadsEntry = null;
		    v.putAll(this);
		    v.modCount = 0;
		    return v;
		} catch (CloneNotSupportedException e) {
		    // this shouldn't happen, since we are Cloneable
			e.printStackTrace();
		    throw new InternalError();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<CK, V> rowMap(RK row) {
		// TODO Auto-generated method stub
		HeadNode<RK> head = this.rowHead(row);
		if (null == head)
			return new RowMapView(row, head);

		if (null == head.viewMap) {
			head.viewMap = new RowMapView(row, head);
		}
		return (Map<CK, V>) head.viewMap;
	}

	private class RowMapView extends AbstractMap<CK, V> {
		private transient volatile HeadNode<RK> head;
		private final transient RK row;

		private RowMapView(RK key, HeadNode<RK> head) {
			this.row = key;
			this.head = head;
		}

		@Override
		public V put(CK key, V value) {
			// TODO Auto-generated method stub
			if (null == head || head.disposed()) {
				head = LinkedMatrix.this.addRowIfNotExists(row);
				head.viewMap = this;
			}
			return LinkedMatrix.this.setValueInRow(head, key, value);
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
							private EntryNode current = null;
							private boolean currentRemoved = false;

							private int expectedModCount = LinkedMatrix.this.modCount;

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								checkModCount();
								if (current == null)
									return head != null && head.entry != null;
								else
									return current.right != null;
							}

							private boolean getNext() {
								checkModCount();
								if (current == null) {
									if (head == null)
										return false;
									return (current = head.entry) != null;
								} else {
									if (current.right == null)
										return false;
									current = current.right;
									return true;
								}
							}

							private void checkModCount() {
								if (expectedModCount != LinkedMatrix.this.modCount)
									throw new ConcurrentModificationException();
							}

							@Override
							public Map.Entry<CK, V> next() {
								// TODO Auto-generated method stub
								if (!getNext())
									throw new NoSuchElementException();
								currentRemoved = false;
								return current.rowMapEntry();
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								checkModCount();
								if (currentRemoved || current == null)
									throw new IllegalStateException();
								EntryNode left = current.left;
								LinkedMatrix.this.removeNode(current);
								if (head.disposed()) {
									current = null;
									head = null;
								} else {
									current = left;
								}
								currentRemoved = true;
								expectedModCount = LinkedMatrix.this.modCount;
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
		HeadNode<CK> head = LinkedMatrix.this.columnHead(column);
		if (null == head)
			return new ColumnMapView(column, head);

		if (null == head.viewMap) {
			head.viewMap = new ColumnMapView(column, head);
		}
		return (Map<RK, V>) head.viewMap;
	}

	private class ColumnMapView extends AbstractMap<RK, V> {
		private transient volatile HeadNode<CK> head;
		private final transient CK column;

		private ColumnMapView(CK key, HeadNode<CK> head) {
			this.column = key;
			this.head = head;
		}

		@Override
		public V put(RK key, V value) {
			// TODO Auto-generated method stub
			if (null == head || head.disposed()) {
				head = LinkedMatrix.this.addColumnIfNotExists(column);
				head.viewMap = this;
			}
			return LinkedMatrix.this.setValueInColumn(head, key, value);
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
							private EntryNode current = null;
							private boolean currentRemoved = false;

							private int expectedModCount = LinkedMatrix.this.modCount;

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								checkModCount();
								if (current == null)
									return head != null && head.entry != null;
								else
									return current.lower != null;
							}

							private boolean getNext() {
								checkModCount();
								if (current == null) {
									if (head == null)
										return false;
									return (current = head.entry) != null;
								} else {
									if (current.lower == null)
										return false;
									current = current.lower;
									return true;
								}
							}

							private void checkModCount() {
								if (expectedModCount != LinkedMatrix.this.modCount)
									throw new ConcurrentModificationException();
							}

							@Override
							public Map.Entry<RK, V> next() {
								// TODO Auto-generated method stub
								if (!getNext())
									throw new NoSuchElementException();
								currentRemoved = false;
								return current.columnMapEntry();
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								checkModCount();
								if (currentRemoved || current == null)
									throw new IllegalStateException();
								EntryNode up = current.upper;
								LinkedMatrix.this.removeNode(current);
								if (head.disposed()) {
									current = null;
									head = null;
								} else {
									current = up;
								}
								currentRemoved = true;
								expectedModCount = LinkedMatrix.this.modCount;
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
	protected int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		HeadNode<RK> head = rowHead(row);
		return head == null ? 0 : head.size;
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		HeadNode<CK> head = columnHead(column);
		return head == null ? 0 : head.size;
	}
	
	@Override
	void clearViews(){
		super.clearViews();
		rowKeySet = null;
		columnKeySet = null;
		entrySet = null;
	}

	// View
	protected transient volatile Set<RK> rowKeySet = null;
	protected transient volatile Set<CK> columnKeySet = null;
	protected transient volatile Set<Entry<RK, CK, V>> entrySet = null;

	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		if (rowKeySet == null) {
			rowKeySet = new AbstractSet<RK>() {

				@Override
				public Iterator<RK> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<RK>() {
						private HeadNode<RK> curHead = null;
						private boolean currentRemoved = false;

						private int expectedModCount = LinkedMatrix.this.modCount;

						private void checkModCount() {
							if (expectedModCount != LinkedMatrix.this.modCount)
								throw new ConcurrentModificationException();
						}

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							checkModCount();
							if (curHead == null)
								return LinkedMatrix.this.rowHeadsEntry != null;
							else
								return curHead.next != null;
						}

						private boolean getNext() {
							checkModCount();
							if (curHead == null) {
								return (curHead = LinkedMatrix.this.rowHeadsEntry) != null;
							} else {
								if (curHead.next == null)
									return false;
								curHead = curHead.next;
								return true;
							}
						}

						@Override
						public RK next() {
							// TODO Auto-generated method stub
							if (!getNext())
								throw new NoSuchElementException();
							currentRemoved = false;
							return curHead.getKey();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							checkModCount();
							if (currentRemoved || curHead == null)
								throw new IllegalStateException();

							HeadNode<RK> prev = curHead.prev;
							LinkedMatrix.this.removeRow(curHead);
							curHead = prev;
							currentRemoved = true;
							expectedModCount = LinkedMatrix.this.modCount;
						}

					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return LinkedMatrix.this.rows;
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
						private HeadNode<CK> curHead = null;
						private boolean currentRemoved = false;

						private int expectedModCount = LinkedMatrix.this.modCount;

						private void checkModCount() {
							if (expectedModCount != LinkedMatrix.this.modCount)
								throw new ConcurrentModificationException();
						}

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							checkModCount();
							if (curHead == null)
								return LinkedMatrix.this.columnHeadsEntry != null;
							else
								return curHead.next != null;
						}

						private boolean getNext() {
							checkModCount();
							if (curHead == null) {
								return (curHead = LinkedMatrix.this.columnHeadsEntry) != null;
							} else {
								if (curHead.next == null)
									return false;
								curHead = curHead.next;
								return true;
							}
						}

						@Override
						public CK next() {
							// TODO Auto-generated method stub
							if (!getNext())
								throw new NoSuchElementException();
							currentRemoved = false;
							return curHead.getKey();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							checkModCount();
							if (currentRemoved || curHead == null)
								throw new IllegalStateException();

							HeadNode<CK> prev = curHead.prev;
							LinkedMatrix.this.removeColumn(curHead);
							curHead = prev;
							currentRemoved = true;
							expectedModCount = LinkedMatrix.this.modCount;
						}

					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return LinkedMatrix.this.columns;
				}

			};
		}
		return columnKeySet;
	}

	@Override
	public Set<Entry<RK, CK, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {
			entrySet = new AbstractSet<Entry<RK, CK, V>>() {

				@Override
				public Iterator<Entry<RK, CK, V>> iterator() {
					// TODO Auto-generated method stub
					return LinkedMatrix.this.nodeIterator(true);
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return LinkedMatrix.this.size;
				}

			};
		}
		return entrySet;
	}

	Iterator<Entry<RK, CK, V>> nodeIterator(final boolean rowOrder) {
		return new Iterator<Entry<RK, CK, V>>() {
			@SuppressWarnings("unchecked")
			private HeadNode<?> nextHead = (HeadNode<?>) (rowOrder ? LinkedMatrix.this.rowHeadsEntry
					: LinkedMatrix.this.columnHeadsEntry);
			private EntryNode currentNode = null;
			private EntryNode nextNode = null;
			private int expectedModCount = LinkedMatrix.this.modCount;

			private boolean getNextNode() {
				checkModCount();
				if (currentNode == null
						|| (rowOrder && (nextNode = currentNode.right) == null)
						|| (!rowOrder && (nextNode = currentNode.lower) == null)) {
					while (nextHead != null && nextHead.entry() == null) {
						nextHead = nextHead.next;
					}
					if (nextHead == null)
						return false;
					nextNode = nextHead.entry();
					nextHead = nextHead.next;
				}
				return nextNode != null;
			}

			private void checkModCount() {
				if (this.expectedModCount != LinkedMatrix.this.modCount)
					throw new ConcurrentModificationException();
			}

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return nextNode == null ? this.getNextNode() : true;
			}

			@Override
			public Entry<RK, CK, V> next() {
				// TODO Auto-generated method stub
				if (null == nextNode && !this.getNextNode())
					throw new NoSuchElementException();
				currentNode = nextNode;
				nextNode = null;
				return currentNode;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				checkModCount();
				if (currentNode == null)
					throw new IllegalStateException();
				if (nextNode == null)
					this.getNextNode();
				LinkedMatrix.this.removeNode(currentNode);
				currentNode = null;
				this.expectedModCount = LinkedMatrix.this.modCount;
			}

		};
	}

	private class EntryNode extends AbstractEntry<RK, CK, V> implements
			Entry<RK, CK, V> {
		private V value;
		private EntryNode right, left, upper, lower;
		private HeadNode<RK> rowHead;
		private HeadNode<CK> columnHead;

		private EntryNode(V value, HeadNode<RK> rowHead, HeadNode<CK> columnHead) {
			this.value = value;
			this.rowHead = rowHead;
			this.columnHead = columnHead;
			this.left = this.right = null;
			this.upper = this.lower = null;
		}

		@Override
		protected void dispose() {
			super.dispose();
			this.value = null;
			this.rowHead = null;
			this.columnHead = null;
			this.left = this.right = null;
			this.upper = this.lower = null;
		}

		private HeadNode<RK> rowHead() {
			return rowHead;
		}

		private HeadNode<CK> columnHead() {
			return columnHead;
		}

		@Override
		public RK getRowKey() {
			// TODO Auto-generated method stub
			return rowHead().getKey();
		}

		@Override
		public CK getColumnKey() {
			// TODO Auto-generated method stub
			return columnHead().getKey();
		}

		@Override
		public V getValue() {
			// TODO Auto-generated method stub
			return value;
		}

		@Override
		public V setValue(V value) {
			// TODO Auto-generated method stub
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}
	}

	private class HeadNode<K> {
		private K key;
		private int index;
		private int size;
		private EntryNode entry;
		private HeadNode<K> prev, next;

		// View
		protected transient volatile Map<?, V> viewMap = null;

		private HeadNode(K key, int index) {
			this.key = key;
			this.index = index;
			this.size = 0;
			this.entry = null;
			this.prev = null;
			this.next = null;
		}

		private K getKey() {
			return key;
		}

		private int addSize(int incr) {
			size = Math.max(0, size + incr);
			return size;
		}

		private EntryNode entry() {
			return entry;
		}

		private void dispose() {
			key = null;
			index = -1;
			size = 0;
			entry = null;
			prev = next = null;

			viewMap = null;
		}

		private boolean disposed() {
			return index < 0;
		}
	}
	
	/**
	 * Save the state of the <tt>LinkedMatrix</tt> instance to a stream (that is,
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

		s.writeInt(this.size());
		Iterator<Matrix.Entry<RK, CK, V>> i = isEmpty() ? null : entrySet().iterator();
		if(i != null){
			while(i.hasNext()){
				Matrix.Entry<RK, CK, V> e = i.next();
				s.writeObject(e.getRowKey());
				s.writeObject(e.getColumnKey());
				s.writeObject(e.getValue());
			}
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}

	}

	/**
	 * Reconstitute the <tt>LinkedMatrix</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();

		// Read in array length and allocate array
		int size = s.readInt();

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++) {
			RK row = (RK) s.readObject();
			CK column = (CK) s.readObject();
			V value = (V) s.readObject();
			put(row, column, value);
		}
	}

}
