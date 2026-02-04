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

package cl.uoh.abaumgart.eqnauac.util;

/**
 * Every exception that is thrown by intention is a subclass of
 * {@linkplain ControlledException}. It is an unchecked exception. By catching
 * {@linkplain ControlledException} you get all the controlled failure cases.
 */
public abstract class ControlledException extends RuntimeException {
	private static final long serialVersionUID = -7764972962627117950L;

	protected ControlledException() {
		super();
	}

	protected ControlledException(String message, Throwable cause) {
		super(message, cause);
	}

	protected ControlledException(String message) {
		super(message);
	}

	protected ControlledException(Throwable cause) {
		super(cause);
	}
}
