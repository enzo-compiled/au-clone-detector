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

import cl.uoh.abaumgart.eqnauac.data.term.Atom;
import cl.uoh.abaumgart.eqnauac.data.term.Permutation;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;

/**
 * Some test cases
 * 
 * @author Alexander Baumgartner
 */
public class TestPermutation implements TermSymbols {

	public static void main(String[] args) {
		try {
			test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test() throws Exception {
		Permutation p1 = new Permutation();
		p1.addSwappingHead(atom("a"), atom("c"));
		p1.addSwappingTail(atom("a"), atom("b"));
		System.out.println(p1);
		System.out.println(p1.permute(atom("a")).name.equals("b"));
		System.out.println(p1.permute(atom("b")).name.equals("c"));
		System.out.println(p1.permute(atom("c")).name.equals("a"));
		Permutation p = new Permutation();
		p.addSwappingTail(atom("a"), atom("c"));
		p.addSwappingTail(atom("a"), atom("b"));
		System.out.println(p1.equals(p));
		p = new Permutation();
		p.addSwappingTail(atom("a"), atom("b"));
		p.addSwappingTail(atom("b"), atom("c"));
		System.out.println(p1.equals(p));
		p = new Permutation();
		p.addSwappingHead(atom("a"), atom("b"));
		p.addSwappingTail(atom("b"), atom("c"));
		System.out.println(p1.equals(p));
		p = new Permutation();
		p.addSwappingHead(atom("a"), atom("b"));
		p.addSwappingHead(atom("a"), atom("c"));
		System.out.println(p1.equals(p));
		p = new Permutation();
		p.addSwappingTail(atom("a"), atom("b"));
		p.addSwappingHead(atom("a"), atom("c"));
		System.out.println(p1.equals(p));
		p = new Permutation();
		p.addSwappingHead(atom("b"), atom("c"));
		p.addSwappingHead(atom("b"), atom("a"));
		System.out.println(p1.equals(p));
		p = new Permutation();
		p.addSwappingTail(atom("b"), atom("c"));
		p.addSwappingHead(atom("b"), atom("a"));
		System.out.println(p1.equals(p));
		p = new Permutation();
		p.addSwappingTail(atom("b"), atom("c"));
		p.addSwappingHead(atom("b"), atom("a"));
		p.addSwappingHead(atom("b"), atom("a"));
		System.out.println(p1.equals(p));
		System.out.println(p);
	}

	private static Atom atom(String name) {
		return atomSymb.parseSymbol(name.intern());
	}

}
