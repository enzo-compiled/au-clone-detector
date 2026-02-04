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
import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;
import cl.uoh.abaumgart.eqnauac.util.Traversable;

/**
 * This is the base class for nominal terms like {@linkplain Abstraction},
 * {@linkplain FunctionApplication}, {@linkplain Atom}, {@linkplain Suspension}.
 * 
 * @author Alexander Baumgartner
 */
public abstract class NominalTerm extends Printable
		implements DeepCopyable<NominalTerm>, Traversable<NominalTerm>, Comparable<NominalTerm> {
	static final int ORD_ATOM = 10;
	static final int ORD_APPL = 20;
	static final int ORD_SUSP = 30;
	static final int ORD_ABS = 40;

	public abstract NominalTerm swap(Atom a1, Atom a2);

	public abstract boolean isFresh(Atom atom, FreshnessCtx nabla);

	public abstract Symbol getHead();

	public abstract int getTypeOrdinal();

	public abstract NominalTerm substitute(Variable fromVar, NominalTerm toTerm);

	public abstract NominalTerm permute(Permutation perm);

	/**
	 * Dummy term might be used as place holder.
	 */
	public static final NominalTerm DUMMY = new NominalTerm() {
		@Override
		public NominalTerm deepCopy() {
			return this;
		}

		@Override
		public NominalTerm swap(Atom a1, Atom a2) {
			return this;
		}

		@Override
		public boolean isFresh(Atom atom, FreshnessCtx nabla) {
			return true;
		}

		@Override
		public Symbol getHead() {
			return null;
		}

		@Override
		public int getTypeOrdinal() {
			return 0;
		}

		@Override
		public NominalTerm substitute(Variable fromVar, NominalTerm toTerm) {
			return this;
		}

		@Override
		public NominalTerm permute(Permutation perm) {
			return this;
		}

		@Override
		public void printString(Writer toPrint) throws IOException {
			toPrint.append("DUMMY");
		}
	};

	/**
	 * Traverses the term tree and executes the callback function at every subterm.
	 * 
	 * @see Traversable
	 * @see cl.uoh.abaumgart.eqnauac.util.Traversable.TraverseCallBack
	 */
	public boolean traverse(TraverseCallBack<NominalTerm> callBack) {
		if (!callBack.exec(this))
			if (propagate(callBack))
				return true;
		return callBack.execBackward(this);
	}

	/**
	 * Propagate traversing to sub-terms. Please override if needed!!!
	 * 
	 * @see Traversable
	 * @see cl.uoh.abaumgart.eqnauac.util.Traversable.TraverseCallBack
	 */
	protected boolean propagate(TraverseCallBack<NominalTerm> callBack) {
		return false;
	}

	@Override
	public int compareTo(NominalTerm o) {
		int cmp = getTypeOrdinal() - o.getTypeOrdinal();
		return cmp != 0 ? cmp : getHead().toString().compareTo(o.getHead().toString());
	}

}
