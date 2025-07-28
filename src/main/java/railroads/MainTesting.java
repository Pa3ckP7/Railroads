package railroads;

import logging.KVLogger;

public class MainTesting {
    public static void main(String[] args) {
        for(int i = 0; i < 3; i++){
            KVLogger.initLogger(i+"");
            for(int j = 0; j < 100*(i+1); j++){
                KVLogger.log(i+"",j);
            }
            KVLogger.close();
        }
    }
}
