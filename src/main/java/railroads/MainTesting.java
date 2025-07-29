package railroads;

import logging.*;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;

public class MainTesting {
    public static void main(String[] args) {
        KVLoggerFactory.addGlobalHandler("console", KVHandlerFactory.getConsoleHandler());
        var mlogger = KVLoggerFactory.getGlobalLogger(MainTesting.class);
        for(int i = 0; i < 3; i++){
            try ( var scope = KVLoggerFactory.createScoped(i+"")){
                var h = KVHandlerFactory.getFileHandler(i+"");
                scope.addHandler("file",h);
                var logger = scope.getLogger(MainTesting.class);

                for(int j = 0; j < 100*(i+1); j++){
                    logger.info(String.format("%s\t%s", i,j));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        mlogger.warning("this is a warning");
        mlogger.log(Level.SEVERE, "this is an error", new Exception("random error"));
    }
}
