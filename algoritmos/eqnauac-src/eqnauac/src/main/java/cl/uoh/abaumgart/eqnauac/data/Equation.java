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

import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;

/**
 * An equation consists of two terms.
 * 
 * @author Alexander Baumgartner
 */
public abstract class Equation<T extends Printable> extends Printable implements DeepCopyable<Equation<T>> {
	public T left, right;

	protected Equation(T left, T right) {
		this.left = left;
		this.right = right;
	}

	public void set(T left, T right) {
		this.left = left;
		this.right = right;
	}
}
