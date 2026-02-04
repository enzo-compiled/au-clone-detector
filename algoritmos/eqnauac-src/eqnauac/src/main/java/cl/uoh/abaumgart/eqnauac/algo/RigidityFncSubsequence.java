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

import java.util.function.Consumer;

import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;
import cl.uoh.abaumgart.eqnauac.util.CoordList;
import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

/**
 * Implementation for rigidity function with subsequence matching.<br>
 * Let m be the number of left term atoms and n be the number of right term
 * atoms.<br>
 * Time complexity = Exponential worst case (back tracking) but should be OK in
 * average case<br>
 * Space complexity = O(m * n)<br>
 * <br>
 * This implementation reuses all the allocated objects for performance reasons.
 * That's why the space complexity will become the quadratic maximum of all m's
 * and n's. O(max(m1, ..., mk) * max(n1, ..., nk))
 * 
 * @author Alexander Baumgartner
 */
public class RigidityFncSubsequence extends RigidityFnc {
	private int traceLenLeft = 32;
	private int traceLenRight = 32;
	private int[][] traceRoute = new int[traceLenLeft][traceLenRight];
	public static boolean DEBUG = false;

	@Override
	public Alignment compute(NominalTerm[] left, NominalTerm[] right) {
		int lenL = left.length, lenR = right.length;
		// check size of recycled memory space
		if (traceLenLeft <= lenL || traceLenRight <= lenR) {
			if (traceLenLeft <= lenL)
				traceLenLeft = lenL + 1;
			if (traceLenRight <= lenR)
				traceLenRight = lenR + 1;
			traceRoute = new int[traceLenLeft][traceLenRight];
		}
		int[][] traceRoute = this.traceRoute;
		int MAX_VALUE = Integer.MAX_VALUE;
		int MIN_VALUE = Integer.MIN_VALUE;

		// trace common subsequence length and directions
		int[] prevLenI = traceRoute[0];
		for (int i = 0; i < lenL;) {
			NominalTerm tLeft = left[i];
			i++;
			int[] currLenI = traceRoute[i];
			for (int j = 0, prevLeft = 0, currLen; j < lenR;) {
				if (tLeft.getHead() == right[j].getHead()) {
					prevLeft = (prevLenI[j] & MAX_VALUE) + 1;
					// In order to save some space,
					// we use the high bit to indicate a match
					currLen = (prevLeft | MIN_VALUE);
					j++;
				} else {
					j++;
					int prevTop = (prevLenI[j] & MAX_VALUE);
					currLen = prevLeft > prevTop ? prevLeft : prevTop;
					prevLeft = currLen;
				}
				currLenI[j] = currLen;
			}
			prevLenI = currLenI;
		}

		if (DEBUG)
			debugMatrix(left, right, VirtualLogging.LOGGING_CONFIG.msgConsumer);
		// create result via recursive back tracking
		Alignment align = new Alignment();
		backTrack(align, traceRoute, left, lenL, lenR);
		return align;
	}

	protected void debugMatrix(NominalTerm[] left, NominalTerm[] right, Consumer<String> debugOut) {
		int lenL = left.length, lenR = right.length;
		int MAX_VALUE = Integer.MAX_VALUE;
		int[][] traceRoute = this.traceRoute;
		debugOut.accept("Length matrix: (* signals a match)");
		StringBuilder out = new StringBuilder("    ");
		for (int j = 0; j < lenR; j++)
			out.append((" " + right[j].getHead() + "    ").substring(0, 5));
		debugOut.accept(out.toString());
		out.setLength(0);
		for (int i = 1; i <= lenL; i++) {
			out.append((left[i - 1].getHead() + "    ").substring(0, 4));
			for (int j = 1; j <= lenR; j++) {
				if (j > 1)
					out.append(',');

				int num = (traceRoute[i][j] & MAX_VALUE);
				if (traceRoute[i][j] < 0) {
					out.append('*');
				} else {
					out.append(' ');
				}
				if (num < 100) {
					if (num < 10)
						out.append('0');
					out.append('0');
				}
				out.append(num);
			}
			debugOut.accept(out.toString());
			out.setLength(0);
		}
	}

	protected void backTrack(Alignment align, int[][] traceLen, NominalTerm[] left, int idxL, int idxR) {
		if (traceLen[idxL][idxR] == 0)
			return;
		CoordList exitList = CoordList.obtainList();
		findExits(traceLen, idxL, idxR, exitList);
		if (!exitList.isEmpty()) {
			int[] exit = exitList.getLast();
			idxL = exit[0];
			idxR = exit[1];
			backTrack(align, traceLen, left, idxL, idxR);
			align.addAtom(idxL, idxR);
		}
		exitList.free();
	}

	private void findExits(int[][] traceLen, int idxL, int idxT, CoordList result) {
		int MAX_VALUE = Integer.MAX_VALUE;
		int len = (traceLen[idxL][idxT] & MAX_VALUE);
		int idxL1 = idxL - 1;
		int idxT1 = idxT - 1;
		boolean moveUp = true;
		CoordList branch = CoordList.obtainList();
		for (int i = 0; i >= 0;) {
			if (traceLen[idxL][idxT] < 0) {
				if (!result.contains(idxL1, idxT1))
					result.add(idxL1, idxT1);
			}
			int walkLeft = (traceLen[idxL1][idxT] & MAX_VALUE);
			int walkUp = MAX_VALUE;
			if (moveUp)
				walkUp &= traceLen[idxL][idxT1];
			if (walkLeft == len) {
				if (walkUp == len) {
					i++;
					branch.add(idxL, idxT1);
					moveUp = false;
				}
				idxL = idxL1;
				idxL1--;
			} else if (walkUp == len) {
				idxT = idxT1;
				idxT1--;
			} else {
				if (i > 0) {
					int[] nextBranch = branch.getLast();
					idxL = nextBranch[0];
					idxT = nextBranch[1];
					idxL1 = idxL - 1;
					idxT1 = idxT - 1;
					branch.removeLast();
					moveUp = true;
				}
				i--;
			}
		}
		branch.free();
	}
}
