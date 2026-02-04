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

package cl.uoh.abaumgart.eqnauac.data.term;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/**
 * Interface representing the term signature. I.e., the sets of atomic term formers.
 */
public interface TermSymbols {
	/**
	 * The set of all function symbols in use
	 */
	static final SymbolSet<FunctionSymbol> fncSymb = new SymbolSet<>("?", "?", FunctionSymbol::new);
	/**
	 * The set of all variables in use
	 */
	static final SymbolSet<Variable> termVar = new SymbolSet<>("Y", "Z", Variable::new);
	/**
	 * The set of all atoms in use
	 */
	static final SymbolSet<Atom> atomSymb = new SymbolSet<>("a", "b", Atom::new);
	/**
	 * The set of associative function symbols
	 */
	static final Set<FunctionSymbol> fncSymbA = new TreeSet<>();
	/**
	 * The set of commutative function symbols
	 */
	static final Set<FunctionSymbol> fncSymbC = new TreeSet<>();

	static void addFncSymbsA(String associativeSymbols) {
		fncSymbA.addAll(parseFncSymbs(associativeSymbols));
	}

	static void addFncSymbsC(String commutativeSymbols) {
		fncSymbC.addAll(parseFncSymbs(commutativeSymbols));
	}

	static List<FunctionSymbol> parseFncSymbs(String symbols) {
		return Arrays.stream(symbols.split("[ ,;:.#(){}\\[\\]]+")).filter(Predicate.not(String::isEmpty))
				.map(e -> fncSymb.parseSymbol(e)).toList();
	}

	static List<Atom> parseAtoms(String atoms) {
		return Arrays.stream(atoms.split("[ ,;:.#(){}\\[\\]]+")).filter(Predicate.not(String::isEmpty))
				.map(e -> atomSymb.parseSymbol(e)).toList();
	}

	static String toSet(Set<FunctionSymbol> symbSet) {
		String tmp = symbSet.toString();
		return "{" + tmp.substring(1, tmp.length() - 1) + "}";
	}

	static void resetAll() {
		fncSymb.reset();
		termVar.reset();
		atomSymb.reset();
		fncSymbA.clear();
		fncSymbC.clear();
	}
}