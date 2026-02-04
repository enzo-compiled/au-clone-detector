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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;
import cl.uoh.abaumgart.eqnauac.data.InputParser;

/**
 * A {@linkplain FunctionApplication} is a {@linkplain NominalTerm} consisting
 * of a unique {@linkplain FunctionSymbol} and an array of arguments.
 * 
 * @author Alexander Baumgartner
 */
public class FunctionApplication extends NominalTerm implements TermSymbols {
	public FunctionSymbol fncSymb;
	public NominalTerm[] args;

	public static boolean REARRANGE_COMMUTATIVE = true;
	private static final NominalTerm[] emptyArgs = new NominalTerm[0];

	/**
	 * Instantiates a constant without arguments.
	 */
	private FunctionApplication(FunctionSymbol fncSymb) {
		this(fncSymb, emptyArgs);
	}

	/**
	 * Instantiates a function application with the given arguments. Use
	 * {@link #FunctionApplication(FunctionSymbol, NominalTerm[])} if possible.
	 */
	private FunctionApplication(FunctionSymbol fncSymb, Collection<NominalTerm> args) {
		this(fncSymb, args == null ? emptyArgs : args.toArray(emptyArgs));
	}

	/**
	 * Instantiates a function application with the given arguments.
	 */
	private FunctionApplication(FunctionSymbol fncSymb, NominalTerm... args) {
		if (args == null || args.length == 0)
			args = emptyArgs;
		this.fncSymb = fncSymb;
		this.args = args;
	}

	public static NominalTerm instantiateFlattenRearrange(FunctionSymbol fncSymb, Collection<NominalTerm> args) {
		return new FunctionApplication(fncSymb, args).flattenAndRearrange(REARRANGE_COMMUTATIVE);
	}

	public static NominalTerm instantiateFlattenRearrange(FunctionSymbol fncSymb, NominalTerm... args) {
		return new FunctionApplication(fncSymb, args).flattenAndRearrange(REARRANGE_COMMUTATIVE);
	}

	public static NominalTerm instantiateFlatten(FunctionSymbol fncSymb, Collection<NominalTerm> args) {
		return new FunctionApplication(fncSymb, args).flattenAndRearrange(false);
	}

	public static NominalTerm instantiateFlatten(FunctionSymbol fncSymb, NominalTerm... args) {
		return new FunctionApplication(fncSymb, args).flattenAndRearrange(false);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof FunctionApplication) {
			FunctionApplication oApp = (FunctionApplication) other;
			// Function instances are unique
			return oApp.fncSymb == fncSymb && oApp.args.equals(args);
		}
		return false;
	}

	public FunctionSymbol getFncSymb() {
		return fncSymb;
	}

	public NominalTerm[] getArgs() {
		return args;
	}

	@Override
	public void printString(Writer writer) throws IOException {
		writer.write(fncSymb.toString());
		int len = args.length;
		if (len > 0) {
			writer.write(InputParser.cp_argsStart);
			args[0].printString(writer);
			for (int i = 1; i < len; i++) {
				writer.write(InputParser.cp_argsSeparator);
				writer.write(' ');
				args[i].printString(writer);
			}
			writer.write(InputParser.cp_argsEnd);
		}
	}

	@Override
	public NominalTerm swap(Atom a1, Atom a2) {
		for (int i = args.length - 1; i >= 0; i--)
			args[i] = args[i].swap(a1, a2);
		return this;
	}

	@Override
	protected boolean propagate(TraverseCallBack<NominalTerm> callBack) {
		for (int i = 0, n = args.length; i < n; i++)
			if (args[i].traverse(callBack))
				return true;
		return false;
	}

	@Override
	public boolean isFresh(Atom atom, FreshnessCtx nabla) {
		for (NominalTerm arg : args)
			if (!arg.isFresh(atom, nabla))
				return false;
		return true;
	}

	@Override
	public FunctionApplication deepCopy() {
		if (args.length == 0)
			return new FunctionApplication(fncSymb, emptyArgs);
		NominalTerm[] tmp = Arrays.stream(args).map(NominalTerm::deepCopy).toArray(NominalTerm[]::new);
		return new FunctionApplication(fncSymb, tmp);
	}

	@Override
	public NominalTerm substitute(Variable fromVar, NominalTerm toTerm) {
		for (int i = args.length - 1; i >= 0; i--)
			args[i] = args[i].substitute(fromVar, toTerm);
		return this.flattenAndRearrange(false);
	}

	@Override
	public NominalTerm permute(Permutation perm) {
		for (int i = args.length - 1; i >= 0; i--)
			args[i] = args[i].permute(perm);
		return this;
	}

	@Override
	public Symbol getHead() {
		return fncSymb;
	}

	public NominalTerm flattenAndRearrange(boolean rearrange) {
		if (needsFlatten()) {
			if (args.length == 1)
				return args[0];
			doFlatten();
		}
		if (rearrange && needsRearrange()) {
			Arrays.sort(args);
		}
		return this;
	}

	private void doFlatten() {
		List<NominalTerm> argsFlattened = new ArrayList<>(args.length + 16);
		for (NominalTerm arg : args) {
			if (arg instanceof FunctionApplication otherAppl && fncSymb == otherAppl.fncSymb) {
				Collections.addAll(argsFlattened, otherAppl.args);
			} else {
				argsFlattened.add(arg);
			}
		}
		args = argsFlattened.toArray(NominalTerm[]::new);
	}

	private boolean needsFlatten() {
		if (!fncSymbA.contains(fncSymb))
			return false;
		if (args.length == 1)
			return true;
		for (NominalTerm arg : args) {
			if (arg instanceof FunctionApplication otherAppl && fncSymb == otherAppl.fncSymb)
				return true;
		}
		return false;
	}

	private boolean needsRearrange() {
		return args.length > 1 && fncSymbC.contains(fncSymb);
	}

	@Override
	public int getTypeOrdinal() {
		return ORD_APPL;
	}

	@Override
	public int compareTo(NominalTerm o) {
		int cmp = super.compareTo(o);
		if (cmp == 0 && o instanceof FunctionApplication oFnc) {
			for (int i = 0; cmp == 0; i++) {
				if (i == args.length)
					return i == oFnc.args.length ? 0 : -1;
				if (i == oFnc.args.length)
					return 1;
				cmp = args[i].compareTo(oFnc.args[i]);
			}
		}
		return cmp;
	}
}
