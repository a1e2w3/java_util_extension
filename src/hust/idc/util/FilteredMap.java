package hust.idc.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class FilteredMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
	final Map<K, V> map;
	final Filter<? super K> filter;

	public FilteredMap(Map<K, V> map, Filter<? super K> filter) {
		super();
		this.map = Objects.requireNonNull(map);
		this.filter = filter;
	}

	@SuppressWarnings("unchecked")
	private final boolean accept(Object key) {
		try {
			return filter == null ? true : filter.accept((K) key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	private final boolean accept(Entry<K, V> entry) {
		return accept(entry.getKey());
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return this.accept(key) && map.containsKey(key);
	}

	@Override
	public V get(Object key) {
		// TODO Auto-generated method stub
		return this.accept(key) ? map.get(key) : null;
	}

	@Override
	public V put(K key, V value) {
		// TODO Auto-generated method stub
		return this.accept(key) ? map.put(key, value) : null;
	}

	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return this.accept(key) ? map.remove(key) : null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		Iterator<Entry<K, V>> entryIt = map.entrySet().iterator();
		while(entryIt.hasNext()){
			if(this.accept(entryIt.next()))
				entryIt.remove();
		}
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

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		Iterator<Entry<K, V>> entryIt = map.entrySet().iterator();
		while(entryIt.hasNext()){
			if(this.accept(entryIt.next()))
				return false;
		}
		return true;
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
								if (FilteredMap.this.accept(next = it.next())) {
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

				@Override
				public void clear() {
					// TODO Auto-generated method stub
					FilteredMap.this.clear();
				}

			};
		}
		return entrySet;
	}

}
