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

package cl.uoh.abaumgart.eqnauac.data;

import java.io.IOException;
import java.io.Writer;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;

/**
 * A system of {@linkplain Equation}s
 * 
 * @author Alexander Baumgartner
 */
public abstract class EquationSystem<T extends Equation<?>> extends Printable
		implements DeepCopyable<EquationSystem<T>>, Cloneable, Iterable<T> {
	private Deque<T> equations = new LinkedList<>();
	/**
	 * The separator is used to separate the equations of this system.
	 */
	public static String EQ_SEPARATOR = ";  ";
	/**
	 * The output will look like "{@linkplain #EQ_PREFIX1}#i#
	 * {@linkplain #EQ_PREFIX2}" where #i# is the index of an equation.
	 */
	public static String EQ_PREFIX1 = null;
	/**
	 * If {@linkplain #EQ_PREFIX2} is null then the output of the index will also be
	 * omitted.
	 */
	public static String EQ_PREFIX2 = null;

	/**
	 * You have to implement this method, so that an algorithm is able to
	 * instantiate new {@linkplain Equation}s of arbitrary types.
	 */
	public abstract T newEquation(NominalTerm left, NominalTerm right);

	/**
	 * Returns the number of equations in this system.
	 */
	public int size() {
		return equations.size();
	}

	/**
	 * Tests whether the system of equations is empty.
	 */
	public boolean isEmpty() {
		return equations.isEmpty();
	}

	/**
	 * Adds a new equation to this system as tail.
	 */
	public void add(T equation) {
		equations.add(equation);
	}

	/**
	 * Adds a new equation to this system as head.
	 */
	public void addFirst(T equation) {
		equations.addFirst(equation);
	}

	/**
	 * Retrieves and removes the first element of this system of equations or
	 * returns null if it is empty.
	 */
	public T popFirst() {
		return equations.pollFirst();
	}

	/**
	 * Retrieves and removes the last element of this system of equations or returns
	 * null if it is empty.
	 */
	public T popLast() {
		return equations.pollLast();
	}

	/**
	 * Retrieves the first element of this system of equations or returns null if it
	 * is empty.
	 */
	public T getFirst() {
		return equations.peekFirst();
	}

	/**
	 * Retrieves the last element of this system of equations or returns null if it
	 * is empty.
	 */
	public T getLast() {
		return equations.peekLast();
	}

	/**
	 * Removes all the equations of this system.
	 */
	public void clear() {
		equations.clear();
	}

	@Override
	public Iterator<T> iterator() {
		return equations.iterator();
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		int i = 0;
		for (T elem : equations) {
			writePrefix(toPrint, i);
			elem.printString(toPrint);
			i++;
		}
	}

	public static void writePrefix(Writer toPrint, int i) throws IOException {
		if (EQ_SEPARATOR != null && i != 0)
			toPrint.write(EQ_SEPARATOR);
		if (EQ_PREFIX1 != null)
			toPrint.write(EQ_PREFIX1);
		if (EQ_PREFIX2 != null) {
			toPrint.write(String.valueOf(i + 1));
			toPrint.write(EQ_PREFIX2);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public EquationSystem<T> deepCopy() {
		try {
			EquationSystem<T> ret = (EquationSystem<T>) clone();
			Deque<T> eqNew = new LinkedList<>();
			for (T eq : equations)
				eqNew.add((T) eq.deepCopy());
			ret.equations = eqNew;
			return ret;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
