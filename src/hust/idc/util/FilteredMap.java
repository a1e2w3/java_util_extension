package hust.idc.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class FilteredMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
	final Map<K, V> map;
	final Filter<? super K> filter;

	FilteredMap(Map<K, V> map, Filter<? super K> filter) {
		super();
		if (map == null)
			throw new NullPointerException();
		this.map = map;
		this.filter = filter;
	}

	private final boolean accept(K key) {
		return filter == null ? true : filter.accept(key);
	}

	private final boolean accept(Entry<K, V> entry) {
		return accept(entry.getKey());
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		int count = 0;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (FilteredMap.this.accept(entry.getKey()))
				++count;
		}
		return count;
	}

	transient volatile Set<Map.Entry<K, V>> entrySet = null;

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		if (entrySet == null) {
			entrySet = new AbstractSet<Map.Entry<K, V>>() {

				@Override
				public Iterator<Map.Entry<K, V>> iterator() {
					// TODO Auto-generated method stub
					return new Iterator<Map.Entry<K, V>>() {
						Iterator<Map.Entry<K, V>> it = map.entrySet()
								.iterator();
						Map.Entry<K, V> next = null;
						boolean removable = false;

						private void advance() {
							while (it.hasNext()) {
								if (FilteredMap.this.accept(next = it.next())){
									removable = true;
									return;
								}
							}
							next = null;
							removable = false;
						}

						@Override
						public boolean hasNext() {
							// TODO Auto-generated method stub
							if (next != null)
								return true;
							else
								advance();
							return next != null;
						}

						@Override
						public java.util.Map.Entry<K, V> next() {
							// TODO Auto-generated method stub
							if (next == null)
								advance();
							if (next == null)
								throw new NoSuchElementException();
							Map.Entry<K, V> current = next;
							next = null;
							return current;
						}

						@Override
						public void remove() {
							// TODO Auto-generated method stub
							if (!removable)
								throw new IllegalStateException();
							it.remove();
							removable = false;
						}

					};

				}

				@Override
				public int size() {
					// TODO Auto-generated method stub
					return FilteredMap.this.size();
				}

			};
		}
		return entrySet;
	}

}
