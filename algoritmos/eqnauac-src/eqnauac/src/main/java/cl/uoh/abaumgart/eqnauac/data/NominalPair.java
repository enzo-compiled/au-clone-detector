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

package cl.uoh.abaumgart.eqnauac.data;

import java.io.IOException;
import java.io.Writer;

import cl.uoh.abaumgart.eqnauac.data.term.Atom;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.Variable;
import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;

/**
 * A nominal pair is a pair consisting of one freshness context (
 * {@linkplain FreshnessCtx}) and one nominal term ({@linkplain NominalTerm}).
 * The freshness context is often referred as nabla.
 * 
 * @author Alexander Baumgartner
 */
public class NominalPair extends Printable implements DeepCopyable<NominalPair> {
	private FreshnessCtx freshEnv;
	private NominalTerm term;
	/**
	 * The opening parenthesis used to group a nominal pair, Default = '&lt;'
	 */
	public static String pairStart = "<";
	/**
	 * The character used to separate the two parts of a nominal pair, Default = ';
	 * '
	 */
	public static String pairSeparator = "; ";
	/**
	 * The closing parenthesis used to group a nominal pair, Default = '&gt;'
	 */
	public static String pairEnd = ">";

	/**
	 * Instantiates a nominal pair with an empty freshness context.
	 * 
	 * @see #NominalPair(FreshnessCtx, NominalTerm)
	 */
	public NominalPair(NominalTerm term) {
		this(new FreshnessCtx(), term);
	}

	/**
	 * Instantiates a nominal pair with then given freshness context and nominal
	 * term.
	 */
	public NominalPair(FreshnessCtx freshEnv, NominalTerm term) {
		this.freshEnv = freshEnv;
		this.term = term;
	}

	/**
	 * Returns the freshness context ({@linkplain FreshnessCtx}) of this nominal
	 * pair.
	 */
	public FreshnessCtx getFreshEnv() {
		return freshEnv;
	}

	/**
	 * Changes the freshness context ({@linkplain FreshnessCtx}) of this nominal
	 * pair.
	 */
	public void setFreshEnv(FreshnessCtx freshEnv) {
		this.freshEnv = freshEnv;
	}

	/**
	 * Returns the nominal term ({@linkplain NominalTerm}) of this nominal pair.
	 */
	public NominalTerm getTerm() {
		return term;
	}

	/**
	 * Changes the nominal term ({@linkplain NominalTerm}) of this nominal pair.
	 */
	public void setTerm(NominalTerm term) {
		this.term = term;
	}

	/**
	 * Applies a swapping to this nominal pair. The swapping is applied to both, the
	 * term and the freshness context.
	 */
	public void swap(Atom a1, Atom a2) {
		freshEnv = freshEnv.swap(a1, a2);
		term = term.swap(a1, a2);
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		printPair(toPrint, freshEnv, term);
	}

	public static void printPair(Writer toPrint, FreshnessCtx freshEnv, NominalTerm term) throws IOException {
		toPrint.write(pairStart);
		freshEnv.printString(toPrint);
		toPrint.write(pairSeparator);
		toPrint.write(' ');
		term.printString(toPrint);
		toPrint.write(pairEnd);
	}

	@Override
	public NominalPair deepCopy() {
		return new NominalPair(freshEnv.deepCopy(), term.deepCopy());
	}

	/**
	 * Tests whether the given symbol is fresh in the nominal term respecting the
	 * freshness context.
	 */
	public boolean prooveFresh(Atom atom) {
		return term.isFresh(atom, freshEnv);
	}

	/**
	 * Applies a substitution to the nominal pair.
	 */
	public void substitute(Variable fromVar, NominalTerm toTerm) {
		freshEnv.substitute(fromVar, toTerm);
		term = term.substitute(fromVar, toTerm);
	}
}
