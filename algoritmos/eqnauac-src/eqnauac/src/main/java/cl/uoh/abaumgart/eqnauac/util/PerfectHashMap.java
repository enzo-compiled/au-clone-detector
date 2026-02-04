/*
 * Copyright 2025 Alexander Baumgartner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cl.uoh.abaumgart.eqnauac.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * The key objects must implement {@linkplain PerfectHashable} in order to
 * specify the indexes.
 */
public class PerfectHashMap<K extends PerfectHashable, V> extends AbstractMap<K, V> {

	private final K[] keys;
	private final V[] vals;

	public PerfectHashMap(K[] allKeys) {
		for (int i = 0; i < allKeys.length; i++)
			allKeys[i].setHashIdx(i);
		this.keys = allKeys;
		@SuppressWarnings("unchecked")
		V[] values = (V[]) new Object[allKeys.length];
		this.vals = values;
	}

	public PerfectHashMap(PerfectHashMap<K, V> toCopy) {
		this.keys = toCopy.keys;
		this.vals = Arrays.copyOf(toCopy.vals, toCopy.vals.length);
	}

	@Override
	public V get(Object key) {
		return key instanceof PerfectHashable phKey ? vals[phKey.getHashIdx()] : null;
	}

	@Override
	public V put(K key, V value) {
		V oldVal = vals[key.getHashIdx()];
		vals[key.getHashIdx()] = value;
		return oldVal;
	}

	@Override
	public V remove(Object key) {
		V oldVal = get(key);
		if (oldVal != null) {
			vals[((PerfectHashable) key).getHashIdx()] = null;
		}
		return oldVal;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	private class EntrySet extends AbstractSet<Entry<K, V>> {

		@Override
		public int size() {
			return keys.length;
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new Iterator<>() {
				int idx;

				@Override
				public boolean hasNext() {
					return idx < keys.length;
				}

				@Override
				public Entry<K, V> next() {
					return new MyEntry(idx++);
				}

			};

		}

		class MyEntry implements Entry<K, V> {
			private int currIdx;

			public MyEntry(int idx) {
				currIdx = idx;
			}

			@Override
			public K getKey() {
				return keys[currIdx];
			}

			@Override
			public V getValue() {
				return vals[currIdx];
			}

			@Override
			public V setValue(V value) {
				V oldVal = vals[currIdx];
				vals[currIdx] = value;
				return oldVal;
			}
		}
	}
}
