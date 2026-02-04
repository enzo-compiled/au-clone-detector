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

import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;

/**
 * An {@linkplain Atom} is a {@linkplain NominalTerm}, consisting of the symbol
 * name and an optional origin name. The origin name may be used to store the
 * name of a bound symbol before renaming it. Atoms may be typed by a sort of
 * symbol.
 * 
 * @author Alexander Baumgartner
 */
public class Atom extends NominalTerm implements Symbol {
	public final String name;

	Atom(String name) {
		this.name = name;
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		toPrint.write(name);
	}

	@Override
	public Atom swap(Atom a1, Atom a2) {
		if (this == a1)
			return a2;
		if (this == a2)
			return a1;
		return this;
	}

	@Override
	public boolean isFresh(Atom atom, FreshnessCtx nabla) {
		return this != atom; // not same object
	}

	@Override
	public NominalTerm deepCopy() {
		return this;
	}

	@Override
	public NominalTerm substitute(Variable fromVar, NominalTerm toTerm) {
		return this;
	}

	@Override
	public Atom permute(Permutation perm) {
		return perm.permute(this);
	}

	@Override
	public Symbol getHead() {
		return this;
	}

	@Override
	public int getTypeOrdinal() {
		return ORD_ATOM;
	}
}
