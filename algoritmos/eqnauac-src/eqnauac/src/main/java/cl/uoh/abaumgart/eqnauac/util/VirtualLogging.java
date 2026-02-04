package cl.uoh.abaumgart.eqnauac.util;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public interface VirtualLogging {
	public static final LoggingConfig LOGGING_CONFIG = new LoggingConfig();

	public static final Level LVL_OFF = Level.OFF;
	public static final Level LVL_SIMPLE_AU = Level.SEVERE;
	public static final Level LVL_SIMPLE_EQ = Level.WARNING;
	public static final Level LVL_VERBOSE_AU = Level.CONFIG;
	public static final Level LVL_VERBOSE_EQ = Level.FINE;
	public static final Level LVL_PROGRESS_AU = Level.FINER;
	public static final Level LVL_PROGRESS_EQ = Level.FINEST;
	public static final Level LVL_DEBUG = new Level("DEBUG", 100) {
	};
	public static final Level LVL_ALL = Level.ALL;

	public default void logInfo(String info) {
		LOGGING_CONFIG.infoConsumer.accept(info);
	}

	public default void logWarn(String warning) {
		LOGGING_CONFIG.warnConsumer.accept(warning);
	}

	public default void logWarn(Level level, Object... warningToConcat) {
		if (level.intValue() >= LOGGING_CONFIG.logLevel.intValue())
			logWarn(Arrays.stream(warningToConcat).map(String::valueOf).collect(Collectors.joining()));
	}

	public default void logInfo(Level level, Object... infoToConcat) {
		if (level.intValue() >= LOGGING_CONFIG.logLevel.intValue())
			logInfo(Arrays.stream(infoToConcat).map(String::valueOf).collect(Collectors.joining()));
	}

	public default void logMsg(String msg) {
		LOGGING_CONFIG.msgConsumer.accept(msg);
	}

	public default void logMsg(Level level, Object... msgToConcat) {
		if (level.intValue() >= LOGGING_CONFIG.logLevel.intValue())
			logMsg(Arrays.stream(msgToConcat).map(String::valueOf).collect(Collectors.joining()));
	}

	public default void logErr(Throwable ex) {
		LOGGING_CONFIG.errConsumer.accept(ex);
	}

	public final class LoggingConfig {
		public Consumer<String> infoConsumer = System.out::println;
		public Consumer<String> msgConsumer = System.out::println;
		public Consumer<String> warnConsumer = System.err::println;
		public Consumer<Throwable> errConsumer = ex -> ex.printStackTrace(System.err);
		public Level logLevel = Level.FINE;

		private LoggingConfig() {
		}
	}
}
