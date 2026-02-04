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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;
import cl.uoh.abaumgart.eqnauac.data.term.Atom;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;

public class EquivarianceSystem extends Printable implements DeepCopyable<EquivarianceSystem>, TermSymbols {
	public Set<Atom> atoms = new HashSet<>(atomSymb.getUsedSymbols());
	public final EquivarianceProblemSequence problemSet;
	public final Map<Atom, Atom> mappings;
	public List<Atom> atomList = new ArrayList<>();
	public final FreshnessCtx nabla;

	public EquivarianceSystem(EquivarianceProblemSequence problemSet, Map<Atom, Atom> mappings, FreshnessCtx nabla) {
		this.problemSet = problemSet;
		this.mappings = mappings;
		this.nabla = nabla;
	}

	public EquivarianceSystem(EquivarianceProblemSequence problemSet, FreshnessCtx nabla) {
		this(problemSet, new HashMap<>(), nabla);
	}

	@Override
	public EquivarianceSystem deepCopy() {
		return new EquivarianceSystem(problemSet.deepCopy(), new HashMap<>(mappings), nabla);
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		problemSet.printString(toPrint.append(" Equi-Problem: "));
		toPrint.append("\n     Mappings: " + mappings);
		nabla.printString(toPrint.append("\n        Nabla: "));
	}

}
