package hust.idc.util.matrix;

import java.util.Map;
import java.util.Set;

import hust.idc.util.pair.AbstractImmutableUnorderedPair;
import hust.idc.util.pair.Pair;

public abstract class AbstractSymmetricMatrix<K, V> extends
		AbstractMatrix<K, K, V> implements SymmetricMatrix<K, V>, Matrix<K, K, V> {

	protected AbstractSymmetricMatrix() {
		super();
	}

	@Override
	public abstract int demension();

	@Override
	public int rows() {
		// TODO Auto-generated method stub
		return demension();
	}

	@Override
	public int columns(){
		return demension();
	}

	@Override
	public boolean containsColumn(Object column) {
		// TODO Auto-generated method stub
		return containsRow(column);
	}

	@Override
	public void removeColumn(K column) {
		// TODO Auto-generated method stub
		removeRow(column);
	}

	@Override
	public Map<K, V> columnMap(K column) {
		// TODO Auto-generated method stub
		return rowMap(column);
	}

	@Override
	protected int columnValueCount(Object column) {
		// TODO Auto-generated method stub
		return rowValueCount(column);
	}

	@Override
	public Set<K> columnKeySet() {
		// TODO Auto-generated method stub
		return rowKeySet();
	}

	@Override
	public abstract Set<Entry<K, K, V>> entrySet();

	public static abstract class AbstractSymmetricMatrixEntry<K, V> extends
			AbstractEntry<K, K, V> implements
			SymmetricMatrixEntry<K, V>, Entry<K, K, V> {

		protected AbstractSymmetricMatrixEntry() {
			super();
		}

		@Override
		public abstract K getRowKey();

		@Override
		public abstract K getColumnKey();

		@Override
		public abstract V getValue();

		@Override
		public Pair<K, K> getKeyPair() {
			// TODO Auto-generated method stub
			if (keyPair == null) {
				keyPair = new AbstractImmutableUnorderedPair<K>() {

					@Override
					public K getFirst() {
						// TODO Auto-generated method stub
						return AbstractSymmetricMatrixEntry.this.getRowKey();
					}

					@Override
					public K getSecond() {
						// TODO Auto-generated method stub
						return AbstractSymmetricMatrixEntry.this.getColumnKey();
					}

				};
			}
			return keyPair;
		}

		@Override
		public boolean match(Object row, Object column) {
			// TODO Auto-generated method stub
			return super.match(row, column) || super.match(column, row);
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
			if (!(obj instanceof SymmetricMatrix.SymmetricMatrixEntry)) {
				return ((Matrix.Entry<?, ?, ?>) obj).equals(this);
			}
			SymmetricMatrix.SymmetricMatrixEntry<?, ?> other = (SymmetricMatrix.SymmetricMatrixEntry<?, ?>) obj;
			return other.match(getRowKey(), getColumnKey())
					&& eq(this.getValue(), other.getValue());
		}

	}

	public static abstract class AbstractImmutableSymmetricMatrixEntry<K, V>
			extends AbstractSymmetricMatrixEntry<K, V> implements
			SymmetricMatrixEntry<K, V>, Entry<K, K, V> {

		protected AbstractImmutableSymmetricMatrixEntry() {
			super();
		}

		@Override
		public abstract K getRowKey();

		@Override
		public abstract K getColumnKey();

		@Override
		public abstract V getValue();

		@Override
		public final V setValue(V value) {
			throw new UnsupportedOperationException();
		}

	}

}
