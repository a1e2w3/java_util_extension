package hust.idc.util.matrix;

import hust.idc.util.pair.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Matrix<RK, CK, V> {
	int size();
	
	int rows();
	
	int columns();
	
	boolean isEmpty();
	
	boolean containsRow(Object row);
	
	boolean containsColumn(Object column);
	
	boolean containsKey(Object row, Object column);
	
	boolean containsKey(Pair<?, ?> keyPair);
	
	boolean containsValue(Object value);
	
	V get(Object row, Object column);
	
	V get(Pair<?, ?> keyPair);
	
	V set(RK row, CK column, V value);

	V set(Pair<? extends RK, ? extends CK> keyPair, V value);
	
	void setAll(Matrix<? extends RK, ? extends CK, ? extends V> matrix);
	
	void setAll(Map<? extends Pair<? extends RK, ? extends CK>, ? extends V> map);
	
	V remove(Object row, Object column);
	
	V remove(Pair<?, ?> keyPair);
	
	void removeRow(Object row);
	
	void removeColumn(Object column);
	
	void clear();
	
	Set<RK> rowKeySet();
	
	Set<CK> columnKeySet();
	
	Collection<V> values();
	
	Map<CK, V> rowMap(Object row);
	
	Map<RK, V> columnMap(Object column);
	
	Set<Entry<RK, CK, V>> entrySet();
	
	Set<Pair<RK, CK>> keyPairSet();
	
	boolean equals(Object o);
	
	int hashCode();
	
	static interface Entry<RK, CK, V>{
		RK getRowKey();
		
		CK getColumnKey();
		
		Pair<RK, CK> getKeyPair();
		
		V getValue();
		
		V setValue(V value);
		
		Map.Entry<CK, V> rowMapEntry();
		
		Map.Entry<RK, V> columnMapEntry();
		
		boolean match(Object row, Object column);
		
		boolean match(Pair<?, ?> keyPair);
		
		boolean equals(Object o);
		
		int hashCode();
		
	}

}
