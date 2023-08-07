package di_framework.framework;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FrameworkDI {

	public static Injector run(Class<?> clazz, String... args) {
		Injector injector = new Injector();
		try {
			injector.initFramework(clazz);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
			e.printStackTrace();
		}
		return injector;
	}

	public static Logger getLogger(Class<?> clazz) {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new SimpleFormatter() {
			@Override
			public synchronized String format(LogRecord record) {
//				return String.format("%1$td %1$tb %1$tY %1$tr [%2$s] %3$s %4$s %5$s %6$s %n", record.getMillis(), record.getSourceMethodName(), record.getLevel(), record.getLoggerName(), record.getMessage(), (record.getThrown() == null ? "" : record.getThrown()));
				return String.format("%5$s %6$s %n", record.getMillis(), record.getSourceMethodName(), record.getLevel(), record.getLoggerName(), record.getMessage(), (record.getThrown() == null ? "" : record.getThrown()));
			}
		});

		Logger logger = Logger.getLogger(clazz.getName());
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
		return logger;
	}
}
