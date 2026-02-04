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
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import cl.uoh.abaumgart.eqnauac.algo.AntiUnifyProblem;
import cl.uoh.abaumgart.eqnauac.data.InputParser.NameInfo.SymbType;
import cl.uoh.abaumgart.eqnauac.data.term.Abstraction;
import cl.uoh.abaumgart.eqnauac.data.term.Atom;
import cl.uoh.abaumgart.eqnauac.data.term.FunctionApplication;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.Permutation;
import cl.uoh.abaumgart.eqnauac.data.term.Suspension;
import cl.uoh.abaumgart.eqnauac.data.term.TermSymbols;
import cl.uoh.abaumgart.eqnauac.util.ControlledException;
import cl.uoh.abaumgart.eqnauac.util.EquationSequence;

/**
 * Sort of data starts with upper case!!! Sort of symbol starts with lower case!!!
 * 
 * @author Alexander Baumgartner
 */
public class InputParser implements TermSymbols {

	/**
	 * A separator character, Default = ','
	 */
	public static int cp_separator1 = ',';
	/**
	 * A separator character, Default = ';'
	 */
	public static int cp_separator2 = ';';
	/**
	 * A separator character, Default = '|'
	 */
	public static int cp_separator3 = '|';
	/**
	 * The opening parenthesis used to group the arguments of a function application
	 * ({@linkplain FunctionApplication}), Default = '('
	 */
	public static int cp_argsStart = '(';
	/**
	 * The character used to separate the arguments of a function application (
	 * {@linkplain FunctionApplication}), Default = {@linkplain #cp_separator1}
	 */
	public static int cp_argsSeparator = cp_separator1;
	/**
	 * The closing parenthesis used to group the arguments of a function application
	 * ({@linkplain FunctionApplication}), Default = ')'
	 */
	public static int cp_argsEnd = ')';
	/**
	 * The opening parenthesis used for swapping application, Default = '('
	 */
	public static int cp_swapStart = '(';
	/**
	 * The closing parenthesis used for swapping application, Default = ')'
	 */
	public static int cp_swapEnd = ')';
	/**
	 * The opening parenthesis used for the set of freshness constraints, Default =
	 * '{'
	 */
	public static int cp_nablaStart = '{';
	/**
	 * The character used to separate the elements of the freshness context, Default
	 * = {@linkplain #cp_separator1}
	 */
	public static int cp_nablaSeparator = cp_separator1;
	/**
	 * The closing parenthesis used for the set of freshness constraints, Default =
	 * '}'
	 */
	public static int cp_nablaEnd = '}';
	/**
	 * The character used for bound symbol abstraction, Default = '.'
	 */
	public static int cp_abstraction = '.';
	/**
	 * The character used for a freshness pair. A freshness pair consists of one
	 * symbol and one variable. See {@linkplain FreshnessCtx}. Default = '#'
	 */
	public static int cp_fresh = '#';
	/**
	 * The begin of the variable range. Default = 'a'
	 */
	public static int cp_atomFrom = 'a';
	/**
	 * The end of the variable range. Default = 'e'
	 */
	public static int cp_atomTo = 'e';
	/**
	 * The begin of the variable range. Default = 'u'
	 */
	public static int cp_varFrom = 'u';
	/**
	 * The end of the variable range. Default = 'z'
	 */
	public static int cp_varTo = 'z';
	/**
	 * The main equation suffix, also used to format the output. Default = '?'
	 */
	public static int cp_equationSuffix1 = '?';
	/**
	 * Another recognized equation suffix. Default = '!'
	 */
	public static int cp_equationSuffix2 = '!';
	/**
	 * Another recognized equation suffix. Default = '^'
	 */
	public static int cp_equationSuffix3 = '^';

	private int nextCodePoint;

	/**
	 * Instantiates a parser for nominal pairs and nominal terms.
	 * 
	 * @param factory The factory which is used to instantiate the atoms of a
	 *                nominal pair.
	 */
	public InputParser() {
		reset();
	}

	public NominalPair parsePair(Reader nablaIn, Reader termIn) throws ParseException, IOException {
		return new NominalPair(parseNabla(nablaIn), parseTerm(termIn, true));
	}

	public FreshnessCtx parseNabla(Reader in) throws IOException, ParseException {
		FreshnessCtx nabla = new FreshnessCtx();
		while (nextNameChar(in)) {
			NameInfo nameI = nextName(in);
			if (nextCodePoint != cp_fresh || !isNameChar(nextSymbol(in)))
				throw new ParseException("Malformed freshness context");
			nabla.put(createAtom(nameI), termVar.parseSymbol(nextName(in).name));
		}
		reset();
		return nabla;
	}

	private boolean nextNameChar(Reader in) throws IOException {
		while (!isNameChar(nextSymbol(in)))
			if (nextCodePoint == -1)
				return false;
		return true;
	}

	/**
	 * Pull characters from the given {@linkplain Reader} and parse the input. New
	 * equations are added to the given {@linkplain EquationSystem}.
	 */
	public <T extends Equation<NominalTerm>> void parseEquationSystem(Reader in, EquationSequence<NominalTerm, T> eqSys)
			throws IOException, ControlledException {
		do {
			NominalTerm left = parseTerm(in, false);
			if (left == null)
				return;
			parseEquationSeparator(in);
			NominalTerm right = parseTerm(in, false);
			if (right == null)
				throw new ParseException("Malformed equation system");
			eqSys.addNewEquation(left, right);
			for (; !isEqSeparatorChar(nextCodePoint); nextSymbol(in))
				if (isNameChar(nextCodePoint))
					throw new ParseException("Malformed equation system");
		} while (nextSymbol(in) != -1);
		reset();
	}

	public <T extends Equation<NominalTerm>> void parseEquation(Reader in1, Reader in2, EquationSystem<T> eqSys)
			throws ParseException, IOException {
		eqSys.newEquation(parseTerm(in1, true), parseTerm(in2, true));
	}

	public FreshnessCtx parseEquationAndCtx(Reader in1, Reader in2, Reader inN, EquationSystem<AntiUnifyProblem> eqSys)
			throws ParseException, IOException {
		parseEquation(in1, in2, eqSys);
		return parseNabla(inN);
	}

	private void parseEquationSeparator(Reader in) throws ParseException, IOException {
		while (!isEquationSign(nextCodePoint)) {
			if (nextCodePoint == -1)
				throw new ParseException("Right hand side of equation is missing");
			if (isNameChar(nextCodePoint))
				throw new ParseException("Malformed equation");
			nextSymbol(in);
		}
		do {
			nextSymbol(in);
		} while (isEquationSeparatorSuffix());
	}

	private boolean isEquationSign(int codePoint) {
		return codePoint == '≙' || codePoint == '⊑' || codePoint == '=';
	}

	/**
	 * Defaults to {@linkplain #cp_separator1} || {@linkplain #cp_separator2} ||
	 * {@linkplain #cp_separator3} || -1. Feel free to override the behavior as
	 * needed ;)
	 */
	protected boolean isEqSeparatorChar(int codePoint) {
		return codePoint == cp_separator1 || codePoint == cp_separator2 || codePoint == cp_separator3
				|| codePoint == -1;
	}

	/**
	 * Defaults to {@linkplain #cp_equationSuffix1} ||
	 * {@linkplain #cp_equationSuffix2} || {@linkplain #cp_equationSuffix3}. Feel
	 * free to override the behavior as needed ;)
	 */
	protected boolean isEquationSeparatorSuffix() {
		return nextCodePoint == cp_equationSuffix1 || nextCodePoint == cp_equationSuffix2
				|| isEquationSign(nextCodePoint) || nextCodePoint == cp_equationSuffix3;
	}

	/**
	 * Pull characters from the given {@linkplain Reader} and parse the input.
	 */
	public NominalTerm parseTerm(Reader in, boolean reset) throws ParseException, IOException {
		NominalTerm nomTerm = parseTermIntern(in, false);
		if (reset)
			reset();
		return nomTerm;
	}

	private NominalTerm parseTermIntern(Reader in, boolean mandatory) throws IOException, ParseException {
		while (!isNameChar(nextCodePoint) && !isSwapStart(nextCodePoint)) {
			if (nextSymbol(in) == -1) {
				if (mandatory)
					throw new ParseException("Malformed nominal term");
				return null;
			}
		}
		if (isSwapStart(nextCodePoint)) {
			Permutation perm = new Permutation();
			while (isSwapStart(nextCodePoint)) {
				perm.addSwappingTail(createAtom(nextName(in)), createAtom(nextName(in)));
				if (!isSwapEnd(nextCodePoint))
					throw new ParseException("Malformed swapping");
				nextSymbol(in);
			}
			return createSuspension(perm, nextName(in));
		}
		NameInfo nameInfo = nextName(in);
		switch (nameInfo.symbType) {
		case ATOM:
			if (nextCodePoint == cp_abstraction) {
				Atom oldAtom = createAtom(nameInfo);
				NominalTerm nomTerm = parseTermIntern(in, true);
				return new Abstraction(oldAtom, nomTerm);
			}
			return createAtom(nameInfo);
		case SUSP:
			return createSuspension(new Permutation(), nameInfo);
		case FNC:
			List<NominalTerm> args = null;
			if (isArgsStart(nextCodePoint) && !isArgsEnd(nextSymbol(in))) {
				args = new ArrayList<>();
				while (!isArgsEnd(nextCodePoint) && nextCodePoint != -1)
					args.add(parseTermIntern(in, true));
			}
			nextSymbol(in);
			return createFunction(nameInfo, args);
		}
		return null;
	}

	private NominalTerm createFunction(NameInfo nameI, List<NominalTerm> args) throws ParseException {
		if (nameI.symbType != SymbType.FNC)
			throw new ParseException("Invalid name for a function: " + nameI.name);
		return FunctionApplication.instantiateFlattenRearrange(fncSymb.parseSymbol(nameI.name), args);
	}

	private Atom createAtom(NameInfo nameI) throws ParseException {
		if (nameI.symbType != SymbType.ATOM)
			throw new ParseException("Invalid name for an symbol: " + nameI.name);
		return atomSymb.parseSymbol(nameI.name);
	}

	private NominalTerm createSuspension(Permutation perm, NameInfo nameI) throws ParseException {
		if (nameI.symbType != SymbType.SUSP)
			throw new ParseException("Invalid name for a suspension: " + nameI.name);
		String name = nameI.name;
		return new Suspension(termVar.parseSymbol(name), perm);
	}

	/**
	 * Tests whether the given sort name is of sort data. By default a sort is of
	 * data, if the first character of the name is a capital letter. Feel free to
	 * override the behavior as needed ;)
	 */
	protected static boolean isSortData(String sort) {
		return Character.isUpperCase(sort.codePointAt(0));
	}

	private NameInfo nextName(Reader in) throws IOException, ParseException {
		while (!isNameChar(nextCodePoint)) {
			if (nextSymbol(in) == -1)
				throw new ParseException("Unexpected end of term");
		}
		StringBuilder name = new StringBuilder();
		do {
			name.appendCodePoint(nextCodePoint);
			nextCodePoint = in.read(); // whitespace separates names
		} while (isNameChar(nextCodePoint));
		NameInfo currentName = new NameInfo(name.toString());
		while (Character.isWhitespace(nextCodePoint))
			nextSymbol(in);
		if (nextCodePoint != '(' && nextCodePoint != '[') {
			// Default = FNC
			int firstChar = Character.toLowerCase(currentName.name.codePointAt(0));
			if (firstChar >= cp_varFrom && firstChar <= cp_varTo)
				currentName.symbType = SymbType.SUSP;
			else if (firstChar >= cp_atomFrom && firstChar <= cp_atomTo)
				currentName.symbType = SymbType.ATOM;
		}
		return currentName;
	}

	/**
	 * Tests whether the given codepoint is a valid opening parenthesis to group
	 * arguments of a function application. Default = {@link #cp_argsStart} ||
	 * codePoint == '(' || codePoint == '['.
	 */
	public static boolean isArgsStart(int codePoint) {
		return codePoint == cp_argsStart || codePoint == '(' || codePoint == '[';
	}

	/**
	 * Tests whether the given codepoint is a valid closing parenthesis to group
	 * arguments of a function application. Default = {@link #cp_argsEnd} ||
	 * codePoint == ')' || codePoint == ']'.
	 */
	public static boolean isArgsEnd(int codePoint) {
		return codePoint == cp_argsEnd || codePoint == ')' || codePoint == ']';
	}

	/**
	 * Tests whether the given codepoint is a valid opening parenthesis for swapping
	 * application. Default = {@link #cp_swapStart}.
	 */
	public static boolean isSwapStart(int codePoint) {
		return codePoint == cp_swapStart;
	}

	/**
	 * Tests whether the given codepoint is a valid opening parenthesis for swapping
	 * application. Default = {@link #cp_swapEnd}.
	 */
	public static boolean isSwapEnd(int codePoint) {
		return codePoint == cp_swapEnd;
	}

	private int nextSymbol(Reader in) throws IOException {
		do {
			nextCodePoint = in.read();
		} while (Character.isWhitespace(nextCodePoint));
		return nextCodePoint;
	}

	/**
	 * Determines if the specified character (Unicode code point) may be part of a
	 * name.
	 * <p>
	 * A character may be part of a name if and only if one of the following
	 * statements is true:
	 * <ul>
	 * <li>it is a letter
	 * <li>it is a connecting punctuation character (such as <code>'_'</code>)
	 * <li>it is a digit
	 * <li>it is a numeric letter (such as a Roman numeral character)
	 * <li>it is a combining mark
	 * <li>it is a non-spacing mark
	 * <li>it is one of the symbols <code>'+','-','*','/','_','@'</code>
	 * <li><code>isIdentifierIgnorable</code> returns <code>true</code> for this
	 * character.
	 * </ul>
	 * 
	 * @param codePoint the character (Unicode code point) to be tested.
	 * @return <code>true</code> if the character may be part of a Unicode
	 *         identifier; <code>false</code> otherwise.
	 */
	public static boolean isNameChar(int codePoint) {
		return Character.isUnicodeIdentifierPart(codePoint) || codePoint == '+' || codePoint == '-' || codePoint == '*'
				|| codePoint == '/' || codePoint == '_' || codePoint == '@';
	}

	/**
	 * This {@link ControlledException} is thrown by the {@link InputParser} if the
	 * input is malformed.
	 * 
	 * @author Alexander Baumgartner
	 */
	public class ParseException extends ControlledException {
		static final long serialVersionUID = 1370936169201259463L;

		ParseException(String message) {
			super(message);
		}
	}

	/**
	 * An auxiliary structure used by the {@link InputParser}.
	 * 
	 * @author Alexander Baumgartner
	 */
	static class NameInfo {
		String name;

		enum SymbType {
			ATOM, FNC, SUSP
		};

		SymbType symbType = SymbType.FNC;

		NameInfo(String name) {
			this.name = name;
		}
	}

	/**
	 * Resets the state of the parser.
	 */
	public void reset() {
		nextCodePoint = -2;
	}
}
