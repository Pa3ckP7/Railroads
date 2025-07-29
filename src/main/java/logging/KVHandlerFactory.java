package logging;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;

public class KVHandlerFactory {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    private static final Formatter formatter = new RawFormatter();
    public static ConsoleOutHandler getConsoleHandler(){
        return new ConsoleOutHandler(new ConsoleFormatter());
    }
    public static Handler getFileHandler(String name) throws IOException {

        File dir = new File("logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        var filename = dateFormat.format(new Date()) + "-" + name +"@"+ hash() + ".log";
        FileHandler handler = new FileHandler(dir.getPath()+"/"+filename, true);
        handler.setFormatter(formatter);
        return handler;
    }

    private static String hash(){
        return String.format("%04x", (int)(Math.random() * 0x10000));
    }
}
