package logging;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class KVLoggerFactory implements AutoCloseable {

    private CopyOnWriteArrayList<Handler> inheritedHandlers = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, Handler> handlers = new ConcurrentHashMap<String, Handler>();
    private static final KVLoggerFactory globalInstance = new KVLoggerFactory(String.format("%04x",(int)(Math.random()*0x10000)));
    private final String scopeName;


    public static Logger getGlobalLogger(Class<?> clazz, String name) {
        return KVLoggerFactory.getGlobalLogger(clazz.getName()+"."+name);
    }
    public static Logger getGlobalLogger(Class<?> clazz){
        return KVLoggerFactory.getGlobalLogger(clazz.getName());
    }
    public static Logger getGlobalLogger(String name) {
        return globalInstance.getLogger(name);
    }

    public static KVLoggerFactory createScoped(String scopeName) {
        return createScoped(scopeName, true);
    }

    public static KVLoggerFactory createScoped(String name, boolean inheritHandlers){
        if (inheritHandlers){
            return new KVLoggerFactory(name, KVLoggerFactory.globalInstance.getHandlers());
        }else{
            return new KVLoggerFactory(name, new CopyOnWriteArrayList<>());
        }
    }

    public static void addGlobalHandler(String name, Handler handler, Formatter formatter){
        KVLoggerFactory.globalInstance.addHandler(name, handler, formatter);
    }
    public static void addGlobalHandler(String name, Handler handler){
        KVLoggerFactory.globalInstance.addHandler(name, handler);
    }

    public static void removeGlobalHandler(String name){
        KVLoggerFactory.globalInstance.removeHandler(name);
    }

    private KVLoggerFactory(String name){
        scopeName = name;
    }
    private KVLoggerFactory(String name, CopyOnWriteArrayList<Handler> inheritedHandlers){
        scopeName = name;
        this.inheritedHandlers = inheritedHandlers;
    }
    public Logger getLogger(Class<?> clazz){
        return getLogger(clazz.getName());
    }
    public Logger getLogger(Class<?> clazz, String name){
        return getLogger(clazz.getName()+"."+name);
    }
    public Logger getLogger(String name){
        var l = Logger.getLogger(name+"_"+scopeName);
        l.setUseParentHandlers(false);
        for(var h : handlers.values()){
            if(!hasHandler(l,h))
                l.addHandler(h);
        }
        for(var h : inheritedHandlers){
            if(!hasHandler(l,h))
                l.addHandler(h);
        }
        return l;
    }
    public void addHandler(String name, Handler handler, Formatter formatter){
        if(handlers.containsKey(name)) return ;
        handler.setFormatter(formatter);
        handlers.putIfAbsent(name, handler);
    }

    public void addHandler(String name, Handler handler){
        if(handlers.containsKey(name)) return ;
        handlers.put(name, handler);
    }
    public void removeHandler(String name){
        var handler = handlers.get(name);
        if (handler == null) return;
        handleHandlerDisposal(handler);
        handlers.remove(name);
    }

    public void close(){
        for(var h : handlers.values()){
            handleHandlerDisposal(h);
        }
        handlers.clear();
    }
    private boolean hasHandler(Logger logger, Handler handler){
        for(var h : logger.getHandlers()){
            if(h == handler) return true;
        }
        return false;
    }
    private CopyOnWriteArrayList<Handler> getHandlers(){
        return new CopyOnWriteArrayList<>(handlers.values());
    }

    private void handleHandlerDisposal(Handler handler){
        var lm = LogManager.getLogManager();
        var loggerNames = lm.getLoggerNames();
        while (loggerNames.hasMoreElements()) {
            String loggerName = loggerNames.nextElement();
            Logger logger = lm.getLogger(loggerName);
            if (logger != null) {
                for (Handler h : logger.getHandlers()) {
                    if (h == handler) {
                        logger.removeHandler(h);
                    }
                }
            }
        }
        handler.flush();
        handler.close();
    }
}
