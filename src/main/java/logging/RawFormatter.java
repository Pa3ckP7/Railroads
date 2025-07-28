package logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RawFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getMessage()+"\n";
    }
}
