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

package cl.uoh.abaumgart.eqnauac.algo;

import java.util.HashSet;
import java.util.Set;

public class Alignment {
	public Set<Integer> leftAligned = new HashSet<>();
	public Set<Integer> rightAligned = new HashSet<>();

	/**
	 * Adds a new {@linkplain AlignmentAtom} to this {@linkplain Alignment}.
	 */
	public void addAtom(int idxLeft, int idxRight) {
		leftAligned.add(idxLeft);
		rightAligned.add(idxRight);
	}

}