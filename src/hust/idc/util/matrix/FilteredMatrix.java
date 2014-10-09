package hust.idc.util.matrix;

import hust.idc.util.Filter;
import hust.idc.util.FilteredMap;
import hust.idc.util.FilteredSet;
import hust.idc.util.pair.Pair;
import hust.idc.util.pair.Pairs;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FilteredMatrix<RK, CK, V> extends AbstractMatrix<RK, CK, V>
		implements Matrix<RK, CK, V> {
	final Matrix<RK, CK, V> matrix;
	final Filter<RK> rowFilter;
	final Filter<CK> columnFilter;
	final Filter<Pair<? super RK, ? super CK>> elementFilter;

	final Filter<Matrix.Entry<RK, CK, V>> entryFilter;

	public FilteredMatrix(Matrix<RK, CK, V> matrix,
			Filter<Pair<? super RK, ? super CK>> elementFilter) {
		this(matrix, null, null, elementFilter);
	}

	public FilteredMatrix(Matrix<RK, CK, V> matrix, Filter<RK> rowFilter,
			Filter<CK> columnFilter,
			Filter<Pair<? super RK, ? super CK>> elementFilter) {
		super();
		this.matrix = Objects.requireNonNull(matrix);
		this.rowFilter = rowFilter;
		this.columnFilter = columnFilter;
		this.elementFilter = elementFilter;

		this.entryFilter = new Filter<Matrix.Entry<RK, CK, V>>() {

			@Override
			public boolean accept(Matrix.Entry<RK, CK, V> key) {
				// TODO Auto-generated method stub
				return FilteredMatrix.this.accept(key);
			}
		};
	}

	boolean hasFilter() {
		return rowFilter != null || columnFilter != null
				|| elementFilter != null;
	}

	@SuppressWarnings("unchecked")
	boolean acceptRow(final Object row) {
		try {
			return rowFilter == null ? true : rowFilter.accept((RK) row);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	boolean acceptColumn(final Object column) {
		try {
			return columnFilter == null ? true : columnFilter
					.accept((CK) column);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	@SuppressWarnings({ "unchecked" })
	boolean accept(final Object row, final Object column) {
		try {
			return this.acceptRow(row)
					&& this.acceptColumn(column)
					&& (elementFilter == null ? true : elementFilter
							.accept(Pairs.<RK, CK>makePair((RK) row,
									(CK) column)));
		} catch (Throwable e) {
			return false;
		}
	}

	boolean accept(Entry<RK, CK, V> entry) {
		return this.acceptRow(entry.getRowKey())
				&& this.acceptColumn(entry.getColumnKey())
				&& (elementFilter == null ? true : elementFilter.accept(entry
						.getKeyPair()));
	}

	@Override
	public int rows() {
		// TODO Auto-generated method stub
		return rowFilter == null ? this.matrix.rows() : this.rowKeySet().size();
	}

	@Override
	public int columns() {
		// TODO Auto-generated method stub
		return columnFilter == null ? this.matrix.columns() : this.columnKeySet().size();
	}

	@Override
	public boolean containsRow(Object row) {
		// TODO Auto-generated method stub
		return this.acceptRow(row) && this.matrix.containsRow(row);
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		return this.acceptColumn(column) && this.matrix.containsColumn(column);
	}

	@Override
	public boolean containsKey(Object row, Object column) {
		// TODO Auto-generated method stub
		return this.accept(row, column) && this.matrix.containsKey(row, column);
	}

	@Override
	public V get(Object row, Object column) {
		// TODO Auto-generated method stub
		return this.accept(row, column) ? this.matrix.get(row, column) : null;
	}

	@Override
	public V put(RK row, CK column, V value) {
		// TODO Auto-generated method stub
		return this.accept(row, column) ? this.matrix.put(row, column, value)
				: null;
	}

	@Override
	public V remove(Object row, Object column) {
		// TODO Auto-generated method stub
		return this.accept(row, column) ? this.matrix.remove(row, column)
				: null;
	}

	@Override
	public void removeRow(RK row) {
		// TODO Auto-generated method stub
		if (this.acceptRow(row)) {
			super.removeRow(row);
		}
	}

	@Override
	public void removeColumn(CK column) {
		// TODO Auto-generated method stub
		if (this.acceptColumn(column)) {
			super.removeColumn(column);
		}
	}

	@Override
	void clearViews() {
		// TODO Auto-generated method stub
		super.clearViews();
		this.rowKeySet = null;
		this.columnKeySet = null;
		this.entrySet = null;
	}

	@Override
	public Map<CK, V> rowMap(final RK row) {
		// TODO Auto-generated method stub
		return new FilteredMap<CK, V>(matrix.rowMap(row), new Filter<CK>() {

			@Override
			public boolean accept(CK column) {
				// TODO Auto-generated method stub
				return FilteredMatrix.this.accept(row, column);
			}

		});
	}

	@Override
	public Map<RK, V> columnMap(final CK column) {
		// TODO Auto-generated method stub
		return new FilteredMap<RK, V>(matrix.columnMap(column),
				new Filter<RK>() {

					@Override
					public boolean accept(RK row) {
						// TODO Auto-generated method stub
						return FilteredMatrix.this.accept(row, column);
					}

				});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected int rowValueCount(Object row) {
		// TODO Auto-generated method stub
		return this.acceptRow(row) ? this.rowMap((RK) row).size() : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		return this.acceptColumn(column) ? this.columnMap((CK) column).size()
				: 0;
	}

	protected transient volatile Set<RK> rowKeySet = null;
	protected transient volatile Set<CK> columnKeySet = null;

	@Override
	public Set<RK> rowKeySet() {
		// TODO Auto-generated method stub
		if (this.rowKeySet == null) {
			this.rowKeySet = this.rowFilter == null ? matrix.rowKeySet()
					: new FilteredSet<RK>(matrix.rowKeySet(), this.rowFilter);
		}
		return this.rowKeySet;
	}

	@Override
	public Set<CK> columnKeySet() {
		// TODO Auto-generated method stub
		if (this.columnKeySet == null) {
			this.columnKeySet = this.columnFilter == null ? matrix
					.columnKeySet() : new FilteredSet<CK>(
					matrix.columnKeySet(), this.columnFilter);
		}
		return this.columnKeySet;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		if (!hasFilter())
			return matrix.size();
		return this.entrySet().size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		if (!hasFilter())
			return matrix.isEmpty();
		Iterator<Entry<RK, CK, V>> entryIt = matrix.entrySet().iterator();
		while (entryIt.hasNext()) {
			if (this.accept(entryIt.next()))
				return false;
		}
		return true;
	}

	transient volatile Set<Matrix.Entry<RK, CK, V>> entrySet = null;

	@Override
	public Set<Matrix.Entry<RK, CK, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {
			entrySet = this.hasFilter() ? new FilteredSet<Matrix.Entry<RK, CK, V>>(
					this.matrix.entrySet(), this.entryFilter) : this.matrix
					.entrySet();
		}
		return entrySet;
	}
}
