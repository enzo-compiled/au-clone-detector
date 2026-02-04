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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Superclass for all printable classes. Printable classes have to implement a
 * method to write a string representation of themselves into an arbitrary
 * {@linkplain Writer}. The method {@link #toString()} will use this
 * representation too. This method is more flexible than the plain
 * {@linkplain #toString()} approach and it uses less system resources by
 * directly writing to the receiver.
 * 
 * @author Alexander Baumgartner
 */
public abstract class Printable {
	/**
	 * Writes a string representation of this object into an arbitrary
	 * {@linkplain Writer}.
	 */
	public abstract void printString(Writer toPrint) throws IOException;

	/**
	 * Writes a string representation of this object into an arbitrary
	 * {@linkplain PrintStream}.
	 */
	public void printString(PrintStream toPrint) throws IOException {
		PrintWriter pw = new PrintWriter(toPrint);
		printString(pw);
		pw.flush();
	}

	/**
	 * Use printString(Writer) if possible! It uses less system resources by
	 * directly writing to the receiver.
	 */
	@Override
	public String toString() {
		try {
			CharArrayWriter writer = new CharArrayWriter(1024);
			printString(writer);
			return writer.toString();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
