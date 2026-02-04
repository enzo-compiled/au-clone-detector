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

import cl.uoh.abaumgart.eqnauac.algo.ACDecomposer.ACDecomposition;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;

/**
 * Decomposition iterator for associative commutative functions.
 */
public class ACDecomposer implements Iterable<ACDecomposition>, Iterator<ACDecomposition>, TermSymbols {
	private final NominalTerm[] args;

	int numBuckets = 2;
	private int[] idx;
	private int[] aux;

	public ACDecomposer(NominalTerm[] args) {
		this.args = args;
		this.initBuckets();
	}

	/**
	 * Supports multiple sequential traversal. To perform nested or parallel
	 * traversal, new instances of {@linkplain ACDecomposer} must be created.
	 */
	@Override
	public Iterator<ACDecomposition> iterator() {
		if (this.numBuckets != 2) {
			this.numBuckets = 2;
			this.initBuckets();
		}
		return this;
	}

	@Override
	public boolean hasNext() {
		return numBuckets == 2;
	}

	@Override
	public ACDecomposition next() {
		var decomposition = new ACDecomposition();
		for (int i = 0; i < args.length; i++)
			decomposition.add(args[i], idx[i]);
		nextPartition();
		return decomposition;
	}

	private void initBuckets() {
		this.idx = new int[args.length];
		this.aux = new int[args.length];

		for (int i = args.length - numBuckets + 1; i < args.length; i++)
			idx[i] = aux[i] = i - args.length + numBuckets;
	}

	private void nextPartition() {
		for (int i = args.length - 1; i > 0; i--) {
			if (idx[i] < numBuckets - 1 && idx[i] <= aux[i - 1]) {
				idx[i]++;
				aux[i] = Math.max(aux[i], idx[i]);

				int n = args.length - numBuckets + aux[i];
				for (int j = i + 1; j < args.length; j++) {
					if (j > n) {
						idx[j] = aux[j] = numBuckets - args.length + j;
					} else {
						idx[j] = 0;
						aux[j] = aux[i];
					}
				}
				return;
			}
		}
		numBuckets--;
	}

	public static class ACDecomposition {
		public final List<NominalTerm> bucket0 = new ArrayList<>();
		public final List<NominalTerm> bucket1 = new ArrayList<>();

		public void add(NominalTerm term, int i) {
			if (i == 0)
				bucket0.add(term);
			else
				bucket1.add(term);
		}

		@Override
		public String toString() {
			return bucket0 + " / " + bucket1;
		}
	}
}