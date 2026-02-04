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

package cl.uoh.abaumgart.eqnauac.data.term;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import cl.uoh.abaumgart.eqnauac.util.Printable;

public final class SymbolSet<T extends Printable> {
	private String fresh1SymbolPrefix;
	private String fresh2SymbolPrefix;
	private String tmpSymbolPrefix;
	private Function<String, T> symbolProvider;

	private Class<? extends Printable> clazz;
	private Map<String, T> usedSymbols;
	private long fresh1SymbolIdx;
	private long fresh2SymbolIdx;
	private long freshTmpSymbolIdx;

	public SymbolSet(String fresh1SymbolPrefix, String fresh2SymbolPrefix, Function<String, T> symbolProvider) {
		this.fresh1SymbolPrefix = fresh1SymbolPrefix;
		this.fresh2SymbolPrefix = fresh2SymbolPrefix;
		this.tmpSymbolPrefix = "~" + fresh2SymbolPrefix; // ~ has higher ordinal number than other ASCII symbols.
		this.symbolProvider = symbolProvider;
		this.clazz = symbolProvider.apply("").getClass();
		reset();
	}

	public T parseSymbol(String name) {
		return usedSymbols.compute(name, (k, v) -> v == null ? symbolProvider.apply(k) : v);
	}

	public T[] fresh1Symbols(int length) {
		@SuppressWarnings("unchecked")
		T[] ret = (T[]) Array.newInstance(clazz, length);
		for (int i = 0; i < ret.length; i++)
			ret[i] = fresh1Symbol();
		return ret;
	}

	public T[] fresh2Symbols(int length) {
		@SuppressWarnings("unchecked")
		T[] ret = (T[]) Array.newInstance(clazz, length);
		for (int i = 0; i < ret.length; i++)
			ret[i] = fresh2Symbol();
		return ret;
	}

	public T fresh1Symbol() {
		String name;
		do {
			name = fresh1SymbolPrefix + ++fresh1SymbolIdx;
		} while (usedSymbols.containsKey(name));
		return parseSymbol(name);
	}

	public T fresh2Symbol() {
		String name;
		do {
			name = fresh2SymbolPrefix + ++fresh2SymbolIdx;
		} while (usedSymbols.containsKey(name));
		return parseSymbol(name);
	}

	public void reset() {
		this.usedSymbols = new TreeMap<>();
		this.fresh1SymbolIdx = 0;
		this.fresh2SymbolIdx = 0;
	}

	public Collection<T> getUsedSymbols() {
		return usedSymbols.values();
	}

	public abstract static class Symbol extends Printable implements Comparable<Symbol> {
		private final String name;

		/**
		 * Symbols have unique instances. Use
		 * {@linkplain SymbolSet#parseSymbol(String, Function)}!
		 */
		protected Symbol(String name) {
			this.name = name;
		}

		@Override
		public void printString(Writer toPrint) throws IOException {
			toPrint.write(name);
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public int compareTo(Symbol other) {
			return name.compareTo(other.name);
		}
	}

	public T freshTmpSymbol() {
		if (freshTmpSymbolIdx > 999999) {
			tmpSymbolPrefix = "~" + tmpSymbolPrefix;
			freshTmpSymbolIdx = 0;
		}
		String freshName = tmpSymbolPrefix + String.format("%06d", freshTmpSymbolIdx);
		freshTmpSymbolIdx++;
		return symbolProvider.apply(freshName);
	}

}
