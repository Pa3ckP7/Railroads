package railroads;

import logging.KVLogger;

import java.io.IOException;
import java.util.logging.Handler;

public class MainTesting {
    public static void main(String[] args) {
        var mlogger = new KVLogger(MainTesting.class, "master");
        for(int i = 0; i < 3; i++){
            var logger = new KVLogger(MainTesting.class, i+"");

            try {
                var h = KVLogger.CreateFileHandler(i+"");
                logger.attachHandler(h);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for(int j = 0; j < 100*(i+1); j++){
                logger.log(i+"",j);
            }
        }

        mlogger.warn("this is a warning", null);
        mlogger.error("this is an error", new Exception("random error"));
    }
}
