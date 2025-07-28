package logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ConsoleFormatter extends Formatter {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        var time =  sdf.format(new Date(record.getMillis()));
        var level = record.getLevel();
        var thread = Thread.currentThread().getName();
        var message = record.getMessage();
        var finalMessage = String.format("%s | [%s]\n%s:\t%s", time, thread, level, message);
        if(level == Level.WARNING){
            finalMessage = "\u001B[33m" + finalMessage + "\u001B[0m\n";
        }
        if(level == Level.SEVERE){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            if (record.getThrown() != null) {
                pw.write("\n");
                record.getThrown().printStackTrace(pw);
            }
            finalMessage = "\u001B[31m" + finalMessage + sw.toString() + "\u001B[0m\n";
        }
        return finalMessage +"\n";
    }
}
