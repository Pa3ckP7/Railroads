package logging;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.*;

public class ConsoleOutHandler extends Handler {

    private final Formatter formatter;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    public ConsoleOutHandler(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (closed.get()) return;
        if (!isLoggable(record)) return;
        String msg = formatter.format(record);
        if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            System.err.print(msg);
            System.err.flush();
        } else {
            System.out.print(msg);
            System.out.flush();
        }
    }

    @Override
    public void flush() {
        System.out.flush();
        System.err.flush();
    }

    @Override
    public synchronized void close() {
        closed.set(true);
        flush();
    }
}
