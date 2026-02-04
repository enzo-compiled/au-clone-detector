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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cl.uoh.abaumgart.eqnauac.data.term.Permutation;
import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

public class Equivariance implements Iterable<Permutation>, VirtualLogging {
	private final EquivarianceSystem eqProblem;
	private final EquivarianceApplicable algorithm;

	public Equivariance(EquivarianceSystem eqProblem, EquivarianceApplicable algorithm) {
		this.eqProblem = eqProblem;
		this.algorithm = algorithm;
	}

	/**
	 * Iterates over the solved {@linkplain EquivarianceAlgoOLD}s. The algorithm
	 * might introduce branching and produce exponentially many results.
	 */
	@Override
	public Iterator<Permutation> iterator() {
		return new EquivarianceSystemIter(eqProblem.deepCopy());
	}

	public class EquivarianceSystemIter implements Iterator<Permutation> {
		private final List<EquivarianceSystem> eqSystems = new ArrayList<>();
		private Permutation nextResult = null;

		public EquivarianceSystemIter(EquivarianceSystem sysIn) {
			eqSystems.add(sysIn);
		}

		@Override
		public boolean hasNext() {
			while (nextResult == null && !eqSystems.isEmpty()) {
				EquivarianceSystem nextSys = eqSystems.removeLast();
				logMsg(LVL_PROGRESS_EQ, "\nSelecting equivariance system:\n", nextSys);
				nextResult = algorithm.apply(nextSys, eqSystems);
				logMsg(LVL_PROGRESS_EQ, "Equivariance system evaluates to ", nextResult);
			}
			return nextResult != null;
		}

		@Override
		public Permutation next() {
			var current = nextResult;
			nextResult = null;
			return current;
		}
	}
}
