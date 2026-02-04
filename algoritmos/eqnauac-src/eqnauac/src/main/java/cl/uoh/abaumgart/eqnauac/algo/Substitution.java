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

/*
 * Copyright 2012 Alexander Baumgartner
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

package cl.uoh.abaumgart.eqnauac.algo;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;
import cl.uoh.abaumgart.eqnauac.data.NominalPair;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.Variable;
import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;

/**
 * This class represents a substitution, which is a mapping from variables to
 * nominal terms.<br>
 * It is used inside the rule based system {@linkplain AntiUnifyAlgoOLD} to
 * compute generalizations for given {@linkplain AntiUnifyProblem}s. In this
 * case, the left hand side of the mapping is the generalization variable of an
 * anti-unification problem (AUP) and the right hand side is the generalization
 * computed so far.
 * 
 * @author Alexander Baumgartner
 */
public class Substitution extends Printable implements DeepCopyable<Substitution> {
	private Map<Variable, NominalTerm> mapping = new HashMap<>();
	/**
	 * Default = "{"
	 */
	public static String SIGMA_START = "{";
	/**
	 * Default = " -> "
	 */
	public static String SIGMA_MAPTO = " -> ";
	/**
	 * Default = "} "
	 */
	public static String SIGMA_END = "} ";
	/**
	 * Default = "; "
	 */
	public static String RAN_PRINT_SEPARATOR = "; ";

	/**
	 * Add a new mapping of the form: Variable -&gt; Term.
	 */
	public void put(Variable fromVar, NominalTerm toTerm) {
		mapping.put(fromVar, toTerm);
	}

	/**
	 * Substitution composition which does not add new variables to the mapping.
	 * (This is useful for generalization computation of AUPs.)
	 */
	public void composeInRange(Variable fromVar, NominalTerm toTerm) {
		for (Entry<Variable, NominalTerm> e : mapping.entrySet())
			e.setValue(e.getValue().substitute(fromVar, toTerm));
	}

	/**
	 * Returns the associated {@linkplain NominalTerm} for a given
	 * {@linkplain Variable} or null if no mapping exists.
	 */
	public NominalTerm get(Variable var) {
		return mapping.get(var);
	}

	/**
	 * Removes all the mappings from a substitution.
	 */
	public void clear() {
		mapping.clear();
	}

	@Override
	public Substitution deepCopy() {
		Substitution cpy = new Substitution();
		for (Entry<Variable, NominalTerm> e : mapping.entrySet())
			cpy.mapping.put(e.getKey(), e.getValue().deepCopy());
		return cpy;
	}

	public Map<Variable, NominalTerm> getMapping() {
		return mapping;
	}

	/**
	 * Only prints the range of the mapping, which is actually useful to display a
	 * computed generalization of an AUP without the generalization variable.
	 */
	public void printRanString(Writer writer, FreshnessCtx nabla) throws IOException {
		boolean first = true;
		for (Entry<Variable, NominalTerm> e : mapping.entrySet()) {
			if (first)
				first = false;
			else
				writer.write(RAN_PRINT_SEPARATOR);
			NominalPair.printPair(writer, nabla, e.getValue());
		}
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		for (Entry<Variable, NominalTerm> e : mapping.entrySet()) {
			toPrint.append(SIGMA_START);
			e.getKey().printString(toPrint);
			toPrint.append(SIGMA_MAPTO);
			e.getValue().printString(toPrint);
			toPrint.append(SIGMA_END);
		}
	}
}