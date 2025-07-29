package timing;

public class Timer {
    private long start;
    private long sum = 0L;

    private boolean running = false;
    public synchronized void start(){
        start = System.nanoTime();
        running = true;
    }
    public synchronized long pause(){
        if(!running)return 0;
        var lap = System.nanoTime() - start;
        sum += lap;
        start = 0;
        running = false;
        return lap;
    }
    public synchronized long stop(){
        var ret = sum + (running?System.nanoTime()-start:0);
        sum = 0L;
        running = false;
        return ret;
    }
    public synchronized long lap(){
        return sum + (running?System.nanoTime()-start:0);
    }
}
