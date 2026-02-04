package cl.uoh.abaumgart.eqnauac;

import java.util.Objects;

import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

public class HtmlWriter extends Thread {
	private boolean error;
	private boolean compact;
	private boolean running = true;
	private HTMLDocument document;
	private HTMLElement outputRoot;
	private HTMLElement outElem;
	private String out;
	private String outStyle;
	private String oldStyle = "INIT";
	private long lineNr;
	private long maxLines;
	private RuntimeException processingException;

	public HtmlWriter(long maxLines, boolean compact) {
		this.maxLines = maxLines;
		this.compact = compact;
		document = HTMLDocument.current();
		outputRoot = document.createElement("div");
		document.getElementById("teavm-out").replaceChild(outputRoot,
				document.getElementById("teavm-out").getFirstChild());
	}

	@Override
	public void run() {
		while (running) {
			if (out != null) {
				try {
					write(out, outStyle);
				} catch (RuntimeException ex) {
					processingException = ex;
				}
				out = null;
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException ignored) {
			}
		}
	}

	private void write(String lines, String style) {
		if (!Objects.equals(oldStyle, style)) {
			outElem = document.createElement("pre");
			if (style != null)
				outElem.setAttribute("style", style);
			outputRoot.appendChild(outElem);
			oldStyle = style;
		}
		String[] lArr = lines.split("\\r?\\n");
		StringBuilder lOut = new StringBuilder(lines.length() + lArr.length * 8);
		try {
			for (String l : lArr) {
				if (!compact || !l.isBlank())
					lOut.append(prepareLine(l));
			}
		} finally {
			outElem.appendChild(document.createTextNode(lOut.toString()));
		}
	}

	private String prepareLine(String line) {
		if (lineNr == maxLines) {
			maxLines = -1;
			throw new IndexOutOfBoundsException("Maximum number of output lines reached. (You can change this limit in the input area above.)");
		}
		lineNr++;
		String lineStr = lineNr > 999999 ? String.valueOf(lineNr) : String.valueOf(lineNr + 1000000).substring(1);
		return lineStr + " " + line + "\n";
	}

	public void out(String msg) {
		out(msg, null);
	}

	public void outGray(String msg) {
		out(msg, "color:gray");
	}

	public void outRed(String msg) {
		out(msg, "color:red");
	}

	public void outYellow(String msg) {
		out(msg, "color:orange");
	}

	public void err(Throwable ex) {
		String msg = ex.getMessage();
		if (msg == null || msg.trim().isEmpty())
			msg = ex.getClass().getSimpleName();
		error = true;
		outRed(msg);
	}

	public void out(String msg, String style) {
		out = msg;
		outStyle = style;
		waitProc();
	}

	private void waitProc() {
		boolean needWait;
		do {
			needWait = out != null;
			try {
				Thread.sleep(5);
			} catch (InterruptedException ignored) {
			}
		} while (needWait);
		var exOccured = processingException;
		if (exOccured != null) {
			processingException = null;
			throw exOccured;
		}
	}

	public void shutdown() {
		try {
			writeFinished();
		} catch (Exception ignore) {
			writeFinished();
		} finally {
			running = false;
		}
	}

	private void writeFinished() {
		if (error) {
			outRed("\nERROR.");
		} else {
			outGray("\nDone.");
		}
	}
}
