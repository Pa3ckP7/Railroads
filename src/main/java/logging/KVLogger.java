package logging;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.*;

public class KVLogger {
    private static Logger _logger;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    private static final Formatter formatter = new RawFormatter();
    private static final Handler consoleHandler = new ConsoleOutHandler(new ConsoleFormatter());

    private static String hash(){
        return String.format("%04x", (int)(Math.random() * 0x10000));
    }
    public static void initLogger(String name){

        _logger = Logger.getLogger("KeyValueLogger");
        _logger.setUseParentHandlers(false);
        File dir = new File("logs");
        if (!dir.exists()) {
            dir.mkdirs(); // Create the directory (and parents) if it doesn't exist
        }
        var filename = dateFormat.format(new Date()) + "-" + name +"@"+ hash() + ".log";
        try{
            var handler = new FileHandler(dir.getPath()+"/"+filename, true);
            handler.setFormatter(formatter);
            _logger.addHandler(handler);
            _logger.addHandler(consoleHandler);
        }catch(IOException e){
            System.err.println("Error: could not create log file " +  filename);
        }
    }

    public static void log(String key, Object value){
        _logger.info(key + " " + value.toString());
    }

    public static void log(Object msg){
        _logger.info(msg.toString());
    }

    public static void close(){
        for (Handler handler : _logger.getHandlers()) {
            handler.close();
            _logger.removeHandler(handler);
        }
    }

}
