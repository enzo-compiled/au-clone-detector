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

import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.data.term.Variable;
import cl.uoh.abaumgart.eqnauac.util.EquationSequence;

public class AntiUnifyProblemSequence extends EquationSequence<NominalTerm, AntiUnifyProblem> implements TermSymbols {

	@Override
	public AntiUnifyProblem addNewEquation(NominalTerm left, NominalTerm right) {
		var aup = new AntiUnifyProblem(left, right);
		add(aup);
		return aup;
	}

	public Variable[] getGenVars() {
		return equations.stream().map(AntiUnifyProblem::getGeneralizationVar).toArray(Variable[]::new);
	}

	@Override
	public AntiUnifyProblemSequence deepCopy() {
		return (AntiUnifyProblemSequence) super.deepCopy();
	}

	public boolean isEmpty() {
		return equations.isEmpty();
	}

}
