package util.logging;

import java.util.logging.*;

public class LoggerFactory {
    static {
        LogManager.getLogManager().reset();
        Logger rootLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);
        rootLogger.addHandler(consoleHandler);

    }

    public static Logger getClassLogger(Class<?> cls) {
        return Logger.getLogger(cls.getName());
    }

    public static Logger getObjectLogger(Object obj){
        return Logger.getLogger(String.format("@%s(%d)", obj.getClass().getName(), System.identityHashCode(obj)));
    }
}
