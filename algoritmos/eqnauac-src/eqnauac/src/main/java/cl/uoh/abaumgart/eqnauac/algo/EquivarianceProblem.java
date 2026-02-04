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

package cl.uoh.abaumgart.eqnauac.algo;

import java.io.IOException;
import java.io.Writer;

import cl.uoh.abaumgart.eqnauac.data.Equation;
import cl.uoh.abaumgart.eqnauac.data.NominalPair;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;

/**
 * This class represents an equivariance problem (an equation of the form t = s)
 * of two {@linkplain NominalPair}s. A permutation is computed by the rule based
 * system {@linkplain EquivarianceAlgoOLD}. It is a sub algorithm of the rule
 * based anti-unification algorithm {@linkplain AntiUnifyAlgoOLD}.
 * 
 * @author Alexander Baumgartner
 */
public class EquivarianceProblem extends Equation<NominalTerm> {
	public static String eqSeparator = " = ";

	public EquivarianceProblem(NominalTerm left, NominalTerm right) {
		super(left, right);
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		printString(toPrint, left, right);
	}

	public static void printString(Writer toPrint, NominalTerm left, NominalTerm right) throws IOException {
		left.printString(toPrint);
		toPrint.write(eqSeparator);
		right.printString(toPrint);
	}

	@Override
	public Equation<NominalTerm> deepCopy() {
		return new EquivarianceProblem(left.deepCopy(), right.deepCopy());
	}
}