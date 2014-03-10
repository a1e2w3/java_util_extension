package hust.idc.util.matrix;

import hust.idc.util.pair.Pair;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class HashMatrix<RK, CK, V> extends AbstractMatrix<RK, CK, V> implements
		Matrix<RK, CK, V> {
	private HashMap<RK, HashMap<CK, V>> map;
	private HashMap<CK, Integer> columnKeys;
	private int size;

	public HashMatrix() {
		super();
		this.map = new HashMap<RK, HashMap<CK, V>>();
		this.columnKeys = new HashMap<CK, Integer>();
		this.size = 0;
	}

	public HashMatrix(int initialCapacity) {
		super();
		this.map = new HashMap<RK, HashMap<CK, V>>(initialCapacity);
		this.columnKeys = new HashMap<CK, Integer>();
		this.size = 0;
	}

	public HashMatrix(int initialCapacity, float loadFactor) {
		super();
		this.map = new HashMap<RK, HashMap<CK, V>>(initialCapacity, loadFactor);
		this.columnKeys = new HashMap<CK, Integer>();
	}

	public HashMatrix(
			Matrix<? extends RK, ? extends CK, ? extends V> otherMatrix) {
		this();
		this.setAll(otherMatrix);
	}

	public HashMatrix(
			Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> otherMatrix) {
		this();
		this.setAll(otherMatrix);
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		return map.containsKey(row);
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		return columnKeys.containsKey(column);
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		HashMap<CK, V> innerMap = map.get(row);
		if (innerMap == null)
			return false;
		return innerMap.containsKey(column);
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		HashMap<CK, V> innerMap = map.get(row);
		return innerMap == null ? null : innerMap.get(column);
	}

	@Override
	public V set(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		if (!map.containsKey(row)) {
			map.put(row, new HashMap<CK, V>());
		}

		if (!this.containsKey(row, column)) {
			this.addColumnElement(column, 1);
		}
		return map.get(row).put(column, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		HashMap<CK, V> innerMap = map.get(row);
		if (innerMap != null) {
			V oldValue = innerMap.remove(column);
			if (innerMap.isEmpty())
				map.remove(row);
			try {
				this.addColumnElement((CK) column, -1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// do nothing
			}
			return oldValue;
		}
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		Iterator<Map.Entry<RK, HashMap<CK, V>>> entryIt = map.entrySet()
				.iterator();
		while (entryIt.hasNext()) {
			Map.Entry<RK, HashMap<CK, V>> entry = entryIt.next();
			entry.getValue().clear();
			entryIt.remove();
		}
		columnKeys.clear();
		this.size = 0;
	}

	private int addColumnElement(CK column, int incr) {
		int oldCount = this.columnValueCount(column);
		int newCount = oldCount + incr;
		if (newCount <= 0) {
			this.columnKeys.remove(column);
			this.size -= oldCount;
		} else {
			this.columnKeys.put(column, newCount);
			this.size += incr;
		}
		return newCount;
	}

	@Override
	public void removeRow(RK row) {
		// TODO Auto-generated method stub
		HashMap<CK, V> innerMap = map.get(row);
		if (innerMap != null) {
			Iterator<CK> it = innerMap.keySet().iterator();
			while (it.hasNext()) {
				CK column = it.next();
				HashMatrix.this.addColumnElement(column, -1);
				it.remove();
			}
		}
		map.remove(row);
	}

	@Override
	protected int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		return map.containsKey(row) ? map.get(row).size() : 0;
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		Integer count = columnKeys.get(column);
		return count == null ? 0 : count.intValue();
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
						private Iterator<Map.Entry<RK, HashMap<CK, V>>> iterator = HashMatrix.this.map
								.entrySet().iterator();
						private Map.Entry<RK, HashMap<CK, V>> current = null;

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return iterator.hasNext();
						}

						@Override
						public RK next() {
							// TODO Auto-generated method stub
							return (current = iterator.next()).getKey();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							iterator.remove();
							if (current.getValue() != null) {
								Iterator<CK> it = current.getValue().keySet()
										.iterator();
								while (it.hasNext()) {
									CK column = it.next();
									HashMatrix.this
											.addColumnElement(column, -1);
									it.remove();
								}
							}
							current = null;
						}

					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return HashMatrix.this.map.size();
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
						Iterator<CK> iterator = columnKeys.keySet().iterator();
						CK current = null;

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return iterator.hasNext();
						}

						@Override
						public CK next() {
							// TODO Auto-generated method stub
							return current = iterator.next();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							iterator.remove();
							HashMatrix.this.removeColumn(current);
							current = null;
						}

					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return HashMatrix.this.columnKeys.size();
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
					return new Iterator<Entry<RK, CK, V>>() {
						Iterator<Map.Entry<RK, HashMap<CK, V>>> iterator = HashMatrix.this.map
								.entrySet().iterator();
						Iterator<Map.Entry<CK, V>> innerIt = null;
						Map.Entry<CK, V> currentInner = null;
						Map.Entry<RK, HashMap<CK, V>> currentEntry = null;

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							if (innerIt != null && innerIt.hasNext()) {
								return true;
							} else {
								while (iterator.hasNext()) {
									currentEntry = iterator.next();
									if (currentEntry.getValue() != null) {
										innerIt = currentEntry.getValue()
												.entrySet().iterator();
										if (innerIt.hasNext()) {
											return true;
										}
									}
								}
							}
							return false;
						}

						private boolean getNext() {
							if (innerIt != null && innerIt.hasNext()) {
								currentInner = innerIt.next();
								return true;
							} else {
								while (iterator.hasNext()) {
									currentEntry = iterator.next();
									if (currentEntry.getValue() != null) {
										innerIt = currentEntry.getValue()
												.entrySet().iterator();
										if (innerIt.hasNext()) {
											currentInner = innerIt.next();
											return true;
										}
									}
								}
							}
							return false;
						}

						@Override
						public Entry<RK, CK, V> next() {
							// TODO Auto-generated method stub
							if (!this.getNext())
								throw new NoSuchElementException();
							return new AbstractEntry<RK, CK, V>() {

								@Override
								public RK getRowKey() {
									// TODO Auto-generated method stub
									return currentEntry.getKey();
								}

								@Override
								public CK getColumnKey() {
									// TODO Auto-generated method stub
									return currentInner.getKey();
								}

								@Override
								public V getValue() {
									// TODO Auto-generated method stub
									return currentInner.getValue();
								}

								@Override
								public V setValue(V value) {
									// TODO Auto-generated method stub
									return currentInner.setValue(value);
								}

							};
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							if (currentInner == null || currentEntry == null)
								throw new IllegalStateException();

							CK column = currentInner.getKey();
							currentInner = null;
							innerIt.remove();
							HashMatrix.this.addColumnElement(column, -1);

							if (currentEntry.getValue().isEmpty()) {
								currentEntry = null;
								iterator.remove();
							}
						}

					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return HashMatrix.this.size;
				}

			};
		}
		return entrySet;
	}

	@Override
	public Map<CK, V> rowMap(final RK row) {
		// TODO Auto-generated method stub
		return new AbstractMap<CK, V>() {
			private HashMap<CK, V> innerMap = HashMatrix.this.map.get(row);

			@Override
			public V put(CK key, V value) {
				// TODO Auto-generated method stub
				if(innerMap == null){
					innerMap = new HashMap<CK, V>();
					HashMatrix.this.map.put(row, innerMap);
				}
				return innerMap.put(key, value);
			}


			@Override
			public Set<Map.Entry<CK, V>> entrySet() {
				// TODO Auto-generated method stub
				return new AbstractSet<Map.Entry<CK, V>>() {

					@Override
					public Iterator<Map.Entry<CK, V>> iterator() {
						// TODO Auto-generated method stub
						return new Iterator<Map.Entry<CK, V>>() {
							Iterator<Map.Entry<CK, V>> iterator = (innerMap == null ? null
									: innerMap.entrySet().iterator());
							Map.Entry<CK, V> current = null;

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								return iterator != null && iterator.hasNext();
							}

							@Override
							public Map.Entry<CK, V> next() {
								// TODO Auto-generated method stub
								if (iterator == null)
									throw new NoSuchElementException();
								return current = iterator.next();
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								if (iterator == null)
									throw new IllegalStateException();
								iterator.remove();
								if (innerMap.isEmpty()) {
									HashMatrix.this.map.remove(row);
									innerMap = null;
									iterator = null;
								}
								HashMatrix.this.addColumnElement(
										current.getKey(), -1);
								current = null;
							}

						};
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return innerMap == null ? 0 : innerMap.size();
					}

				};
			}

		};
	}

}
