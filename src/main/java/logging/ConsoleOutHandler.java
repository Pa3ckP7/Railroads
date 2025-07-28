package logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class ConsoleOutHandler extends StreamHandler {
    public ConsoleOutHandler(Formatter formatter) {
        super(System.out, formatter);
        setLevel(Level.ALL);
    }

    @Override
    public synchronized void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public synchronized void close() {
        flush();
    }
}
