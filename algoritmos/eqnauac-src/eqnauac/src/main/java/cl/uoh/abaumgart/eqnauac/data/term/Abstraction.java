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
import cl.uoh.abaumgart.eqnauac.data.InputParser;

/**
 * An abstraction is a {@linkplain NominalTerm} which consists of a bound symbol
 * and a subterm.
 * 
 * @author Alexander Baumgartner
 */
public class Abstraction extends NominalTerm {
	public Atom boundAtom;
	public NominalTerm subTerm;

	public Abstraction(Atom boundAtom, NominalTerm subTerm) {
		this.boundAtom = boundAtom;
		this.subTerm = subTerm;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Abstraction) {
			Abstraction oAbs = (Abstraction) other;
			// Atom instances are unique
			return oAbs.boundAtom == boundAtom && oAbs.subTerm.equals(subTerm);
		}
		return false;
	}

	public Atom getBoundAtom() {
		return boundAtom;
	}

	public void setBoundAtom(Atom boundAtom) {
		this.boundAtom = boundAtom;
	}

	public NominalTerm getSubTerm() {
		return subTerm;
	}

	public void setSubTerm(NominalTerm subTerm) {
		this.subTerm = subTerm;
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		toPrint.write(boundAtom.toString());
		toPrint.write(InputParser.cp_abstraction);
		subTerm.printString(toPrint);
	}

	@Override
	public NominalTerm swap(Atom a1, Atom a2) {
		boundAtom = boundAtom.swap(a1, a2);
		subTerm = subTerm.swap(a1, a2);
		return this;
	}

	@Override
	protected boolean propagate(TraverseCallBack<NominalTerm> callBack) {
		return subTerm.traverse(callBack);
	}

	@Override
	public boolean isFresh(Atom atom, FreshnessCtx nabla) {
		if (atom == boundAtom) // #-abst-1
			return true;
		return subTerm.isFresh(atom, nabla); // #-abst-2
	}

	@Override
	public Abstraction deepCopy() {
		return new Abstraction(boundAtom, subTerm.deepCopy());
	}

	@Override
	public NominalTerm substitute(Variable fromVar, NominalTerm toTerm) {
		subTerm = subTerm.substitute(fromVar, toTerm);
		return this;
	}

	@Override
	public Abstraction permute(Permutation perm) {
		boundAtom = boundAtom.permute(perm);
		subTerm = subTerm.permute(perm);
		return this;
	}

	@Override
	public Symbol getHead() {
		return Symbol.DUMMY;
	}

	@Override
	public int getTypeOrdinal() {
		return ORD_ABS;
	}

	@Override
	public int compareTo(NominalTerm o) {
		if (o instanceof Abstraction oAbs) {
			Atom c = TermSymbols.atomSymb.freshTmpSymbol();
			return subTerm.deepCopy().swap(c, boundAtom).compareTo(oAbs.subTerm.deepCopy().swap(c, oAbs.boundAtom));
		}
		return super.compareTo(o);
	}
}
