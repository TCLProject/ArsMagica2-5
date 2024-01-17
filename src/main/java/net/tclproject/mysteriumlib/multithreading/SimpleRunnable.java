package net.tclproject.mysteriumlib.multithreading;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// courtesy of TheFramework (austinv11)
public abstract class SimpleRunnable implements Runnable {

    public volatile static boolean RESTRICT_THREAD_USAGE = true;

    //Key of true = the thread is being used, vice versa
    private volatile static ConcurrentHashMap<Boolean, ConcurrentHashMap<Integer, SimpleThread>> threadMap = new ConcurrentHashMap<Boolean, ConcurrentHashMap<Integer, SimpleThread>>();

    private volatile boolean isCleaned = false;
    private volatile int thread = -1;
    private volatile long delay = -1;

    static {
        threadMap.put(true, new ConcurrentHashMap<Integer, SimpleThread>());
        threadMap.put(false, new ConcurrentHashMap<Integer, SimpleThread>());
    }

    /**
     * Disables this thread from running
     * @param clean Whether to clean the thread running - This will prevent this runnable from being able to run, only cleans if enabled in the config
     * @throws IllegalThreadStateException
     */
    public final void disable(boolean clean) throws IllegalThreadStateException {
        if (isCleaned)
            throw new IllegalThreadStateException("Thread "+this.getName()+" is cleaned already!");
        getThread().isActive = false;
        getThread().delay = -1;
        getThread().started = false;
        if (clean && RESTRICT_THREAD_USAGE) {
            isCleaned = true;
            add(threadMap.get(false), getThread());
            threadMap.get(true).remove(thread);
        }
    }

    /**
     * Attempts to reenable this thread
     * @throws IllegalThreadStateException
     */
    public final void enable() throws IllegalThreadStateException {
        if (isCleaned)
            throw new IllegalThreadStateException("Thread "+this.getName()+" is cleaned already!");
        getThread().isActive = true;
        getThread().delay = delay;
        getThread().started = true;
    }

    /**
     * Runs a thread with this runnable
     */
    public final void start() {
        getThread().start();
    }

    /**
     * Called by the thread to run this runnable
     */
    @Override
    public abstract void run();

    /**
     * Gets the name of the thread
     * @return The name
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Delays the given thread from running
     * @param delay The length of the delay (in milliseconds)
     */
    public final void delay(int delay) throws InterruptedException {
        getThread().wait(delay);
    }

    /**
     * Gets whether is runnable is cleaned
     * @return IsCleaned
     */
    public boolean isCleaned() {
        return isCleaned;
    }

    /**
     * Gets whether the runnable is running
     * @return IsEnabled
     */
    public boolean isEnabled() {
        return getThread().isActive;
    }

    /**
     * Makes the thread loop every x milliseconds
     * @param delay The delay for each loop, in milliseconds
     */
    public void setTicking(long delay) {
        this.delay = delay;
        getThread().delay = delay;
    }

    /**
     * Gets the internal thread running the runnable
     * @return The thread
     */
    private final SimpleThread getThread() {
        if (this.thread == -1) { //Assign a thread
            if (threadMap.get(false).size() < 1) {
                SimpleThread thread = new SimpleThread(this, this.getName());
                this.thread = add(threadMap.get(true), thread);
                return getThread();
            } else {
                Map.Entry<Integer, SimpleThread> entry = threadMap.get(false).entrySet().iterator().next();
                SimpleThread thread = entry.getValue();
                threadMap.get(false).remove(entry.getKey());
                thread.isActive = true;
                thread.setName(getName());
                try {
                    thread.setTarget(this);
                    this.thread = add(threadMap.get(true), thread);
                    return getThread();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else { //Fetch the thread
            return threadMap.get(true).get(this.thread);
        }
        return null; //This should never be reached
    }

    private static <T> int add(Map<Integer, T> map, T o) {
        int i = 0;
        while (map.containsKey(i))
            i++;
        map.put(i, o);
        return i;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SimpleRunnable)
            return ((SimpleRunnable) other).thread == thread && ((SimpleRunnable) other).getName().equals(getName());
        return false;
    }

    @Override
    public String toString() {
        return "SimpleRunnable(Name: "+getName()+" ID:"+thread+" Is Cleaned: "+isCleaned+")";
    }
}