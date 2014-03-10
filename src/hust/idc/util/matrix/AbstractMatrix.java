package hust.idc.util.matrix;

import hust.idc.util.AbstractImmutableMapEntry;
import hust.idc.util.AbstractMapEntry;
import hust.idc.util.pair.AbstractImmutablePair;
import hust.idc.util.pair.Pair;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class AbstractMatrix<RK, CK, V> implements Matrix<RK, CK, V> {
	protected AbstractMatrix() {
	}

	@Override
	public int size() {
		return entrySet().size();
	}

	@Override
	public int rows() {
		// TODO Auto-generated method stub
		return rowKeySet().size();
	}

	@Override
	public int columns() {
		// TODO Auto-generated method stub
		return columnKeySet().size();
	}

	@Override
	public boolean isEmpty() {
		return 0 == size();
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		Iterator<Entry<RK, CK, V>> entryIt = entrySet().iterator();
		if (row == null) {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (null == entry.getRowKey())
					return true;
			}
		} else {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (row.equals(entry.getRowKey()))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		Iterator<Entry<RK, CK, V>> entryIt = entrySet().iterator();
		if (column == null) {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (null == entry.getColumnKey())
					return true;
			}
		} else {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (column.equals(entry.getColumnKey()))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		Iterator<Entry<RK, CK, V>> entryIt = entrySet().iterator();
		while (entryIt.hasNext()) {
			Entry<RK, CK, V> entry = entryIt.next();
			if (entry.match(row, column))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsKey(Pair<?, ?> keyPair) {
		// TODO Auto-generated method stub
		return containsKey(keyPair.getFirst(), keyPair.getSecond());
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		Iterator<Entry<RK, CK, V>> entryIt = entrySet().iterator();
		if (value == null) {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (null == entry.getValue())
					return true;
			}
		} else {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (value.equals(entry.getValue()))
					return true;
			}
		}
		return false;
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		Iterator<Entry<RK, CK, V>> entryIt = entrySet().iterator();
		while (entryIt.hasNext()) {
			Entry<RK, CK, V> entry = entryIt.next();
			if (entry.match(row, column))
				return entry.getValue();
		}
		return null;
	}

	@Override
	public V get(Pair<?, ?> keyPair) {
		// TODO Auto-generated method stub
		return this.get(keyPair.getFirst(), keyPair.getSecond());
	}

	@Override
	public V set(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public V set(Pair<? extends RK, ? extends CK> keyPair, V value) {
		// TODO Auto-generated method stub
		return set(keyPair.getFirst(), keyPair.getSecond(), value);
	}

	@Override
	public void setAll(Matrix<? extends RK, ? extends CK, ? extends V> matrix) {
		// TODO Auto-generated method stub
		Iterator<? extends Entry<? extends RK, ? extends CK, ? extends V>> entryIt = matrix
				.entrySet().iterator();
		while (entryIt.hasNext()) {
			Entry<? extends RK, ? extends CK, ? extends V> entry = entryIt
					.next();
			set(entry.getRowKey(), entry.getColumnKey(), entry.getValue());
		}
	}

	@Override
	public void setAll(
			Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> map) {
		// TODO Auto-generated method stub
		Iterator<? extends Map.Entry<? extends Pair<? extends RK, ? extends CK>, ? extends V>> entryIt = map
				.entrySet().iterator();
		while (entryIt.hasNext()) {
			Map.Entry<? extends Pair<? extends RK, ? extends CK>, ? extends V> entry = entryIt
					.next();
			set(entry.getKey().getFirst(), entry.getKey().getSecond(),
					entry.getValue());
		}
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		Iterator<Entry<RK, CK, V>> entryIt = entrySet().iterator();
		while (entryIt.hasNext()) {
			Entry<RK, CK, V> entry = entryIt.next();
			if (entry.match(row, column)) {
				V oldValue = entry.getValue();
				entryIt.remove();
				return oldValue;
			}
		}
		return null;
	}

	@Override
	public V remove(Pair<?, ?> keyPair) {
		// TODO Auto-generated method stub
		return this.remove(keyPair.getFirst(), keyPair.getSecond());
	}

	@Override
	public void removeRow(RK row) {
		// TODO Auto-generated method stub
		rowMap(row).clear();
	}

	@Override
	public void removeColumn(CK column) {
		// TODO Auto-generated method stub
		columnMap(column).clear();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		entrySet().clear();
	}

	// Views

	/**
	 * Each of these fields are initialized to contain an instance of the
	 * appropriate view the first time this view is requested. The views are
	 * stateless, so there's no reason to create more than one of each.
	 */
	protected transient volatile Collection<V> values = null;
	protected transient volatile Set<Pair<RK, CK>> keyPairSet = null;

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		if (values == null) {
			values = new AbstractCollection<V>() {

				@Override
				public Iterator<V> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<V>() {
						Iterator<Entry<RK, CK, V>> it = AbstractMatrix.this
								.entrySet().iterator();

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return it.hasNext();
						}

						@Override
						public V next() {
							// TODO Auto-generated method stub
							return it.next().getValue();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							it.remove();
						}

					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return AbstractMatrix.this.entrySet().size();
				}

				@Override
				public boolean contains(Object o) {
					// TODO Auto-generated method stub
					return AbstractMatrix.this.containsColumn(o);
				}

			};
		}
		return values;
	}

	@Override
	public Set<Pair<RK, CK>> keyPairSet() {
		// TODO Auto-generated method stub
		if (keyPairSet == null) {
			keyPairSet = new AbstractSet<Pair<RK, CK>>() {

				@Override
				public Iterator<Pair<RK, CK>> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<Pair<RK, CK>>() {
						Iterator<Entry<RK, CK, V>> it = AbstractMatrix.this
								.entrySet().iterator();

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							return it.hasNext();
						}

						@Override
						public Pair<RK, CK> next() {
							// TODO Auto-generated method stub
							return it.next().getKeyPair();
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							it.remove();
						}

					};
				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return AbstractMatrix.this.size();
				}

			};
		}
		return keyPairSet;
	}

	@Override
	public abstract Set<Entry<RK, CK, V>> entrySet();

	@Override
	public Map<CK, V> rowMap(final RK row) {
		// TODO Auto-generated method stub
		return new AbstractMap<CK, V>() {

			@Override
			public V put(CK key, V value) {
				// TODO Auto-generated method stub
				return AbstractMatrix.this.set(row, key, value);
			}

			@Override
			public Set<Map.Entry<CK, V>> entrySet() {
				// TODO Auto-generated method stub
				return new AbstractSet<Map.Entry<CK, V>>() {

					@Override
					public Iterator<Map.Entry<CK, V>> iterator() {
						// TODO Auto-generated method stub
						return new Iterator<Map.Entry<CK, V>>() {
							Iterator<Matrix.Entry<RK, CK, V>> iterator = AbstractMatrix.this
									.entrySet().iterator();
							Matrix.Entry<RK, CK, V> next = null;

							private void getNext() {
								next = null;
								if (null == row) {
									while (iterator.hasNext()) {
										Matrix.Entry<RK, CK, V> entry = iterator
												.next();
										if (null == entry.getRowKey()) {
											next = entry;
											break;
										}
									}
								} else {
									while (iterator.hasNext()) {
										Matrix.Entry<RK, CK, V> entry = iterator
												.next();
										if (row.equals(entry.getRowKey())) {
											next = entry;
											break;
										}
									}
								}
							}

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								if (next == null && iterator.hasNext())
									this.getNext();
								return next != null;
							}

							@Override
							public java.util.Map.Entry<CK, V> next() {
								// TODO Auto-generated method stub
								if (next == null && iterator.hasNext())
									this.getNext();
								if (next == null)
									throw new NoSuchElementException();
								Map.Entry<CK, V> entry = next.rowMapEntry();
								next = null;
								return entry;
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								iterator.remove();
							}

						};
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return AbstractMatrix.this.rowValueCount(row);
					}

				};
			}

		};
	}

	@Override
	public Map<RK, V> columnMap(final CK column) {
		// TODO Auto-generated method stub
		return new AbstractMap<RK, V>() {

			@Override
			public V put(RK key, V value) {
				// TODO Auto-generated method stub
				return AbstractMatrix.this.set(key, column, value);
			}

			@Override
			public Set<Map.Entry<RK, V>> entrySet() {
				// TODO Auto-generated method stub
				return new AbstractSet<Map.Entry<RK, V>>() {

					@Override
					public Iterator<Map.Entry<RK, V>> iterator() {
						// TODO Auto-generated method stub
						return new Iterator<Map.Entry<RK, V>>() {
							Iterator<Matrix.Entry<RK, CK, V>> iterator = AbstractMatrix.this
									.entrySet().iterator();
							Matrix.Entry<RK, CK, V> next = null;

							private void getNext() {
								next = null;
								if (null == column) {
									while (iterator.hasNext()) {
										Matrix.Entry<RK, CK, V> entry = iterator
												.next();
										if (null == entry.getColumnKey()) {
											next = entry;
											break;
										}
									}
								} else {
									while (iterator.hasNext()) {
										Matrix.Entry<RK, CK, V> entry = iterator
												.next();
										if (column.equals(entry.getColumnKey())) {
											next = entry;
											break;
										}
									}
								}
							}

							@Override
							public boolean hasNext() {
								// TODO Auto-generated method stub
								if (next == null && iterator.hasNext())
									this.getNext();
								return next != null;
							}

							@Override
							public Map.Entry<RK, V> next() {
								// TODO Auto-generated method stub
								if (next == null && iterator.hasNext())
									this.getNext();
								if (next == null)
									throw new NoSuchElementException();
								Map.Entry<RK, V> entry = next.columnMapEntry();
								next = null;
								return entry;
							}

							@Override
							public void remove() {
								// TODO Auto-generated method stub
								iterator.remove();
							}

						};
					}

					@Override
					public int size() {
						// TODO Auto-generated method stub
						return AbstractMatrix.this.columnValueCount(column);
					}

				};
			}

		};
	}

	protected int rowValueCount(final Object row) {
		int count = 0;
		Iterator<Entry<RK, CK, V>> entryIt = entrySet().iterator();
		if (null == row) {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (null == entry.getRowKey()) {
					++count;
				}
			}
		} else {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (row.equals(entry.getRowKey())) {
					++count;
				}
			}
		}
		return count;
	}

	protected int columnValueCount(final Object column) {
		int count = 0;
		Iterator<Entry<RK, CK, V>> entryIt = entrySet().iterator();
		if (null == column) {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (null == entry.getColumnKey()) {
					++count;
				}
			}
		} else {
			while (entryIt.hasNext()) {
				Entry<RK, CK, V> entry = entryIt.next();
				if (column.equals(entry.getColumnKey())) {
					++count;
				}
			}
		}
		return count;
	}

	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<CK> columnKeySet() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
	 * Utility method for AbstractEntry. Test for equality, checking for nulls.
	 */
	protected static boolean eq(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int h = 0;
		Iterator<Entry<RK, CK, V>> i = entrySet().iterator();
		while (i.hasNext())
			h += i.next().hashCode();
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Matrix))
			return false;

		@SuppressWarnings("unchecked")
		Matrix<RK, CK, V> m = (Matrix<RK, CK, V>) obj;
		if (m.size() != size())
			return false;

		try {
			Iterator<Entry<RK, CK, V>> i = entrySet().iterator();
			while (i.hasNext()) {
				Entry<RK, CK, V> e = i.next();
				RK row = e.getRowKey();
				CK column = e.getColumnKey();
				V value = e.getValue();
				if (value == null) {
					if (!(m.get(row, column) == null && m.containsKey(row,
							column)))
						return false;
				} else {
					if (!value.equals(m.get(row, column)))
						return false;
				}
			}
		} catch (ClassCastException unused) {
			return false;
		} catch (NullPointerException unused) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		Iterator<Entry<RK, CK, V>> i = entrySet().iterator();
		if (!i.hasNext())
			return "{}";

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Entry<RK, CK, V> e = i.next();
			RK row = e.getRowKey();
			CK column = e.getColumnKey();
			V value = e.getValue();
			sb.append("[");
			sb.append(row == this ? "(this Matrix)" : row);
			sb.append(", ");
			sb.append(column == this ? "(this Matrix)" : column);
			sb.append("]=");
			sb.append(value == this ? "(this Matrix)" : value);
			if (!i.hasNext())
				return sb.append('}').toString();
			sb.append(", ");
		}
	}

	public static abstract class AbstractEntry<RK, CK, V> implements
			Entry<RK, CK, V> {
		protected AbstractEntry() {
		}

		@Override
		public abstract RK getRowKey();

		@Override
		public abstract CK getColumnKey();

		// View
		protected transient volatile Pair<RK, CK> keyPair = null;

		@Override
		public Pair<RK, CK> getKeyPair() {
			if (keyPair == null) {
				keyPair = new AbstractImmutablePair<RK, CK>() {

					@Override
					public RK getFirst() {
						// TODO Auto-generated method stub
						return AbstractEntry.this.getRowKey();
					}

					@Override
					public CK getSecond() {
						// TODO Auto-generated method stub
						return AbstractEntry.this.getColumnKey();
					}

				};
			}
			return keyPair;
		}

		@Override
		public abstract V getValue();

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}

		// View
		protected transient volatile Map.Entry<CK, V> rowMapEntry = null;
		protected transient volatile Map.Entry<RK, V> columnMapEntry = null;

		@Override
		public Map.Entry<CK, V> rowMapEntry() {
			// TODO Auto-generated method stub
			if (rowMapEntry == null) {
				rowMapEntry = new AbstractMapEntry<CK, V>() {

					@Override
					public CK getKey() {
						// TODO Auto-generated method stub
						return AbstractEntry.this.getColumnKey();
					}

					@Override
					public V getValue() {
						// TODO Auto-generated method stub
						return AbstractEntry.this.getValue();
					}

					@Override
					public V setValue(V value) {
						// TODO Auto-generated method stub
						return AbstractEntry.this.setValue(value);
					}
				};
			}
			return rowMapEntry;
		}

		@Override
		public Map.Entry<RK, V> columnMapEntry() {
			// TODO Auto-generated method stub
			if (columnMapEntry == null) {
				columnMapEntry = new AbstractMapEntry<RK, V>() {

					@Override
					public RK getKey() {
						// TODO Auto-generated method stub
						return AbstractEntry.this.getRowKey();
					}

					@Override
					public V getValue() {
						// TODO Auto-generated method stub
						return AbstractEntry.this.getValue();
					}

					@Override
					public V setValue(V value) {
						// TODO Auto-generated method stub
						return AbstractEntry.this.setValue(value);
					}
				};
			}
			return columnMapEntry;
		}

		@Override
		public boolean match(Object row, Object column) {
			// TODO Auto-generated method stub
			return eq(row, this.getRowKey()) && eq(column, this.getColumnKey());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result += ((getRowKey() == null) ? 0 : getRowKey().hashCode());
			result += ((getColumnKey() == null) ? 0 : getColumnKey().hashCode());
			result += ((getValue() == null) ? 0 : getValue().hashCode());
			return result * prime;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Matrix.Entry))
				return false;
			Entry<?, ?, ?> other = (Entry<?, ?, ?>) obj;
			return other.match(getRowKey(), getColumnKey())
					&& eq(this.getValue(), other.getValue());
		}

		@Override
		public String toString() {
			return getKeyPair() + "=" + getValue();
		}
	}

	public static abstract class AbstractImmutableEntry<RK, CK, V> extends
			AbstractEntry<RK, CK, V> implements Entry<RK, CK, V> {
		protected AbstractImmutableEntry() {
			super();
		}

		@Override
		public abstract RK getRowKey();

		@Override
		public abstract CK getColumnKey();

		@Override
		public abstract V getValue();

		@Override
		public final V setValue(V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map.Entry<CK, V> rowMapEntry() {
			// TODO Auto-generated method stub
			if (rowMapEntry == null) {
				rowMapEntry = new AbstractImmutableMapEntry<CK, V>() {

					@Override
					public CK getKey() {
						// TODO Auto-generated method stub
						return AbstractImmutableEntry.this.getColumnKey();
					}

					@Override
					public V getValue() {
						// TODO Auto-generated method stub
						return AbstractImmutableEntry.this.getValue();
					}
				};
			}
			return rowMapEntry;
		}

		@Override
		public Map.Entry<RK, V> columnMapEntry() {
			// TODO Auto-generated method stub
			if (columnMapEntry == null) {
				columnMapEntry = new AbstractImmutableMapEntry<RK, V>() {

					@Override
					public RK getKey() {
						// TODO Auto-generated method stub
						return AbstractImmutableEntry.this.getRowKey();
					}

					@Override
					public V getValue() {
						// TODO Auto-generated method stub
						return AbstractImmutableEntry.this.getValue();
					}
				};
			}
			return columnMapEntry;
		}
	}

	public static class SimpleEntry<RK, CK, V> extends AbstractEntry<RK, CK, V>
			implements Entry<RK, CK, V>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7760938353404805442L;
		private final RK row;
		private final CK column;
		private V value;

		public SimpleEntry(RK row, CK column, V value) {
			super();
			this.row = row;
			this.column = column;
			this.value = value;
		}

		public SimpleEntry(Entry<RK, CK, V> entry) {
			this(entry.getRowKey(), entry.getColumnKey(), entry.getValue());
		}

		@Override
		public RK getRowKey() {
			// TODO Auto-generated method stub
			return row;
		}

		@Override
		public CK getColumnKey() {
			// TODO Auto-generated method stub
			return column;
		}

		@Override
		public V getValue() {
			// TODO Auto-generated method stub
			return this.value;
		}

		@Override
		public V setValue(V value) {
			// TODO Auto-generated method stub
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

	}

	public static class SimpleImmutableEntry<RK, CK, V> extends
			AbstractImmutableEntry<RK, CK, V> implements Entry<RK, CK, V>,
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2995108056898314381L;
		private final RK row;
		private final CK column;
		private final V value;

		public SimpleImmutableEntry(RK row, CK column, V value) {
			super();
			this.row = row;
			this.column = column;
			this.value = value;
		}

		public SimpleImmutableEntry(Entry<RK, CK, V> entry) {
			this(entry.getRowKey(), entry.getColumnKey(), entry.getValue());
		}

		@Override
		public RK getRowKey() {
			// TODO Auto-generated method stub
			return row;
		}

		@Override
		public CK getColumnKey() {
			// TODO Auto-generated method stub
			return column;
		}

		@Override
		public V getValue() {
			// TODO Auto-generated method stub
			return this.value;
		}
	}

}
