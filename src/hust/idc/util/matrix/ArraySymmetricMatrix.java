package hust.idc.util.matrix;

import hust.idc.util.pair.UnorderedPair;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ArraySymmetricMatrix<K, V> extends AbstractSymmetricMatrix<K, V>
		implements SymmetricMatrix<K, V>, Matrix<K, K, V> {

	private ArrayList<Head> heads;
	private ArraySymmetricMatrix<K, V>.Entry[] entrys;
	private int size;
	private int demensionCapacity;

	private volatile int modCount = 0;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30.
	 */
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	public ArraySymmetricMatrix() {
		this(10);
	}

	@SuppressWarnings("unchecked")
	public ArraySymmetricMatrix(int initialDemension) {
		super();
		if (initialDemension < 0)
			throw new IllegalArgumentException("Illegal Demension: "
					+ initialDemension);

		demensionCapacity = Math.min(MAXIMUM_CAPACITY, initialDemension);
		heads = new ArrayList<Head>(demensionCapacity);
		entrys = new ArraySymmetricMatrix.Entry[arraySize(demensionCapacity)];
	}

	@SuppressWarnings("unchecked")
	public ArraySymmetricMatrix(
			SymmetricMatrix<? extends K, ? extends V> otherMatrix) {
		super();
		int demension = otherMatrix == null ? 0 : otherMatrix.demension();
		demensionCapacity = Math.max(10, demension + (demension << 1));
		heads = new ArrayList<Head>(demensionCapacity);
		entrys = new ArraySymmetricMatrix.Entry[arraySize(demensionCapacity)];

		this.putAll(otherMatrix);
		// modCount = 0;
	}

	public ArraySymmetricMatrix(
			Map<? extends UnorderedPair<? extends K>, ? extends V> otherMatrix) {
		this();
		this.putAll(otherMatrix);
		// modCount = 0;
	}

	private static int arraySize(int demension) {
		return demension == 0 ? 0 : (demension * (demension + 1)) >> 1;
	}

	private int demensionCapacity() {
		return demensionCapacity;
	}

	public void trimToSize() {
		this.heads.trimToSize();

		if (this.demension() < this.demensionCapacity()) {
			this.entrys = Arrays.copyOf(this.entrys,
					arraySize(this.demension()));
		}
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public int demension() {
		// TODO Auto-generated method stub
		return heads.size();
	}

	private Head getHead(Object key) {
		if (key == null) {
			for (int i = 0; i < heads.size(); ++i) {
				Head head = heads.get(i);
				if (head.getKey() == null)
					return head;
			}
		} else {
			for (int i = 0; i < heads.size(); ++i) {
				Head head = heads.get(i);
				if (key.equals(head.getKey()))
					return head;
			}
		}
		return null;
	}

	private static int indexFor(int row, int column) {
		if (row < column)
			return indexFor(column, row);
		return arraySize(row) + column;
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		return null != getHead(row);
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		Head rowHead = getHead(row);
		if (null == rowHead)
			return false;
		Head columnHead = getHead(column);
		if (null == columnHead)
			return false;

		int index = indexFor(rowHead.index, columnHead.index);
		if (index < 0 || index > entrys.length)
			return false;
		return null != entrys[index];
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		Head rowHead = getHead(row);
		if (null == rowHead)
			return null;
		Head columnHead = getHead(column);
		if (null == columnHead)
			return null;

		int index = indexFor(rowHead.index, columnHead.index);
		if (index < 0 || index > entrys.length)
			return null;
		return null == entrys[index] ? null : entrys[index].getValue();
	}

	private Head addHeadIfNotExists(K key) {
		if (key == null) {
			for (int i = 0; i < heads.size(); ++i) {
				Head head = heads.get(i);
				if (head.getKey() == null)
					return head;
			}
		} else {
			for (int i = 0; i < heads.size(); ++i) {
				Head head = heads.get(i);
				if (key.equals(head.getKey()))
					return head;
			}
		}
		Head newHead = new Head(key, heads.size());
		heads.add(newHead);
		return newHead;
	}

	@Override
	public V put(K row, K column, V value) {
		// TODO Auto-generated method stub
		return setValue(addHeadIfNotExists(row), addHeadIfNotExists(column),
				value);
	}

	private V setValue(Head rowHead, Head columnHead, V value) {
		if (rowHead.index >= this.demensionCapacity()
				|| columnHead.index >= this.demensionCapacity())
			ensureDemension(Math.max(rowHead.index, columnHead.index));

		int index = indexFor(rowHead.index, columnHead.index);
		++modCount;
		if (entrys[index] == null) {
			if(rowHead.index < columnHead.index)
				entrys[index] = new Entry(value, columnHead, rowHead);
			else
				entrys[index] = new Entry(value, rowHead, columnHead);
			++size;
			rowHead.increaseSize(1);
			if (!entrys[index].isDiagonal())
				columnHead.increaseSize(1);
			return null;
		} else {
			return entrys[index].setValue(value);
		}
	}

	public void ensureDemension(int minDemension) {
		modCount++;
		if (minDemension > this.demensionCapacity()) {
			Entry oldData[] = entrys;
			this.demensionCapacity = (this.demensionCapacity() * 3) / 2 + 1;
			if (this.demensionCapacity < minDemension)
				this.demensionCapacity = minDemension;
			// minCapacity is usually close to size, so this is a win:
			entrys = Arrays.copyOf(oldData, arraySize(this.demensionCapacity));
		}
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		Head rowHead = getHead(row);
		if (null == rowHead)
			return null;
		Head columnHead = getHead(column);
		if (null == columnHead)
			return null;
		return removeElementAt(indexFor(rowHead.index, columnHead.index));
	}
	
	private V removeElementAt(int index){
		if (index < 0 || index > entrys.length || null == entrys[index])
			return null;

		++modCount;
		--size;
		Entry entry = entrys[index];
		entrys[index] = null;
		if (entry.rowHead.increaseSize(-1) == 0)
			this.removeHead(entry.rowHead);
		if (!entry.isDiagonal() && entry.columnHead.increaseSize(-1) == 0)
			this.removeHead(entry.columnHead);

		V oldValue = entry.getValue();
		entry.dispose();
		return oldValue;
	}

	private void removeHead(Head head) {
		this.heads.remove(head.index);
		
		++ modCount;
		for(int i = head.index; i < heads.size(); ++i){
			Head otherHead = heads.get(i);
			otherHead.index = i;

			for(int j = 0; j <= i; ++j){
				// adjust the position of entries in array for each row behind
				int oldIndex = indexFor(i + 1, j);
				if(null == entrys[oldIndex])
					continue;
				
				int newIndex = indexFor(i, j);
				
				entrys[newIndex] = entrys[oldIndex];
				entrys[oldIndex] = null;
			}
			
			// move the diagonal element
			int oldIndex = indexFor(i + 1, i + 1);
			if(null == entrys[oldIndex])
				continue;
			
			int newIndex = indexFor(i, i);
			entrys[newIndex] = entrys[oldIndex];
			entrys[oldIndex] = null;
		}
		head.dispose();
	}

	@Override
	public void removeRow(K row) {
		// TODO Auto-generated method stub
		Head rowHead = getHead(row);
		if(null == rowHead)
			return ;
		for(int i = 0; i < this.demension(); ++i){
			this.removeElementAt(indexFor(rowHead.index, i));
		}
		if(!rowHead.disposed())
			this.removeHead(rowHead);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		int demension = this.demension() - 1;
		int maxIndex = indexFor(demension, demension);
		for (int i = 0; i <= maxIndex; ++i) {
			if (this.entrys[i] != null) {
				this.entrys[i].dispose();
				this.entrys[i] = null;
			}
		}
		for (int i = demension; i >= 0; --i) {
			this.heads.get(i).dispose();
			this.heads.remove(i);
		}
		size = 0;
		++modCount;
	}

	@Override
	public Map<K, V> rowMap(K row) {
		// TODO Auto-generated method stub
		Head head = this.getHead(row);
		if (null == head)
			return new RowMapView(row, head);
		if (null == head.rowMapView) {
			head.rowMapView = new RowMapView(row, head);
		}
		return head.rowMapView;
	}

	private class RowMapView extends AbstractMap<K, V> {
		private Head head;
		private K key;

		private RowMapView(K key, Head head) {
			this.key = key;
			this.head = head;
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			if (null == head)
				return false;
			Head columnHead = ArraySymmetricMatrix.this.getHead(key);
			if (null == columnHead)
				return false;

			int index = indexFor(head.index, columnHead.index);
			if (index < 0 || index > entrys.length)
				return false;
			return null != entrys[index];
		}

		@Override
		public V get(Object key) {
			// TODO Auto-generated method stub
			if (null == head)
				return null;
			Head columnHead = ArraySymmetricMatrix.this.getHead(key);
			if (null == columnHead)
				return null;

			int index = indexFor(head.index, columnHead.index);
			if (index < 0 || index > entrys.length)
				return null;
			return null == entrys[index] ? null : entrys[index].getValue();
		}

		@Override
		public V put(K key, V value) {
			// TODO Auto-generated method stub
			if (null == head || head.disposed()) {
				head = ArraySymmetricMatrix.this.addHeadIfNotExists(this.key);
				head.rowMapView = this;
			}
			return ArraySymmetricMatrix.this.setValue(head,
					addHeadIfNotExists(key), value);
		}

		// View
		protected transient volatile Set<Map.Entry<K, V>> entrySet = null;

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			// TODO Auto-generated method stub
			if (null == entrySet) {
				entrySet = new AbstractSet<Map.Entry<K, V>>() {

					@Override
					public Iterator<Map.Entry<K, V>> iterator() {
						// TODO Auto-generated method stub
						return new Iterator<Map.Entry<K, V>>() {
							private int currentIndex = -1;
							private int nextColumn = -1, nextIndex = -1;
							private boolean nextInRow = false;

							private int expectedModCount = ArraySymmetricMatrix.this.modCount;

							private void checkModCount() {
								if (expectedModCount != ArraySymmetricMatrix.this.modCount)
									throw new ConcurrentModificationException();
							}
							
							private boolean getNext(){
								checkModCount();
								if(null == head)
									return false;
								for(nextColumn = nextColumn + 1; nextColumn < ArraySymmetricMatrix.this.demension(); ++nextColumn){
									nextIndex = indexFor(head.index, nextColumn);
									nextInRow = (head.index >= nextColumn);
									if(null != ArraySymmetricMatrix.this.entrys[nextIndex])
										return true;
								}
								return false;
							}

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								if(nextIndex < 0)
									return getNext();
								return null != ArraySymmetricMatrix.this.entrys[nextIndex];
							}

							@Override
							public Map.Entry<K, V> next() {
								// TODO Auto-generated method stub
								if(nextIndex < 0 && !getNext())
									throw new NoSuchElementException();
								if(null == ArraySymmetricMatrix.this.entrys[nextIndex])
									throw new NoSuchElementException();
								ArraySymmetricMatrix<K, V>.Entry entry = ArraySymmetricMatrix.this.entrys[nextIndex];
								currentIndex = nextIndex;
								nextIndex = -1;
								if(nextInRow)
									return entry.rowMapEntry();
								else
									return entry.columnMapEntry();
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								checkModCount();
								if(currentIndex < 0 || null == ArraySymmetricMatrix.this.entrys[currentIndex])
									throw new IllegalStateException();
								
								ArraySymmetricMatrix.this.removeElementAt(currentIndex);
								if(head.disposed()){
									currentIndex = nextIndex = nextColumn = -1;
									head = null;
								}
								expectedModCount = ArraySymmetricMatrix.this.modCount;
							}

						};
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return null == head ? 0 : head.size;
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
		Head head = getHead(row);
		return head == null ? 0 : head.size;
	}

	// View
	protected transient volatile Set<Matrix.Entry<K, K, V>> entrySet = null;
	protected transient volatile Set<K> keySet = null;

	@Override
	public Set<K> rowKeySet() {
		// TODO Auto-generated method stub
		if (keySet == null) {
			keySet = new AbstractSet<K>(){

				@Override
				public Iterator<K> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<K>(){
						private int currentIndex = -1;
						private Head currentHead = null;
						
						private int expectedModCount = ArraySymmetricMatrix.this.modCount;

						private void checkModCount() {
							if (expectedModCount != ArraySymmetricMatrix.this.modCount)
								throw new ConcurrentModificationException();
						}

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							checkModCount();
							return currentIndex + 1 < ArraySymmetricMatrix.this.demension();
						}

						@Override
						public K next() {
							// TODO Auto-generated method stub
							if(!hasNext())
								throw new NoSuchElementException();
							currentHead = ArraySymmetricMatrix.this.heads.get(++currentIndex);
							return currentHead.getKey();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							checkModCount();
							if(null == currentHead || currentHead.disposed())
								throw new IllegalStateException();
							ArraySymmetricMatrix.this.removeHead(currentHead);
							--currentIndex;
							expectedModCount = ArraySymmetricMatrix.this.modCount;
						}
						
					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return ArraySymmetricMatrix.this.demension();
				}
				
			};
		}
		return keySet;
	}

	@Override
	public Set<Matrix.Entry<K, K, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {
			entrySet = new AbstractSet<Matrix.Entry<K, K, V>>(){

				@Override
				public Iterator<Matrix.Entry<K, K, V>> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<Matrix.Entry<K, K, V>>(){
						private int currentIndex = -1;
						private int nextIndex = -1;
						private final int maxIndex = arraySize(ArraySymmetricMatrix.this.demension()) + 1;
						
						private int expectedModCount = ArraySymmetricMatrix.this.modCount;

						private void checkModCount() {
							if (expectedModCount != ArraySymmetricMatrix.this.modCount)
								throw new ConcurrentModificationException();
						}
						
						private boolean getNext(){
							checkModCount();
							for(nextIndex = currentIndex + 1; nextIndex < maxIndex; ++nextIndex){
								if(null != ArraySymmetricMatrix.this.entrys[nextIndex])
									return true;
							}
							return false;
						}

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							if(nextIndex < 0)
								return getNext();
							return nextIndex < maxIndex && null != ArraySymmetricMatrix.this.entrys[nextIndex];
						}

						@Override
						public Matrix.Entry<K, K, V> next() {
							// TODO Auto-generated method stub
							if(!hasNext())
								throw new NoSuchElementException();
							Entry entry = ArraySymmetricMatrix.this.entrys[nextIndex];
							currentIndex = nextIndex;
							nextIndex = -1;
							return entry;
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							if(currentIndex < 0 || null == ArraySymmetricMatrix.this.entrys[currentIndex])
								throw new IllegalStateException();
							
							Head rowHead = ArraySymmetricMatrix.this.entrys[currentIndex].rowHead;
							Head columnHead = ArraySymmetricMatrix.this.entrys[currentIndex].columnHead;
							int curRow = rowHead.index, curColumn = columnHead.index;
							ArraySymmetricMatrix.this.removeElementAt(currentIndex);
							if(rowHead.disposed()){
								curColumn = 0;
								nextIndex = -1;
							}
							if(columnHead.disposed()){
								nextIndex = -1;
							}
							currentIndex = indexFor(curRow, curColumn) - 1;
							expectedModCount = ArraySymmetricMatrix.this.modCount;
						}
						
					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return ArraySymmetricMatrix.this.size;
				}
				
			};
		}
		return entrySet;
	}

	private class Entry extends AbstractEntry<K, K, V> implements
			Matrix.Entry<K, K, V> {
		private V value;
		private Head rowHead;
		private Head columnHead;

		private Entry(V value, Head rowHead, Head columnHead) {
			this.value = value;
			this.rowHead = rowHead;
			this.columnHead = columnHead;
		}

		@Override
		public K getRowKey() {
			// TODO Auto-generated method stub
			return this.rowHead.getKey();
		}

		@Override
		public K getColumnKey() {
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

		private boolean isDiagonal() {
			return rowHead == columnHead;
		}

	}

	private class Head {
		private K key;
		private int index;
		private int size = 0;

		// View
		protected transient volatile Map<K, V> rowMapView = null;

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
			rowMapView = null;
		}

		private boolean disposed() {
			return index < 0;
		}
	}

}
