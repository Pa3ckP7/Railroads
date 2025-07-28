package logging;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.*;

public class KVLogger {
    private Logger _logger;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    private static final Formatter formatter = new RawFormatter();
    private static final Handler consoleHandler = new ConsoleOutHandler(new ConsoleFormatter());
    private static Handler fileHandler = null;

    public KVLogger(String name){
        setUpLogger(name);
    }
    public KVLogger(Class<?> clazz){
        setUpLogger(clazz.getName());
    }
    public KVLogger(Class<?> clazz, String name) {
        setUpLogger(clazz.getName()+"."+name);
    }

    private void setUpLogger(String name){
        _logger = Logger.getLogger(name);
        _logger.setUseParentHandlers(false);
        _logger.setLevel(Level.ALL);
        _logger.addHandler(consoleHandler);
    }

    public void attachHandler(Handler h){
        if(!hasHandler(_logger,h)){
            _logger.addHandler(h);
        }
    }
    private static String hash(){
        return String.format("%04x", (int)(Math.random() * 0x10000));
    }
    public static Handler CreateFileHandler(String name) throws IOException {

        File dir = new File("logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        var filename = dateFormat.format(new Date()) + "-" + name +"@"+ hash() + ".log";
        FileHandler handler = new FileHandler(dir.getPath()+"/"+filename, true);
        handler.setFormatter(formatter);
        return handler;
    }

    public void log(String key, Object value){
        _logger.info(key + " " + value.toString());
    }

    public void log(Object msg){
        _logger.info(msg.toString());
    }

    public void error(Object msg, Exception e){
        _logger.log(Level.SEVERE, msg.toString(), e);
    }

    public void warn(Object msg, Exception e){
        _logger.log(Level.WARNING, msg.toString(), e);
    }

    private static boolean hasHandler(Logger logger, Handler handler) {
        for (Handler h : logger.getHandlers()) {
            if (h == handler) return true;
        }
        return false;
    }

}
