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

/**
 * Enumeration with 4 different levels for debugging.
 * <ul>
 * <li>{@linkplain DebugLevel#SILENT} = No debug output
 * <li>{@linkplain DebugLevel#SIMPLE} = Print the computation results
 * <li>{@linkplain DebugLevel#VERBOSE} = Print some additional information
 * <li>{@linkplain DebugLevel#PROGRESS} = Print all the available information
 * </ul>
 */
public enum DebugLevel {
	SILENT, SIMPLE, VERBOSE, PROGRESS;

	public static DebugLevel currentLevel = SIMPLE;

}