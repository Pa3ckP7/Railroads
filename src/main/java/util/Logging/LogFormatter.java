package util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private static DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")
            .withZone(ZoneId.systemDefault());

    @Override
    public String format(LogRecord record) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("[%s] ", record.getLevel().getName()));
        logMessage.append(String.format("[%s] ", ISO_DATE.format(Instant.ofEpochMilli(record.getMillis()))));
        logMessage.append(String.format("[%s] ", getThreadName(record.getLongThreadID())));
        logMessage.append(String.format("[%s]> ", record.getLoggerName()));
        logMessage.append(String.format("%s ", record.getMessage()));
        logMessage.append(System.lineSeparator());

        if (record.getParameters() != null) {
            logMessage.append(" | Params: ");
            for (Object param : record.getParameters()) {
                if (param instanceof Loggable details){
                    logMessage.append(String.format("Extra details: %s", details.LogDetails()));
                    logMessage.append(System.lineSeparator());
                }else{
                    logMessage.append(String.format("Extra param: %s" , param));
                }
            }

        }
        logMessage.append(System.lineSeparator());
        if (record.getThrown() != null) {
            Throwable throwable = record.getThrown();
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            logMessage.append(sw.toString());
        }

        logMessage.append(System.lineSeparator());
        return logMessage.toString();
    }

    private String getThreadName(long threadID) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.threadId() == threadID) {
                return thread.getName();
            }
        }
        return "Unknown";
    }
}
