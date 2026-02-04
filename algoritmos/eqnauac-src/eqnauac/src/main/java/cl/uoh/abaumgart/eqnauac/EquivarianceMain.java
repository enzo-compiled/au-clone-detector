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

package cl.uoh.abaumgart.eqnauac;

import java.io.IOException;
import java.io.StringReader;

import cl.uoh.abaumgart.eqnauac.algo.Equivariance;
import cl.uoh.abaumgart.eqnauac.algo.EquivarianceAlgorithm;
import cl.uoh.abaumgart.eqnauac.algo.EquivarianceProblemSequence;
import cl.uoh.abaumgart.eqnauac.algo.EquivarianceSystem;
import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;
import cl.uoh.abaumgart.eqnauac.data.InputParser;
import cl.uoh.abaumgart.eqnauac.data.term.FunctionApplication;
import cl.uoh.abaumgart.eqnauac.data.term.Permutation;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

/**
 * Simple presentation layer for running the rule based algorithm
 * {@linkplain EquivarianceAlgorithm} in a shell.
 * 
 * @author Alexander Baumgartner
 */
public class EquivarianceMain implements VirtualLogging {

	public static void main(String[] args) {
		System.out.println("TEST RUN. Use Client to solve your own problem!\n");
		try {
			new EquivarianceMain().test1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("TEST RUN. Use Client to solve your own problem!\n");
	}

	private void test1() throws IOException {
//		LOGGING_CONFIG.logLevel = LVL_ALL;
//		doEquivariance("f(a,b,a.X) =^= f(b,c,c.X)", "a#X, b#X, c#X", "", "", "a, b, c, d");
//		System.exit(0);
		LOGGING_CONFIG.logLevel = LVL_SIMPLE_EQ;
		doEquivariance("f(a,b) = f(a,b)", "", "", "", "");
		doEquivariance("f(a.a,b) = f(a.a,a)", "", "", "", "");
		doEquivariance("f(a.a,b) = f(a,a.a)", "", "f", "f", "");
		doEquivariance("f(a,b) = f(b,a)", "", "", "", "");
		doEquivariance("f(a,a) = f(b,b)", "", "", "", "");
		doEquivariance("f(a,a) = f(b,c)", "", "", "", "");
		doEquivariance("f(a,b) = f(b,c)", "", "", "", "");
		doEquivariance("f(a,b,a.X) =^= f(b,c,c.X)", "a#X, b#X, c#X", "", "", "a, b, c, d");
		doEquivariance("a.a = b.c", "", "", "", "");
		doEquivariance("a.b = b.a", "", "", "", "");
		doEquivariance("a.b = b.a", "", "", "", "a,b,c");
		doEquivariance("h(X1, c.X1) =^= h(X1, c.X1)", "", "", "", "");
		doEquivariance("h(X1, a.(a c)X1) =^= h(X1, b.(b c)X1)", "a#X1,b#X1,c#X1", "", "", "");
		doEquivariance("h(X1, a.X2) =^= h(X1, a.X2)", "a#X1", "", "", "");
		doEquivariance("a.f(a,b) =^= b.f(b,a)", "", "", "", "");
		doEquivariance("a.f(a,b) =^= b.f(b,a)", "", "", "", "a,b,c");
		doEquivariance("a.b.c =^= c.a.b", "", "", "", "");
		doEquivariance("a.b.b =^= b.b.a", "", "", "", "");
		doEquivariance("f(a.b, X) =^= f(b.a, X)", "c#X", "", "", "");
		doEquivariance("h(X1, a.a.(a c)X1) =^= h(X1, c.b.(b c)X1)", "{a#X1,b#X1,c#X1}", "", "", "");
	}

	public void doEquivariance(String problemSetIn, String inNabla, String associative, String commutative,
			String extra) throws IOException {
		doEquivariance(problemSetIn, inNabla, associative, commutative, extra, false);
	}

	public void doEquivariance(String problemSetIn, String inNabla, String associative, String commutative,
			String extra, boolean rearrange) throws IOException {
		FunctionApplication.REARRANGE_COMMUTATIVE = rearrange;
		TermSymbols.resetAll();
		TermSymbols.addFncSymbsC(commutative);
		TermSymbols.addFncSymbsA(associative);
		TermSymbols.parseAtoms(extra);
		FreshnessCtx nabla = parseNabla(inNabla);
		EquivarianceProblemSequence problemSet = parseEquationSystem(problemSetIn);
		EquivarianceSystem eqSys = new EquivarianceSystem(problemSet, nabla);

		logInfo(LVL_SIMPLE_EQ, "Input problem set: ", problemSet);
		logInfo(LVL_SIMPLE_EQ, "Input freshness context: ", nabla);
		logInfo(LVL_SIMPLE_EQ, "Input associative symbols: ", TermSymbols.toSet(TermSymbols.fncSymbA),
				", commutative symbols: ", TermSymbols.toSet(TermSymbols.fncSymbC));

		Equivariance eq = new Equivariance(eqSys, new EquivarianceAlgorithm());
		long cnt = 1;
		for (Permutation perm : eq) {
			logMsg(LVL_OFF, "\nEquivariant. Permutation ", cnt, ": ", perm.toString(true));
			cnt++;
		}
		if (cnt == 1) {
			logMsg(LVL_OFF, "\nNot equivariant.");
		}
	}

	private EquivarianceProblemSequence parseEquationSystem(String in) throws IOException {
		var eqSys = new EquivarianceProblemSequence();
		new InputParser().parseEquationSystem(new StringReader(in), eqSys);
		return eqSys;
	}

	private FreshnessCtx parseNabla(String in) throws IOException {
		return new InputParser().parseNabla(new StringReader(in));
	}
}
