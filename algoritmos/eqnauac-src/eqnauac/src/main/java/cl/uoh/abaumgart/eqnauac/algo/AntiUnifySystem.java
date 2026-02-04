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

import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;
import cl.uoh.abaumgart.eqnauac.data.term.Suspension;
import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;

public class AntiUnifySystem extends Printable implements DeepCopyable<AntiUnifySystem> {
	public final AntiUnifyProblemSequence problemSet;
	public final AntiUnifyProblemSequence store;
	public final FreshnessCtx nablaIn;
	public final FreshnessCtx nablaOut;
	public final Substitution sigma;

	public AntiUnifySystem(AntiUnifyProblemSequence problemSet, AntiUnifyProblemSequence store, FreshnessCtx nablaIn,
			Substitution sigma) {
		this(problemSet, store, nablaIn, new FreshnessCtx(), sigma);
	}

	public AntiUnifySystem(AntiUnifyProblemSequence problemSet, AntiUnifyProblemSequence store, FreshnessCtx nablaIn,
			FreshnessCtx nablaOut, Substitution sigma) {
		this.problemSet = problemSet;
		this.store = store;
		this.nablaIn = nablaIn;
		this.nablaOut = nablaOut;
		this.sigma = sigma;
	}

	public AntiUnifySystem(AntiUnifyProblemSequence problemSet, FreshnessCtx nabla) {
		this(problemSet, new AntiUnifyProblemSequence(), nabla, new Substitution());
		for (AntiUnifyProblem aup : problemSet)
			sigma.put(aup.generalizationVar, new Suspension(aup.generalizationVar));
	}

	@Override
	public AntiUnifySystem deepCopy() {
		return new AntiUnifySystem(problemSet.deepCopy(), store.deepCopy(), nablaIn, nablaOut.deepCopy(),
				sigma.deepCopy());
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		problemSet.printString(toPrint.append("   Problem: "));
		store.printString(toPrint.append("\n     Store: "));
		nablaOut.printString(toPrint.append("\n     Nabla: "));
		sigma.printString(toPrint.append("\n     Sigma: "));
	}

}
