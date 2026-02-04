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

import java.io.StringReader;

import cl.uoh.abaumgart.eqnauac.data.InputParser;
import cl.uoh.abaumgart.eqnauac.data.NominalPair;

/**
 * Some test cases
 * 
 * @author Alexander Baumgartner
 */
public class TestAlphaEquivalence {

	public static void main(String[] args) {
		// NodeFactory.PREFIX_IndividualVar = "$";
		test("a#X,b#X", "b.X", "a#X,b#X", "a.(a b)X");
		test("a#X,b#X", "b.X", "a#X,b#X", "b.(a b)X");
		test("a#X,b#X", "c.X", "a#X,b#X", "c.(a b)X");
		test("a#X,b#X", "c.X", "a#X,b#X", "d.(a b)X");
	}

	private static void test(String nablaIn1, String termIn1, String nablaIn2, String termIn2) {
		try {
			InputParser nParse = new InputParser();
			NominalPair np1 = nParse.parsePair(new StringReader(nablaIn1), new StringReader(termIn1));
			nParse.reset();
			NominalPair np2 = nParse.parsePair(new StringReader(nablaIn2), new StringReader(termIn2));
			System.out.println(np1.equals(np2) + "/" + np2.equals(np1) + " " + np1 + " / " + np2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
