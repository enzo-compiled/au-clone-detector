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
 * A suspension is a {@linkplain NominalTerm} which consists of a
 * {@linkplain Permutation} and a {@linkplain Variable}.
 * 
 * @author Alexander Baumgartner
 */
public class Suspension extends NominalTerm {
	public Permutation perm;
	public Variable vari;

	/**
	 * Instantiates a new suspension with an empty {@link Permutation}.
	 */
	public Suspension(Variable var) {
		this(var, new Permutation());
	}

	/**
	 * Instantiates a new suspension with the given {@link Permutation}. The given
	 * {@link Permutation} must not be null.
	 */
	public Suspension(Variable var, Permutation perm) {
		this.perm = perm;
		this.vari = var;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Suspension) {
			Suspension oSusp = (Suspension) other;
			// Variable instances are unique
			return oSusp.vari == vari && oSusp.perm.equals(perm);
		}
		return false;
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		if (!perm.isEmpty()) {
			toPrint.append(perm.toString(false));
			toPrint.append('·');
		}
		toPrint.write(vari.toString());
	}

	@Override
	public NominalTerm swap(Atom a1, Atom a2) {
		perm.addSwappingHead(a1, a2);
		return this;
	}

	@Override
	public boolean isFresh(Atom atom, FreshnessCtx nabla) {
		return nabla.contains(perm.permuteInverse(atom), vari);
	}

	@Override
	public Suspension deepCopy() {
		return new Suspension(vari, perm.deepCopy());
	}

	@Override
	public NominalTerm substitute(Variable fromVar, NominalTerm toTerm) {
		if (vari != fromVar)
			return this;
		return toTerm.deepCopy().permute(perm);
	}

	@Override
	public NominalTerm permute(Permutation perm) {
		if (!perm.isEmpty())
			this.perm = Permutation.compose(perm, this.perm);
		return this;
	}

	@Override
	public Symbol getHead() {
		return vari;
	}

	@Override
	public int getTypeOrdinal() {
		return ORD_SUSP;
	}

}
