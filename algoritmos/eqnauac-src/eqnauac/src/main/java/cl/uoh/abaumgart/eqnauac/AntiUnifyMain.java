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
import java.util.List;

import cl.uoh.abaumgart.eqnauac.algo.AntiUnif;
import cl.uoh.abaumgart.eqnauac.algo.AntiUnifAlgorithm;
import cl.uoh.abaumgart.eqnauac.algo.AntiUnifyProblem;
import cl.uoh.abaumgart.eqnauac.algo.AntiUnifyProblemSequence;
import cl.uoh.abaumgart.eqnauac.algo.AntiUnifySystem;
import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;
import cl.uoh.abaumgart.eqnauac.data.InputParser;
import cl.uoh.abaumgart.eqnauac.data.NominalPair;
import cl.uoh.abaumgart.eqnauac.data.term.FunctionApplication;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.data.term.Variable;
import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

/**
 * Entry point for running the rule based algorithm
 * {@linkplain AntiUnifAlgorithm}
 * 
 * @author Alexander Baumgartner
 */
public class AntiUnifyMain implements VirtualLogging {

	public static void main(String[] args) {
		System.out.println("TEST RUN. Use Client to solve your own problem!\n");
		try {
			new AntiUnifyMain().test1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("TEST RUN. Use Client to solve your own problem!\n");
	}

	public void test1() throws IOException {
//		LOGGING_CONFIG.logLevel = LVL_PROGRESS_AU;
//		doAntiUnify("f[a1, a2, a3] =^= f[b1, b2, b3, b4]", "", "f", "f", "");
//		System.exit(0);
//		doAntiUnify("f(b,a,c) =^= f(c,b,a)", "", "f", "f", "");
//		doAntiUnify("f(b, a, c, a, b.b, g(a)) ≙ f(a.a, c, d, b, a, b, c, g(c))", "", "f", "f", "", false);
		LOGGING_CONFIG.logLevel = LVL_SIMPLE_AU;
		doAntiUnify("f(b, a, c, a, b.b, g(a)) =^= f(c, d, b, a, b, c, a.a, g(c))", "", "f", "f", "", true, false, 1);
//		doAntiUnify("f(f(a,b),c) =^= f(b,f(c,a))", "", "", "f", "");
//		System.exit(0);
		LOGGING_CONFIG.logLevel = LVL_SIMPLE_AU;
		doAntiUnify("f(f(a,b),c) =^= f(b,f(c,a))", "", "", "f", "", true, false, 2);
		doAntiUnify("f(a,b,a) =^= f(Y, a, (a b)Y)", "b#Y", "f", "", "");
		doAntiUnify("f(a,b) =^= f(a,b)", "", "", "", "");
		doAntiUnify("f(a,b,c) =^= f(b,c,a)", "", "f", "f", "");
		doAntiUnify("f(a,a) =^= f(b,b)", "", "", "", "");
		doAntiUnify("f(a,a) =^= f(b,c)", "", "", "", "");
		doAntiUnify("f(a,b) =^= f(b,c)", "", "", "", "");
		doAntiUnify("a.a =^= b.c", "", "", "", "");
		doAntiUnify("a.b =^= b.a", "", "", "", "");
		doAntiUnify("a.b =^= b.a", "", "", "", "c");
		doAntiUnify("f(a,b) =^= f(Y,(a b)Y)", "b#Y", "", "", "");
		doAntiUnify("f(b,a) =^= f(Y,(a b)Y)", "b#Y", "", "", "");
		doAntiUnify("h(a.a, b.b)> =^= h(c.Y, c.Y)>", "a#Y", "", "", "");
		doAntiUnify("h(a.a, b.b)> =^= h(c.Y, c.Y)>", "", "", "", "");
		doAntiUnify("h(f(X1), c.f(X1)) =^= h(g(X2), c.g(X2))", "c#X1, c#X2", "", "", "");
		doAntiUnify("h(f(X1), a.f((a c)X1)) =^= h(g(X2), b.g((b c)X2))", "a#X1,b#X1,c#X1,a#X2,b#X2,c#X2", "", "", "");
		doAntiUnify("h(f(X1), a.f((a c)X1)) =^= h(g(X2), b.g((b c)X2))", "a#X1,b#X1,c#X1,a#X2,b#X2,c#X2", "f,g,h",
				"f,g,h", "");
		doAntiUnify("h(f(X1), a.f(X2)) =^= h(g(X1), a.g(X2))", "a#X1", "", "", "");
		doAntiUnify("a.f(a,b) =^= b.f(b,a)", "", "", "", "");
		doAntiUnify("a.f(a,b) =^= b.f(b,a)", "", "", "", "c");
		doAntiUnify("a.b.c =^= c.a.b", "", "", "", "");
		doAntiUnify("a.b.b =^= b.b.a", "", "", "", "");
		doAntiUnify("f(a.b, X) =^= f(b.a, Y)", "c#X", "", "", "");
		doAntiUnify("h(f(X1), a.a.f((a c)X1)) =^= h(g(X2), c.b.g((b c)X2))", "{a#X1,b#X1,c#X1}", "", "", "");
		doAntiUnify("f(X,X,Y,Y) =^= f(Z,Z,Z,V)", "", "", "", "");
		doAntiUnify("f(X,X,Y,Y) =^= f(X,X,X,Z)", "", "", "", "");
		doAntiUnify("a =^= b", "", "", "", "");
		doAntiUnify("X =^= (a b)X", "", "", "", "");
	}

	public void doAntiUnify(String problemSetIn, String inNabla, String associative, String commutative, String extra)
			throws IOException {
		doAntiUnify(problemSetIn, inNabla, associative, commutative, extra, true, false);
	}

	public void doAntiUnify(String problemSetIn, String inNabla, String associative, String commutative, String extra,
			boolean rearrange, boolean linear) throws IOException {
		doAntiUnify(problemSetIn, inNabla, associative, commutative, extra, rearrange, linear, -1);
	}

	public void doAntiUnify(String problemSetIn, String inNabla, String associative, String commutative, String extra,
			boolean rearrange, boolean linear, int branchLimit) throws IOException {
		FunctionApplication.REARRANGE_COMMUTATIVE = rearrange;
		TermSymbols.resetAll();
		TermSymbols.addFncSymbsC(commutative);
		TermSymbols.addFncSymbsA(associative);
		TermSymbols.parseAtoms(extra);
		FreshnessCtx nabla = parseNabla(inNabla);
		AntiUnifyProblemSequence problemSet = parseEquationSystem(problemSetIn);
		List<Variable> genVars = problemSet.stream().map(AntiUnifyProblem::getGeneralizationVar).toList();
		AntiUnifySystem auSys = new AntiUnifySystem(problemSet, nabla);

		logInfo(LVL_SIMPLE_AU, "Input problem set: ", problemSet);
		logInfo(LVL_SIMPLE_AU, "Input freshness context: ", nabla);
		logInfo(LVL_SIMPLE_AU, "Input associative symbols: ", TermSymbols.toSet(TermSymbols.fncSymbA),
				", commutative symbols: ", TermSymbols.toSet(TermSymbols.fncSymbC));
		logInfo(LVL_SIMPLE_AU, "Use branching heuristics: ", rearrange);

		AntiUnif au = new AntiUnif(auSys, new AntiUnifAlgorithm(linear), branchLimit);
		long cnt = 1;
		for (AntiUnifySystem sys : au) {
			for (Variable genVar : genVars) {
				logMsg(LVL_OFF, "\nResult branch ", cnt, ": ", new NominalPair(sys.nablaOut, sys.sigma.get(genVar)));
			}
			logMsg(LVL_VERBOSE_AU, sys);
			cnt++;
		}
		if (au.lastIteratorLimitExceeded())
			logWarn("\nLimit of " + branchLimit + " branches has been exceeded!");
	}

	private AntiUnifyProblemSequence parseEquationSystem(String in) throws IOException {
		var eqSys = new AntiUnifyProblemSequence();
		new InputParser().parseEquationSystem(new StringReader(in), eqSys);
		return eqSys;
	}

	private FreshnessCtx parseNabla(String in) throws IOException {
		return new InputParser().parseNabla(new StringReader(in));
	}
}
