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

import cl.uoh.abaumgart.eqnauac.algo.AntiUnifyProblemSequence;
import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;
import cl.uoh.abaumgart.eqnauac.data.InputParser;
import cl.uoh.abaumgart.eqnauac.data.term.Atom;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.util.ControlledException;

/**
 * Some test cases
 * 
 * @author Alexander Baumgartner
 */
public class TestParser implements TermSymbols {

	public static void main(String[] args) {
		try {
			test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test() throws Exception {
		parseNabla("");
		parseNabla("a#Xalhg");
		parseNabla("a#X,b#Y");
		parseNabla(" \n\t   a#\nX, b# Y ");
		parseNabla("{c#X,b#Y}");
		parseTerm("a.f(a,b,(a b)X)");
		parseTerm("  (a b)(c d)(a d)(af e)Y");
		parseTerm(" (a b)(c d)(a d)(af e)Y  ");
		parseTerm("(a b)(c d)(a d)(af e)Y");
		parseEquationSystem("a=b");
		parseEquationSystem("a=^=b");
		parseEquationSystem("a =^= \n b");
		parseEquationSystem("a=b;c =^= \n b");
		parseEquationSystem("a.f(a,b,(a b)X)=^= a.f[a,b,(a b)X], a.f(a,b,(a b)X)=a.f(a,b,(a b)X),  "
				+ "a.f(a,b,(a b)X)=a.f(a,b,(a b)X),  (a b)(c d)(a d)(af e)Y=(a b)(c d)(a d)(af e)Y, "
				+ "(a b)(c d)(a d)(af e)Y= (a b)(c d)(a d)(af e)Y");
	}

	private static void parseTerm(String nomTerm) throws Exception {
		InputParser nParse = new InputParser();
		NominalTerm np = nParse.parseTerm(new StringReader(nomTerm), true);
		np.printString(System.out);
		System.out.print(" ==> SWAP (a c) ==> ");
		Atom a1 = atomSymb.parseSymbol("a");
		Atom a2 = atomSymb.parseSymbol("c");
		np.swap(a1, a2);
		np.printString(System.out);
		System.out.println();
	}

	private static void parseNabla(String input) throws Exception {
		InputParser nParse = new InputParser();
		FreshnessCtx nabla = nParse.parseNabla(new StringReader(input));
		nabla.printString(System.out);
		System.out.print(" ==> SWAP (a c) ==> ");
		Atom a1 = atomSymb.parseSymbol("a");
		Atom a2 = atomSymb.parseSymbol("c");
		nabla.swap(a1, a2).printString(System.out);
		System.out.println();
	}

	private static void parseEquationSystem(String nomEqSysIn) throws IOException, ControlledException {
		InputParser nParse = new InputParser();
		AntiUnifyProblemSequence nomEqSysOut = new AntiUnifyProblemSequence();
		nParse.parseEquationSystem(new StringReader(nomEqSysIn), nomEqSysOut);
		nomEqSysOut.printString(System.out);
		System.out.println();
	}
}
