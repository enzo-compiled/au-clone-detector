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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cl.uoh.abaumgart.eqnauac.data.term.Abstraction;
import cl.uoh.abaumgart.eqnauac.data.term.Atom;
import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.data.term.Suspension;
import cl.uoh.abaumgart.eqnauac.data.term.Variable;
import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;
import cl.uoh.abaumgart.eqnauac.util.Traversable.TraverseCallBack;

/**
 * A freshness context is a set of the form {a#X, b#Y,...} where a,b,... are
 * atoms and X,Y,... are variables, stating that the symbol a is fresh in X, b is
 * fresh in Y,.... A nominal pair ({@linkplain NominalPair}) consists of a
 * freshness context and a nominal term ({@linkplain NominalTerm}).
 * 
 * @author Alexander Baumgartner
 */
public class FreshnessCtx extends Printable implements DeepCopyable<FreshnessCtx> {
	private Map<Atom, Set<Variable>> atomIdx;
	private Map<Variable, Set<Atom>> varIdx;

	public FreshnessCtx() {
		this(16, 16);
	}

	public FreshnessCtx(int sizeAtomIdx, int sizeVarIdx) {
		atomIdx = new HashMap<>(sizeAtomIdx);
		varIdx = new HashMap<>(sizeVarIdx);
	}

	public boolean contains(Atom atom, Variable var) {
		Set<Variable> freshIn = atomIdx.get(atom);
		return freshIn != null && freshIn.contains(var);
	}

	public boolean isEmpty() {
		return atomIdx.isEmpty();
	}

	public void put(Atom freshA, Collection<Variable> forVar) {
		put(freshA, forVar.toArray(new Variable[forVar.size()]));
	}

	public boolean isSubsetOf(FreshnessCtx other) {
		for (Entry<Variable, Set<Atom>> entry : varIdx.entrySet())
			for (Atom atom : entry.getValue())
				if (!other.contains(atom, entry.getKey()))
					return false;
		return true;
	}

	public Set<Variable> get(Atom fresh) {
		Set<Variable> ret = atomIdx.get(fresh);
		if (ret == null)
			return Collections.emptySet();
		return ret;
	}

	public Set<Atom> get(Variable fresh) {
		Set<Atom> ret = varIdx.get(fresh);
		if (ret == null)
			return Collections.emptySet();
		return ret;
	}

	public void put(Atom freshA, Variable... forVar) {
		Collections.addAll(createOrGet(freshA, atomIdx), forVar);
		for (Variable var : forVar)
			createOrGet(var, varIdx).add(freshA);
	}

	public void put(Variable forVar, Collection<Atom> freshA) {
		put(forVar, freshA.toArray(new Atom[freshA.size()]));
	}

	public void put(Variable forVar, Atom... freshA) {
		Collections.addAll(createOrGet(forVar, varIdx), freshA);
		for (Atom atom : freshA)
			createOrGet(atom, atomIdx).add(forVar);
	}

	private <K, V> Set<V> createOrGet(K key, Map<K, Set<V>> map) {
		Set<V> valueSet = map.get(key);
		if (valueSet == null) {
			valueSet = new HashSet<>();
			map.put(key, valueSet);
		}
		return valueSet;
	}

	public void remove(Atom freshA, Variable forVar) {
		removeIfExists(atomIdx, freshA, forVar);
		removeIfExists(varIdx, forVar, freshA);
	}

	public Set<Atom> removeAll(Variable var) {
		Set<Atom> freshIn = varIdx.remove(var);
		if (freshIn == null)
			return Collections.emptySet();
		for (Atom atom : freshIn)
			removeIfExists(atomIdx, atom, var);
		return freshIn;
	}

	public Set<Variable> removeAll(Atom atom) {
		Set<Variable> freshIn = atomIdx.remove(atom);
		if (freshIn == null)
			return Collections.emptySet();
		for (Variable var : freshIn)
			removeIfExists(varIdx, var, atom);
		return freshIn;
	}

	private <K, V> void removeIfExists(Map<K, Set<V>> map, K key, V value) {
		Set<V> vars = map.get(key);
		if (vars != null) {
			vars.remove(value);
			if (vars.isEmpty())
				map.remove(key);
		}
	}

	public Set<Atom> allAtoms() {
		return atomIdx.keySet();
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		toPrint.write(InputParser.cp_nablaStart);
		boolean sep = false;
		for (Entry<Variable, Set<Atom>> fresh : varIdx.entrySet()) {
			String var = fresh.getKey().toString();
			for (Atom atom : fresh.getValue()) {
				if (sep)
					toPrint.write(InputParser.cp_nablaSeparator);
				else
					sep = true;
				atom.printString(toPrint);
				toPrint.write(InputParser.cp_fresh);
				toPrint.write(var);
			}
		}
		toPrint.write(InputParser.cp_nablaEnd);
	}

	@Override
	public FreshnessCtx deepCopy() {
		FreshnessCtx ret = new FreshnessCtx(atomIdx.size(), varIdx.size());
		for (Entry<Atom, Set<Variable>> entry : atomIdx.entrySet())
			ret.put(entry.getKey(), entry.getValue());
		return ret;
	}

	/**
	 * Creates a new freshness context with swapped atoms. This one is not modified.
	 */
	public FreshnessCtx swap(Atom a, Atom b) {
		FreshnessCtx ret = new FreshnessCtx(atomIdx.size(), varIdx.size());
		for (Entry<Atom, Set<Variable>> entry : atomIdx.entrySet())
			ret.put(entry.getKey().swap(a, b), entry.getValue());
		return ret;
	}

	/**
	 * Add freshness constraints as needed, such that the given symbol is fresh in all
	 * the given nominal terms.
	 */
	public void addFreshConstraint(final Atom atom, NominalTerm... nomTerms) {
		TraverseCallBack<NominalTerm> callBack = new TraverseCallBack<NominalTerm>() {
			@Override
			public boolean exec(NominalTerm term) {
				if (term instanceof Suspension) {
					Suspension susp = (Suspension) term;
					put(susp.vari, susp.perm.permuteInverse(atom));
					return true;
				}
				if (term instanceof Abstraction) {
					Abstraction abs = (Abstraction) term;
					return abs.getBoundAtom() == atom;
				}
				return false;
			}
		};
		for (NominalTerm term : nomTerms)
			term.traverse(callBack);
	}

	public void substitute(Variable fromVar, NominalTerm toTerm) {
		for (Atom atom : removeAll(fromVar))
			addFreshConstraint(atom, toTerm);
	}

}
