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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import cl.uoh.abaumgart.eqnauac.data.term.Abstraction;
import cl.uoh.abaumgart.eqnauac.data.term.Atom;
import cl.uoh.abaumgart.eqnauac.data.term.FunctionApplication;
import cl.uoh.abaumgart.eqnauac.data.term.FunctionSymbol;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.Permutation;
import cl.uoh.abaumgart.eqnauac.data.term.Suspension;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.data.term.Variable;
import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

public class AntiUnifAlgorithm implements AntiUnifApplicable, TermSymbols, VirtualLogging {
	public RigidityFnc rFnc = new RigidityFncSubsequence();
	public EquivarianceApplicable eqvm = new EquivarianceAlgorithm();
	private Collection<Atom> atoms = TermSymbols.atomSymb.getUsedSymbols();
	boolean linear;

	public AntiUnifAlgorithm() {
		this(false);
	}

	public AntiUnifAlgorithm(boolean linear) {
		this.linear = linear;
	}

	@Override
	public AntiUnifySystem apply(AntiUnifySystem currentSystem, AntiUnifySystemStack branchStack) {
		solve(currentSystem, branchStack);
		if (!linear)
			merge(currentSystem, branchStack);
		return currentSystem;
	}

	private void solve(AntiUnifySystem currSys, AntiUnifySystemStack branchStack) {
		while (!currSys.problemSet.isEmpty()) {
			int idx = 0;
			if (!tryApplyAtm(currSys, idx) && !tryApplyDec(currSys, idx, branchStack) && !tryApplyAbs(currSys, idx))
				applySol(currSys, idx);
		}
	}

	private void merge(AntiUnifySystem currSys, AntiUnifySystemStack branchStack) {
		// Mer
		logMsg(LVL_PROGRESS_AU, "\nStart merging phase");
		long merCnt = 0;
		for (int i = currSys.store.size() - 1; i >= 1; i--) {
			var aup1 = currSys.store.get(i);
			for (int j = i - 1; j >= 0; j--) {
				var aup2 = currSys.store.get(j);
				EquivarianceProblemSequence problemSet = new EquivarianceProblemSequence();
				problemSet.addNewEquation(aup1.left, aup2.left);
				problemSet.addNewEquation(aup1.right, aup2.right);
				EquivarianceSystem eqSys = new EquivarianceSystem(problemSet, currSys.nablaIn);
				Iterator<Permutation> perms = new Equivariance(eqSys, eqvm).iterator();
				if (perms.hasNext()) {
					var susp = new Suspension(aup1.generalizationVar, perms.next());
					currSys.sigma.composeInRange(aup2.generalizationVar, susp);
					currSys.nablaOut.substitute(aup2.generalizationVar, susp);
					currSys.store.remove(j, false);
					i--;
					logRuleAppl("Mer", currSys);
					merCnt++;
				}
			}
		}
		logMsg(LVL_VERBOSE_AU, "\nMerge rule applied " + merCnt + " times ==>");
	}

	private boolean tryApplyAtm(AntiUnifySystem currSys, int aupIdx) {
		var currAup = currSys.problemSet.get(aupIdx);
		if (currAup.left == currAup.right) {
			currSys.sigma.composeInRange(currAup.generalizationVar, currAup.left);
			currSys.problemSet.remove(aupIdx, true);
			logRuleAppl("Dec0", currSys);
			return true;
		}
		return false;
	}

	private boolean tryApplyDec(AntiUnifySystem currSys, int aupIdx, AntiUnifySystemStack branchStack) {
		var currAup = currSys.problemSet.get(aupIdx);
		if (currAup.left instanceof FunctionApplication lFnc && currAup.right instanceof FunctionApplication rFnc
				&& lFnc.fncSymb == rFnc.fncSymb) {
			var fncSymb = lFnc.fncSymb;
			var lArgs = lFnc.args;
			var rArgs = rFnc.args;
			if (fncSymbA.contains(fncSymb)) {
				if (lArgs.length < 2 || rArgs.length < 2)
					throw new AntiUnifException(
							"Associative function application must be of arity 2. Symbol=" + fncSymb);
				if (fncSymbC.contains(fncSymb)) {
					applyDecAC(currSys, aupIdx, fncSymb, lArgs, rArgs, branchStack);
					logRuleAppl("DecAC current branch (position 1)", currSys);
				} else {
					applyDecA(currSys, aupIdx, fncSymb, lArgs, rArgs, branchStack);
					logRuleAppl("DecA current branch (position 1)", currSys);
				}
			} else if (fncSymbC.contains(fncSymb)) {
				if (lArgs.length != 2 || rArgs.length != 2)
					throw new AntiUnifException(
							"Commutative function application must be of arity 2. Symbol=" + fncSymb);
				applyDecC(currSys, aupIdx, fncSymb, lArgs, rArgs, branchStack);
				logRuleAppl("DecC current branch (position 1)", currSys);
			} else {
				if (lArgs.length != rArgs.length)
					throw new AntiUnifException("Contradicting arity of function application. Symbol=" + fncSymb);
				applyDec0(currSys, aupIdx, fncSymb, lArgs, rArgs);
				logRuleAppl("Dec0", currSys);
			}
			return true;
		}
		return false;
	}

	private void applyDecAC(AntiUnifySystem currSys, int aupIdx, FunctionSymbol fncSymb, NominalTerm[] lArgs,
			NominalTerm[] rArgs, AntiUnifySystemStack branchStack) {
		var currAup = currSys.problemSet.get(aupIdx);
		var newAup = prepareDecBranch(currSys, aupIdx, currAup, fncSymb, lArgs, rArgs,
				FunctionApplication.REARRANGE_COMMUTATIVE);
		ACDecomposer lDecIter = new ACDecomposer(lArgs);
		ACDecomposer rDecIter = new ACDecomposer(rArgs);
		long cnt = ((1L << (lArgs.length - 1)) - 1) * ((1L << (rArgs.length - 1)) - 1) * 2L;
		long skip = branchStack.branchesSkipCount(cnt);
		if (cnt < 0 || cnt > 1000000)
			logWarn("\nMore than one million branches are being generated by DecAC!");
		for (var lDec : lDecIter) {
			currAup.left = FunctionApplication.instantiateFlatten(fncSymb, lDec.bucket0);
			newAup.left = FunctionApplication.instantiateFlatten(fncSymb, lDec.bucket1);
			for (var rDec : rDecIter) {
				if (skip >= 2) {
					skip -= 2;
					cnt -= 2;
					continue;
				}
				currAup.right = FunctionApplication.instantiateFlatten(fncSymb, rDec.bucket1);
				newAup.right = FunctionApplication.instantiateFlatten(fncSymb, rDec.bucket0);
				if (skip >= 1) {
					--skip;
				} else {
					performDecBranch("DecAC new branch (position " + cnt + ")", currAup, newAup, cnt == 1, currSys,
							branchStack);
				}
				--cnt;
				var tmpTerm = currAup.right;
				currAup.right = newAup.right;
				newAup.right = tmpTerm;
				performDecBranch("DecAC new branch (position " + cnt + ")", currAup, newAup, cnt == 1, currSys,
						branchStack);
				--cnt;
			}
		}
		if (cnt != 0)
			throw new IllegalStateException("Internal Error in branch iteration. This is a bug! "
					+ "We kindly ask you to report it and attach the input that caused this bug.");
	}

	private void applyDecA(AntiUnifySystem currSys, int aupIdx, FunctionSymbol fncSymb, NominalTerm[] lArgs,
			NominalTerm[] rArgs, AntiUnifySystemStack branchStack) {
		var currAup = currSys.problemSet.get(aupIdx);
		var newAup = prepareDecBranch(currSys, aupIdx, currAup, fncSymb, lArgs, rArgs, false);
		long cnt = (lArgs.length - 1) * (rArgs.length - 1);
		long skip = branchStack.branchesSkipCount(cnt);
		if (cnt < 0 || cnt > 1000000)
			logWarn("\nMore than one million branches are being generated by DecA!");
		for (int i = lArgs.length - 1; i >= 1; i--) {
			currAup.left = FunctionApplication.instantiateFlatten(fncSymb, Arrays.copyOfRange(lArgs, 0, i));
			newAup.left = FunctionApplication.instantiateFlatten(fncSymb, Arrays.copyOfRange(lArgs, i, lArgs.length));
			for (int j = rArgs.length - 1; j >= 1; j--) {
				if (skip >= 1) {
					--skip;
				} else {
					currAup.right = FunctionApplication.instantiateFlatten(fncSymb, Arrays.copyOfRange(rArgs, 0, j));
					newAup.right = FunctionApplication.instantiateFlatten(fncSymb,
							Arrays.copyOfRange(rArgs, j, rArgs.length));
					performDecBranch("DecA new branch (position " + cnt + ")", currAup, newAup, cnt == 1, currSys,
							branchStack);
				}
				--cnt;
			}
		}
		if (cnt != 0)
			throw new IllegalStateException("Internal Error in branch iteration. This is a bug! "
					+ "We kindly ask you to report it and attach the input that caused this bug.");
	}

	private void applyDecC(AntiUnifySystem currSys, int aupIdx, FunctionSymbol fncSymb, NominalTerm[] lArgs,
			NominalTerm[] rArgs, AntiUnifySystemStack branchStack) {
		var currAup = currSys.problemSet.get(aupIdx);
		var newAup = prepareDecBranch(currSys, aupIdx, currAup, fncSymb, lArgs, rArgs,
				FunctionApplication.REARRANGE_COMMUTATIVE);
		currAup.set(lArgs[0], rArgs[1]);
		newAup.set(lArgs[1], rArgs[0]);
		currSys.problemSet.add(currAup);
		currSys.problemSet.add(newAup);
		if (!branchStack.isLimitExceeded() && branchStack.add(currSys.deepCopy()))
			logRuleAppl("DecC new branch (position 2)", currSys);
		currAup.right = rArgs[0];
		newAup.right = rArgs[1];
	}

	private void applyDec0(AntiUnifySystem currSys, int aupIdx, FunctionSymbol fncSymb, NominalTerm[] lArgs,
			NominalTerm[] rArgs) {
		var currAup = currSys.problemSet.get(aupIdx);
		if (lArgs.length == 0) {
			currSys.sigma.composeInRange(currAup.generalizationVar, FunctionApplication.instantiateFlatten(fncSymb));
			currSys.problemSet.remove(aupIdx, true);
		} else {
			Variable[] fresh = termVar.fresh2Symbols(lArgs.length);
			var susps = Arrays.stream(fresh).map(Suspension::new).toArray(Suspension[]::new);
			currSys.sigma.composeInRange(currAup.generalizationVar,
					FunctionApplication.instantiateFlatten(fncSymb, susps));
			currAup.set(fresh[0], lArgs[0], rArgs[0]);
			for (int i = 1; i < fresh.length; i++) {
				var newAup = new AntiUnifyProblem(lArgs[i], rArgs[i], fresh[i]);
				currSys.problemSet.add(newAup);
			}
		}
	}

	private AntiUnifyProblem prepareDecBranch(AntiUnifySystem currSys, int aupIdx, AntiUnifyProblem currAup,
			FunctionSymbol fncSymb, NominalTerm[] lArgs, NominalTerm[] rArgs, boolean rearrange) {
		var susp1 = new Suspension(termVar.fresh2Symbol());
		var susp2 = new Suspension(termVar.fresh2Symbol());
		currSys.sigma.composeInRange(currAup.generalizationVar,
				FunctionApplication.instantiateFlatten(fncSymb, susp1, susp2));
		currSys.problemSet.remove(aupIdx, true);
		currAup.generalizationVar = susp1.vari;
		if (rearrange) {
			Arrays.sort(lArgs);
			Arrays.sort(rArgs);
			Alignment a = rFnc.compute(lArgs, rArgs);
			doRearrange(lArgs, a.leftAligned);
			doRearrange(rArgs, a.rightAligned);
			logMsg(LVL_DEBUG, Arrays.toString(lArgs) + " / " + Arrays.toString(rArgs));
		}
		return new AntiUnifyProblem(NominalTerm.DUMMY, NominalTerm.DUMMY, susp2.vari);
	}

	private void doRearrange(NominalTerm[] args, Set<Integer> arranged) {
		ArrayList<NominalTerm> tmp = new ArrayList<>();
		int j = 0;
		for (int i = 0; i < args.length; i++) {
			if (arranged.contains(i)) {
				args[j] = args[i];
				j++;
			} else {
				tmp.add(args[i]);
			}
		}
		for (int start = j; j < args.length; j++) {
			args[j] = tmp.get(j - start);
		}
	}

	private void performDecBranch(String ruleName, AntiUnifyProblem currAup, AntiUnifyProblem newAup, boolean isLast,
			AntiUnifySystem currSys, AntiUnifySystemStack branchStack) {
		if (isLast) {
			currSys.problemSet.add(currAup);
			currSys.problemSet.add(newAup);
		} else {
			AntiUnifySystem copySys = currSys.deepCopy();
			branchStack.add(copySys);
			copySys.problemSet.add(currAup.deepCopy());
			copySys.problemSet.add(newAup.deepCopy());
			logRuleAppl(ruleName, copySys);
		}
	}

	private boolean tryApplyAbs(AntiUnifySystem currSys, int aupIdx) {
		var currAup = currSys.problemSet.get(aupIdx);
		if (currAup.left instanceof Abstraction lAbs && currAup.right instanceof Abstraction rAbs) {
			if (lAbs.boundAtom == rAbs.boundAtom) {
				currSys.sigma.composeInRange(currAup.generalizationVar,
						new Abstraction(lAbs.boundAtom, new Suspension(currAup.generalizationVar)));
				currAup.left = lAbs.subTerm;
				currAup.right = rAbs.subTerm;
			} else {
				Atom c = atoms.stream()
						.filter(atom -> lAbs.isFresh(atom, currSys.nablaIn) && rAbs.isFresh(atom, currSys.nablaIn))
						.findFirst().orElse(null);
				if (c == null)
					return false;
				currSys.sigma.composeInRange(currAup.generalizationVar,
						new Abstraction(c, new Suspension(currAup.generalizationVar)));
				currAup.left = lAbs.subTerm.swap(c, lAbs.boundAtom);
				currAup.right = rAbs.subTerm.swap(c, rAbs.boundAtom);
			}
			logRuleAppl("Abs", currSys);
			return true;
		}
		return false;
	}

	private void applySol(AntiUnifySystem currSys, int idx) {
		AntiUnifyProblem currAup = currSys.problemSet.remove(idx, true);
		for (Atom atom : atoms) {
			if (currAup.left.isFresh(atom, currSys.nablaIn) && currAup.right.isFresh(atom, currSys.nablaIn))
				currSys.nablaOut.put(atom, currAup.generalizationVar);
		}
		currSys.store.add(currAup);
		logRuleAppl("Sol", currSys);
	}

	private void logRuleAppl(String ruleName, AntiUnifySystem currSys) {
		logMsg(LVL_PROGRESS_AU, "\n ==> ", ruleName);
		logMsg(LVL_PROGRESS_AU, currSys);
	}
}
