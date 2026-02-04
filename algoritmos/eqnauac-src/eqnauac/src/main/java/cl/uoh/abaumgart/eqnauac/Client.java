package cl.uoh.abaumgart.eqnauac;

import java.util.logging.Level;

import cl.uoh.abaumgart.eqnauac.util.VirtualLogging;

public class Client implements VirtualLogging {
	public Client(String[] args) {
		try {
			if (args.length < 3 || isHelpRequest(args[0])) {
				printHelp();
				return;
			}
			LOGGING_CONFIG.logLevel = parseLogLevel(args[0], args[1]);
			var fresh = "";
			var assoc = "";
			var commu = "";
			var extra = "";
			var rearrange = true;
			var linear = false;
			var branchLimit = -1;
			if (args.length >= 4)
				fresh = args[3];
			if (args.length >= 6) {
				assoc = args[4];
				commu = args[5];
				if (args.length > 6)
					extra = args[6];
				if (args.length > 7)
					rearrange = booleanArg(args[7]);
				if (args.length > 8)
					linear = booleanArg(args[8]);
				if (args.length > 9)
					branchLimit = Integer.parseInt(args[9]);
			} else if (args.length == 5)
				extra = args[4];
			switch (args[0]) {
			case "AU" ->
				new AntiUnifyMain().doAntiUnify(args[2], fresh, assoc, commu, extra, rearrange, linear, branchLimit);
			case "EQ" -> new EquivarianceMain().doEquivariance(args[2], fresh, assoc, commu, extra, rearrange);
			default -> throw new IllegalArgumentException("First argument is invalid. \"AU\" or \"EQ\" expected.");
			}
		} catch (Exception ex) {
			try {
				logErr(ex);
			} catch (Exception logEx) {
				ex.printStackTrace();
				logEx.printStackTrace();
				logErr(logEx);
			}
		}
	}

	private boolean booleanArg(String arg) {
		char firstC = Character.toLowerCase(arg.charAt(0));
		return firstC == 't' || firstC == 'y';
	}

	private boolean isHelpRequest(String msg) {
		var msgLC = msg == null ? "?" : msg.toLowerCase().replace("-", "");
		return "?".equals(msgLC) || "h".equals(msgLC) || "help".equals(msgLC);
	}

	private Level parseLogLevel(String fnc, String level) {
		return switch (level) {
		case "SILENT" -> LVL_OFF;
		case "SIMPLE" -> "AU".equals(fnc) ? LVL_SIMPLE_AU : LVL_SIMPLE_EQ;
		case "VERBOSE" -> "AU".equals(fnc) ? LVL_VERBOSE_AU : LVL_VERBOSE_EQ;
		case "PROGRESS" -> "AU".equals(fnc) ? LVL_PROGRESS_AU : LVL_PROGRESS_EQ;
		case "ALL" -> LVL_ALL;
		default -> throw new IllegalArgumentException("Invalid argument " + level + ". Log-Level expected.");
		};
	}

	private void printHelp() {
		logInfo("");
		logInfo("USAGE: java -jar eqnauac-lib.jar arg_1 ... arg_n");
		logInfo("");
		logInfo(" arg_1 : specifies the algorithm. There are 3 algorithms available:");
		logInfo("         AU = anti-unification solver");
		logInfo("         EQ = equivariance solver");
		logInfo(" arg_2 : specifies the log-level. There are 5 levels available:");
		logInfo("         SIMPLE   = the results are displayed");
		logInfo("         VERBOSE  = some additional informations are displayed");
		logInfo("         PROGRESS = rule applications are displayed");
		logInfo("         ALL      = all debug informations are displayed");
		logInfo("         SILENT   = no output in the case of success. Errors are displayed.");
		logInfo("                    Useful to embed the library instead of executing the jar file");
		logInfo(" arg_3 : the problem set");
		logInfo("[arg_4]: the freshness context");
		logInfo("[arg_5]: associative function symbols");
		logInfo("[arg_6]: commutative function symbols");
		logInfo("[arg_7]: extra atoms (arg_5 if associative AND commutative symbols are missing)");
		logInfo("[arg_8]: align commutative arguments (default=true)");
		logInfo("[arg_9]: compute linear generalizations (default=false)");
		logInfo("[arg_10]: branch limit (default=-1)");
		logInfo("");
		logInfo("Some examples:");
		logInfo("java -jar eqnauac-lib.jar AU SIMPLE \"f(a,b,a) =^= f(Y, a, (a b)Y)\" \"b#Y\"");
		logInfo("java -jar eqnauac-lib.jar AU SIMPLE \"f(f(a,b),a) =^= f(b,f(b,a))\" \"\" \"f\" \"f\"");
		logInfo("java -jar eqnauac-lib.jar AU VERBOSE \"f(a,b,a) =^= f(Y, a, (a b)Y)\" \"b#Y\" \"\" \"f\"");
		logInfo("java -jar eqnauac-lib.jar AU PROGRESS \"a.b =^= b.a\" \"\" \"c,d\"");
		logInfo("java -jar eqnauac-lib.jar AU PROGRESS \"f(a.b, c) =^= f(c, b.a)\" \"\" \"f\" \"f\" \"d,e\"");
		logInfo("java -jar eqnauac-lib.jar EQ PROGRESS \"f(a,b) =< f(b,a)\"");
		logInfo("java -jar eqnauac-lib.jar EQ SIMPLE \"f(X,f(b,c)) =< f((a,b)X,f(a,d))\" \"c#X, d#X\"");
		logInfo("");
		logInfo("arg_5 and arg_6 may be omitted TOGETHER. It's not allowed to only omit one of them.");
		logInfo("");
	}

	public static void main(String[] args) {
		new Client(args);
	}
}
