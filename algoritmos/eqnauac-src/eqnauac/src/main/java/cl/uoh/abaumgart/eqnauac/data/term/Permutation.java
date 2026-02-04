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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cl.uoh.abaumgart.eqnauac.data.FreshnessCtx;
import cl.uoh.abaumgart.eqnauac.data.InputParser;
import cl.uoh.abaumgart.eqnauac.util.DeepCopyable;
import cl.uoh.abaumgart.eqnauac.util.Printable;

/**
 * A permutation of atoms which may be composed by swappings.
 * 
 * @author Alexander Baumgartner
 */
public class Permutation extends Printable implements DeepCopyable<Permutation> {
	private Map<Atom, Atom> perm;
	private Map<Atom, Atom> invs;
	public static String idString = "Id";

	public Permutation() {
		this(10);
	}

	protected Permutation(Map<Atom, Atom> perm, Map<Atom, Atom> invs) {
		this.perm = new HashMap<>(perm);
		this.invs = new HashMap<>(invs);
	}

	public Permutation(int initSize) {
		perm = new HashMap<>(initSize);
		invs = new HashMap<>(initSize);
	}

	/**
	 * Adds a swapping at the head (left) of this sequence of swappings.
	 */
	public void addSwappingHead(Atom a1, Atom a2) {
		addSwapping(perm, invs, a1, a2);
	}

	/**
	 * Adds a swapping at the tail (right) of this sequence of swappings.
	 */
	public void addSwappingTail(Atom a1, Atom a2) {
		addSwapping(invs, perm, a1, a2);
	}

	private void addSwapping(Map<Atom, Atom> from, Map<Atom, Atom> to, Atom a, Atom b) {
		Atom c = to.get(a);
		Atom d = to.get(b);

		if (c == b) {
			from.remove(c);
			to.remove(b);
		} else if (c != null) {
			from.put(c, b);
			to.put(b, c);
		} else {
			from.put(a, b);
			to.put(b, a);
		}
		if (d == a) {
			from.remove(d);
			to.remove(a);
		} else if (d != null) {
			from.put(d, a);
			to.put(a, d);
		} else {
			from.put(b, a);
			to.put(a, b);
		}
	}

	/**
	 * Applies the permutation to the given {@linkplain Atom} and returns the
	 * permuted symbol.
	 */
	public Atom permute(Atom key) {
		Atom target = perm.get(key);
		return target == null ? key : target;
	}

	/**
	 * Applies the inverse permutation to the given {@linkplain Atom} and returns
	 * the inverse permuted symbol.
	 */
	public Atom permuteInverse(Atom key) {
		Atom target = invs.get(key);
		return target == null ? key : target;
	}

	/**
	 * The internal {@linkplain Map} representation of the permutation.
	 */
	public Map<Atom, Atom> getPerm() {
		return perm;
	}

	/**
	 * The internal {@linkplain Map} representation of the inverse permutation.
	 */
	public Map<Atom, Atom> getInverse() {
		return invs;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Permutation) {
			Permutation oPerm = (Permutation) other;
			return oPerm.perm.equals(perm);
		}
		return false;
	}

	public boolean isEmpty() {
		return perm.isEmpty();
	}

	public void clear() {
		perm.clear();
		invs.clear();
	}

	/**
	 * Returns the set of disagreements between this permutation and another one.
	 */
	public Set<Atom[]> getDisagreementSet(Permutation otherPerm, Variable var, FreshnessCtx nabla) {
		Set<Atom> composed = new HashSet<>(perm.keySet());
		composed.addAll(otherPerm.perm.keySet());
		Set<Atom[]> ds = new HashSet<>();
		for (Atom atom : composed) {
			Atom a1 = this.permute(atom);
			Atom a2 = otherPerm.permute(atom);
			if (a1 != a2 && !nabla.contains(atom, var))
				ds.add(new Atom[] { a1, a2 });
		}
		return ds;
	}

	@Override
	public Permutation deepCopy() {
		return new Permutation(perm, invs);
	}

	/**
	 * Returns a new permutation which is a composition of the two given ones.
	 */
	public static Permutation compose(Permutation permHeadLeft, Permutation permTailRight) {
		if (permHeadLeft.isEmpty())
			return permTailRight.deepCopy();
		if (permTailRight.isEmpty())
			return permHeadLeft.deepCopy();
		Permutation permComposed = permTailRight.deepCopy();
		Permutation permTmp = new Permutation(permHeadLeft.perm.size());
		for (Entry<Atom, Atom> mapping : permHeadLeft.perm.entrySet()) {
			Atom a1 = permTmp.permute(mapping.getKey());
			Atom a2 = mapping.getValue();
			if (a1 != a2) {
				permTmp.addSwappingHead(a1, a2);
				permComposed.addSwappingHead(a1, a2);
			}
		}
		return permComposed;
	}

	/**
	 * Returns the inverse permutation of this one. It uses the same data references
	 * such that changes are reflected!
	 */
	public Permutation inverse() {
		return new Permutation(invs, perm);
	}

	@Override
	public void printString(Writer toPrint) throws IOException {
		printString(toPrint, false);
	}

	public void printString(Writer toPrint, boolean useIdString) throws IOException {
		toPrint.append(toString(useIdString));
	}

	public CharSequence toString(boolean useIdString) {
		if (perm.isEmpty())
			return useIdString ? idString : "";
		Permutation permTmp = new Permutation(perm.size());
		StringBuilder sb = new StringBuilder(perm.size() * 6);
		StringBuilder sb2 = new StringBuilder();
		sb2.appendCodePoint(InputParser.cp_swapStart);
		for (Entry<Atom, Atom> mapping : perm.entrySet()) {
			Atom a1 = permTmp.permute(mapping.getKey());
			Atom a2 = mapping.getValue();
			if (a1 != a2) {
				permTmp.addSwappingHead(a1, a2);
				sb2.setLength(1);
				sb2.append(a1).append(' ').append(a2).appendCodePoint(InputParser.cp_swapEnd);
				sb.insert(0, sb2);
			}
		}
		return sb;
	}
}
