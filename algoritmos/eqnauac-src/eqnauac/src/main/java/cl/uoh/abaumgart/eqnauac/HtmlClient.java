package cl.uoh.abaumgart.eqnauac;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

public class HtmlClient {

	public static void main(String[] args) {
//		callTest(args);
		callClient(args);
	}

	private static void callTest(String[] args) {
		// Write to JavaScript console
		System.out.println("TEST TeaVM");
	}

	private static void callClient(String[] args) {
		long maxLines = -1;
		try {
			maxLines = new BigDecimal(args[0]).toBigInteger().min(BigInteger.valueOf(Long.MAX_VALUE))
					.max(BigInteger.valueOf(Long.MIN_VALUE)).longValue();
			args = Arrays.copyOfRange(args, 1, args.length);
		} catch (Exception notAnumber) {
		}
		boolean compact = false;
		if (args.length > 0 && String.valueOf(false).equals(args[0])) {
			args = Arrays.copyOfRange(args, 1, args.length);
		} else if (args.length > 0 && String.valueOf(true).equals(args[0])) {
			compact = true;
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		var htmlWriter = new HtmlWriter(maxLines, compact);
		VirtualLogging.LOGGING_CONFIG.infoConsumer = htmlWriter::outGray;
		VirtualLogging.LOGGING_CONFIG.msgConsumer = htmlWriter::out;
		VirtualLogging.LOGGING_CONFIG.warnConsumer = htmlWriter::outYellow;
		VirtualLogging.LOGGING_CONFIG.errConsumer = htmlWriter::err;
		htmlWriter.start();
		Client.main(args);
		htmlWriter.shutdown();
	}
}
