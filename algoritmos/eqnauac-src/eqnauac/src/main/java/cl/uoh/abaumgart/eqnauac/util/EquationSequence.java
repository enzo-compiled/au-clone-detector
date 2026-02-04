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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cl.uoh.abaumgart.eqnauac.data.Equation;

/**
 * A sequence of {@linkplain Equation}s
 */
public abstract class EquationSequence<U extends Printable, T extends Equation<U>> extends Printable
		implements DeepCopyable<EquationSequence<U, T>>, Cloneable, Iterable<T> {
	protected List<T> equations = new ArrayList<>();
	/**
	 * The separator is used to separate the equations of this system.
	 */
	public static String EQ_SEPARATOR = ";  ";

	/**
	 * Creates a new equation and adds it to the sequence.
	 * 
	 * @return the newly created and added equation.
	 */
	public abstract T addNewEquation(U left, U right);

	public int size() {
		return equations.size();
	}

	public boolean isEmpty() {
		return equations.isEmpty();
	}

	public T get(int idx) {
		return equations.get(idx);
	}

	public T remove(int idx, boolean allowSwapLast) {
		if (!allowSwapLast)
			return equations.remove(idx);
		var last = equations.removeLast();
		return idx == equations.size() ? last : equations.set(idx, last);
	}

	public T removeLast() {
		return equations.removeLast();
	}

	public void clear() {
		equations.clear();
	}

	public Stream<T> stream() {
		return equations.stream();
	}

	public void add(T element) {
		equations.add(element);
	}

	public void addAll(Collection<T> elements) {
		equations.addAll(elements);
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		boolean first = true;
		toPrint.append('{');
		for (T elem : equations) {
			if (first)
				first = false;
			else
				toPrint.write(EQ_SEPARATOR);
			elem.printString(toPrint);
		}
		toPrint.append('}');
	}

	@SuppressWarnings("unchecked")
	@Override
	public EquationSequence<U, T> deepCopy() {
		try {
			EquationSequence<U, T> ret = (EquationSequence<U, T>) clone();
			ret.equations = (List<T>) equations.stream().map(Equation::deepCopy)
					.collect(Collectors.toCollection(ArrayList::new));
			return ret;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return equations.iterator();
	}
}
