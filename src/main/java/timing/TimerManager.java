package timing;

import java.util.concurrent.ConcurrentHashMap;

public class TimerManager {
    private static final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();

    public static void startTimer(String name){
        Timer timer = timers.computeIfAbsent(name, k -> new Timer());
        timer.start();
    }

    public static long pauseTimer(String name){
        Timer timer = timers.get(name);
        if(timer == null) throw new IllegalArgumentException("Timer doesn't exist");
        return timer.pause();
    }
    public static long stopTimer(String name){
        Timer timer = timers.get(name);
        if(timer == null) throw new IllegalArgumentException("Timer doesn't exist");
        var ret = timer.stop();
        timers.remove(name);
        return ret;
    }

    public static long lapTimer(String name){
        Timer timer = timers.get(name);
        if(timer == null) throw new IllegalArgumentException("Timer doesn't exist");
        return timer.lap();
    }
}
