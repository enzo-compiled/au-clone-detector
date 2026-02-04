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

import java.util.ArrayList;
import java.util.List;

public class AntiUnifySystemStack {
	private int branchLimit;
	private int branchCnt;
	private boolean limitExceeded;
	private List<AntiUnifySystem> systemsStack = new ArrayList<>();

	/**
	 * Use -1 to disable the limit.
	 */
	public AntiUnifySystemStack(int branchLimit) {
		this.branchLimit = branchLimit;
	}

	public boolean add(AntiUnifySystem state) {
		if (branchLimit > -1) {
			if (branchCnt >= branchLimit) {
				limitExceeded = true;
				return false;
			}
			branchCnt++;
		}
		systemsStack.add(state);
		return true;
	}

	public boolean isEmpty() {
		return systemsStack.isEmpty();
	}

	public AntiUnifySystem removeLast() {
		return systemsStack.removeLast();
	}

	public boolean isLimitExceeded() {
		return limitExceeded;
	}

	public long branchesSkipCount(long branchesRequested) {
		if (branchLimit < 0)
			return 0;
		long maxPossible = branchLimit + 1 - branchCnt;
		if (branchesRequested > maxPossible) {
			limitExceeded = true;
			return branchesRequested - maxPossible;
		}
		return 0;
	}
}
