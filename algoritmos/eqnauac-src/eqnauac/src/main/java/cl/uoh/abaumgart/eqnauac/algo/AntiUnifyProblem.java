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
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.data.term.Variable;

/**
 * This class represents an anti-unification problem (AUP) which consists of one
 * generalization variable (the most general generalization) and an
 * {@linkplain Equation} of terms.
 * 
 * @author Alexander Baumgartner
 */
public class AntiUnifyProblem extends Equation<NominalTerm> implements TermSymbols {
	public Variable generalizationVar;
	public static String varSeparator = ": ";
	public static String eqSeparator = " =^= ";

	/**
	 * A constructor who takes two nominal terms and a factory to instantiate a
	 * fresh generalization variable.
	 */
	public AntiUnifyProblem(NominalTerm left, NominalTerm right) {
		this(left, right, termVar.fresh1Symbol());
	}

	public AntiUnifyProblem(NominalTerm left, NominalTerm right, Variable generalizationVar) {
		super(left, right);
		this.generalizationVar = generalizationVar;
	}

	/**
	 * The most general generalization.
	 */
	public Variable getGeneralizationVar() {
		return generalizationVar;
	}

	@Override
	public AntiUnifyProblem deepCopy() {
		return new AntiUnifyProblem(left.deepCopy(), right.deepCopy(), generalizationVar);
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		printString(toPrint, left, right, generalizationVar);
	}

	public static void printString(Writer toPrint, NominalTerm left, NominalTerm right, Variable genVar)
			throws IOException {
		toPrint.write(String.valueOf(genVar));
		toPrint.write(varSeparator);
		left.printString(toPrint);
		toPrint.write(eqSeparator);
		right.printString(toPrint);
	}

	public void set(Variable genVar, NominalTerm termLeft, NominalTerm termRight) {
		set(termLeft, termRight);
		this.generalizationVar = genVar;
	}

}