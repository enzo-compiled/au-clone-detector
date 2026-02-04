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

import java.util.Iterator;

import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

public class AntiUnif implements Iterable<AntiUnifySystem>, VirtualLogging {
	private final AntiUnifySystem originalAup;
	private final AntiUnifApplicable algorithm;
	private final int branchLimit;
	private AntiUnifSystemIter lastIterator;

	/**
	 * Use -1 to disable the limit.
	 */
	public AntiUnif(AntiUnifySystem originalAup, AntiUnifApplicable algorithm, int branchLimit) {
		this.originalAup = originalAup;
		this.algorithm = algorithm;
		this.branchLimit = branchLimit;
	}

	public boolean lastIteratorLimitExceeded() {
		return lastIterator.aupSystems.isLimitExceeded();
	}

	/**
	 * Iterates over the solved {@linkplain AntiUnifySystem}s. The algorithm might
	 * introduce branching and produce exponentially many results.
	 */
	@Override
	public Iterator<AntiUnifySystem> iterator() {
		lastIterator = new AntiUnifSystemIter(originalAup.deepCopy());
		return lastIterator;
	}

	public class AntiUnifSystemIter implements Iterator<AntiUnifySystem> {
		private final AntiUnifySystemStack aupSystems = new AntiUnifySystemStack(branchLimit);

		public AntiUnifSystemIter(AntiUnifySystem sysIn) {
			aupSystems.add(sysIn);
		}

		@Override
		public boolean hasNext() {
			return !aupSystems.isEmpty();
		}

		@Override
		public AntiUnifySystem next() {
			AntiUnifySystem nextSys = aupSystems.removeLast();
			logMsg(LVL_PROGRESS_AU, "\nSelecting anti-unify system:\n", nextSys);
			return algorithm.apply(nextSys, aupSystems);
		}
	}
}
