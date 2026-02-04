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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import cl.uoh.abaumgart.eqnauac.data.term.Abstraction;
import cl.uoh.abaumgart.eqnauac.data.term.Atom;
import cl.uoh.abaumgart.eqnauac.data.term.FunctionApplication;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.Permutation;
import cl.uoh.abaumgart.eqnauac.data.term.Suspension;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.data.term.Variable;
import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

public class EquivarianceAlgorithm implements EquivarianceApplicable, TermSymbols, VirtualLogging {

	@Override
	public Permutation apply(EquivarianceSystem currentSystem, List<EquivarianceSystem> systemsBranchStack) {
		if (!reduceAll(currentSystem, systemsBranchStack))
			return null;
		return computePermutation(currentSystem);
	}

	private Permutation computePermutation(EquivarianceSystem currSys) {
		logMsg(LVL_DEBUG, "Starting phase 2 of equivariance algorithm (permutation phase)");
		Map<Atom, Atom> atomMap = currSys.mappings;
		Set<Atom> atoms = currSys.atoms;
		Permutation pi = new Permutation();
		for (Atom lAtom : currSys.atomList) {
			Atom rAtom = atomMap.remove(lAtom);
			lAtom = pi.permute(lAtom);
			if (lAtom == rAtom) {
				atoms.remove(lAtom);
				logRuleAppl("Rem-E", currSys);
			} else if (atoms.contains(lAtom) && atoms.remove(rAtom)) {
				pi.addSwappingHead(lAtom, rAtom);
				logRuleAppl("Sol-E", currSys);
			} else {
				logMsg(LVL_DEBUG,
						"Permutation application not possible to " + lAtom + EquivarianceProblem.eqSeparator + rAtom);
				return null; // No rule applicable
			}
		}
		return pi;
	}

	private boolean reduceAll(EquivarianceSystem currentSystem, Collection<EquivarianceSystem> systemsBranchStack) {
		logMsg(LVL_DEBUG, "Starting phase 1 of equivariance algorithm (simplification phase)");
		while (!currentSystem.problemSet.isEmpty()) {
			if (!canAdvance(currentSystem.problemSet.removeLast(), currentSystem, systemsBranchStack))
				return false;
		}
		return true;
	}

	private boolean canAdvance(EquivarianceProblem currProblem, EquivarianceSystem currSys,
			Collection<EquivarianceSystem> sysBranchStack) {
		return tryApplyAtm(currProblem, currSys) || tryApplySus(currProblem, currSys)
				|| tryApplyDec(currProblem, currSys, sysBranchStack) || tryApplyAbs(currProblem, currSys);
	}

	private boolean tryApplyAtm(EquivarianceProblem currProblem, EquivarianceSystem currSys) {
		if (currProblem.left instanceof Atom lAtm && currProblem.right instanceof Atom rAtm) {
			logRuleAppl("Rem-E", currSys);
			return putAtom(currSys, lAtm, rAtm);
		}
		return false;
	}

	private boolean tryApplySus(EquivarianceProblem currProblem, EquivarianceSystem currSys) {
		if (currProblem.left instanceof Suspension lSusp && currProblem.right instanceof Suspension rSusp) {
			Variable lVar = lSusp.vari;
			if (lVar != rSusp.vari) {
				return false;
			}
			Permutation lPerm = lSusp.perm;
			Permutation rPerm = rSusp.perm;
			logRuleAppl("Sus-E", currSys);
			for (Atom atom : currSys.atoms) {
				if (!currSys.nabla.contains(atom, lVar)) {
					if (!putAtom(currSys, lPerm.permute(atom), rPerm.permute(atom)))
						return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean putAtom(EquivarianceSystem currSys, Atom lAtom, Atom rAtom) {
		Atom oldVal = currSys.mappings.put(lAtom, rAtom);
		if (oldVal == null) {
			currSys.atomList.add(lAtom);
		} else if (oldVal != rAtom) {
			logMsg(LVL_DEBUG, "Swapping conflict: " + lAtom + EquivarianceProblem.eqSeparator + oldVal + "; " + lAtom
					+ EquivarianceProblem.eqSeparator + rAtom);
			return false;
		}
		return true;
	}

	private boolean tryApplyDec(EquivarianceProblem currProblem, EquivarianceSystem currSys,
			Collection<EquivarianceSystem> systemsBranchStack) {
		if (currProblem.left instanceof FunctionApplication lAppl
				&& currProblem.right instanceof FunctionApplication rAppl && lAppl.fncSymb.equals(rAppl.fncSymb)
				&& lAppl.args.length == rAppl.args.length) {
			var fncSymb = lAppl.fncSymb;
			var argsLen = lAppl.args.length;
			if (fncSymbC.contains(fncSymb) && argsLen > 1) {
				// DecC & DecAC
				for (int i = argsLen - 1; i >= 1; i--) {
					var sysBranch = currSys.deepCopy();
					doDecC(sysBranch.problemSet, lAppl.deepCopy(), rAppl.deepCopy(), i);
					logRuleAppl("Dec-E system branch", sysBranch);
					systemsBranchStack.add(sysBranch);
				}
				doDecC(currSys.problemSet, lAppl, rAppl, 0);
			} else {
				// Dec0 & DecA
				currSys.problemSet.addAll(IntStream.range(0, argsLen)
						.mapToObj(i -> new EquivarianceProblem(lAppl.args[i], rAppl.args[i])).toList().reversed());
			}
			logRuleAppl("Dec-E", currSys);
			return true;
		}
		return false;
	}

	private void doDecC(EquivarianceProblemSequence problemSet, FunctionApplication lAppl, FunctionApplication rAppl,
			int idx) {
		EquivarianceProblem p0 = new EquivarianceProblem(lAppl.args[0], rAppl.args[idx]);
		int argsLen = lAppl.args.length;
		NominalTerm t1, t2;
		if (argsLen == 2) {
			t1 = lAppl.args[1];
			t2 = rAppl.args[1 - idx];
		} else {
			t1 = lAppl;
			t2 = rAppl;
			lAppl.args = Arrays.copyOfRange(lAppl.args, 1, argsLen);
			var rArgs = Arrays.copyOfRange(rAppl.args, 1, argsLen);
			System.arraycopy(rAppl.args, 0, rArgs, 0, idx);
			rAppl.args = rArgs;
		}
		problemSet.add(new EquivarianceProblem(t1, t2));
		problemSet.add(p0);
	}

	private boolean tryApplyAbs(EquivarianceProblem currProblem, EquivarianceSystem currSys) {
		if (currProblem.left instanceof Abstraction lAbs && currProblem.right instanceof Abstraction rAbs) {
			Atom freshAtom = atomSymb.freshTmpSymbol();
			currProblem.left = lAbs.subTerm.swap(lAbs.boundAtom, freshAtom);
			currProblem.right = rAbs.subTerm.swap(rAbs.boundAtom, freshAtom);
			currSys.problemSet.add(currProblem);
			logRuleAppl("Abs-E", currSys);
			return true;
		}
		return false;
	}

	private void logRuleAppl(String ruleName, EquivarianceSystem currSys) {
		logMsg(LVL_PROGRESS_EQ, "\n ==> ", ruleName);
		logMsg(LVL_PROGRESS_EQ, currSys);
	}
}
