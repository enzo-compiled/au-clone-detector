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

import cl.uoh.abaumgart.eqnauac.util.Printable;

/**
 * A {@linkplain FunctionSymbol} has a unique name and may be typed by a sort of
 * data. The arity and the sorts of its arguments can be specified explicitly.
 * If the arity is unknown, then the value is set to -1 (see
 * {@linkplain #isArityUnknown()}).
 * 
 * @author Alexander Baumgartner
 */
public class FunctionSymbol extends Printable implements Symbol, Comparable<FunctionSymbol> {
	public final String name;

	FunctionSymbol(String name) {
		this.name = name;
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		toPrint.append(name);
	}

	@Override
	public int compareTo(FunctionSymbol o) {
		return name.compareTo(o.name);
	}
}
