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

import java.util.List;

import cl.uoh.abaumgart.eqnauac.data.term.Permutation;
import cl.uoh.abaumgart.eqnauac.util.ControlledException;

public interface EquivarianceApplicable {
	/**
	 * Applies the algorithm to the given system. The algorithm might create various
	 * branches and introduce new systems.
	 * 
	 * @return A <strong>permutation</strong>, if there exists a permutation that
	 *         solve the given equivariance problem. Otherwise,
	 *         <strong>null</strong>.
	 * @throws EquivarianceException if some inconsistencies are detected.
	 */
	Permutation apply(EquivarianceSystem currentSystem, List<EquivarianceSystem> systemsBranchStack)
			throws EquivarianceException;

	/**
	 * This {@link ControlledException} is thrown by the equivariance algorithm if
	 * some inconsistencies are detected.
	 */
	public class EquivarianceException extends ControlledException {
		static final long serialVersionUID = 1370936169201259463L;

		protected EquivarianceException(String message) {
			super(message);
		}
	}

}
