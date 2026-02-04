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

import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;

/**
 * Some test cases
 * 
 * @author Alexander Baumgartner
 */
public class TestTermSymbols implements TermSymbols {

	public static void main(String[] args) {
		try {
			test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test() throws Exception {
		Object a1 = fncSymb.parseSymbol("a");
		Object b1 = atomSymb.parseSymbol("a");
		atomSymb.parseSymbol("b");
		atomSymb.parseSymbol("b");
		atomSymb.parseSymbol("a");
		Object a2 = fncSymb.parseSymbol("a");
		Object b2 = atomSymb.parseSymbol("a");
		System.out.println(atomSymb.getUsedSymbols());
		System.out.println(fncSymb.getUsedSymbols());
		// Should be true:
		System.out.println(a1 == a2);
		System.out.println(a1.hashCode() == a2.hashCode());
		System.out.println(a1.equals(a2));
		System.out.println(b1 == b2);
		System.out.println(b1.hashCode() == b2.hashCode());
		System.out.println(b1.equals(b2));
		// Should be false:
		System.out.println(a1 == b1);
		System.out.println(a1.hashCode() == b1.hashCode());
		System.out.println(a1.equals(b1));
	}
}
