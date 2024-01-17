package net.tclproject.mysteriumlib.multithreading;

// courtesy of TheFramework (austinv11)

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * This class is used to simplify multithreading for performance by delegating
 * multiple concurrent calculations across a variable amount of threads
 */
public class HeavyCalculations {

    private static int calculationNumber = 0;

    private List<CalculationThread> threads = new ArrayList<CalculationThread>();

    private ConcurrentHashMap<SimpleThread.ICalculations, Future> futures = new ConcurrentHashMap<SimpleThread.ICalculations, Future>();

    private boolean isKill = false;

    /**
     * Constructor for HeavyCalculations
     * @param numberOfThreads The number of threads to delegate calculations to
     */
    public HeavyCalculations(int numberOfThreads) {
        for (int i = 0; i < numberOfThreads; i++)
            threads.add(new CalculationThread());
    }

    private void removeCalculation(SimpleThread.ICalculations calculations) {
        for (CalculationThread thread : threads)
            if (thread.calculations.contains(calculations)) {
                thread.calculations.remove(calculations);
                return;
            }
    }

    private void delegate(SimpleThread.ICalculations calculations) {
        int minCalculations = -1;
        CalculationThread thread = null;
        for (CalculationThread thread1 : threads) {
            if ((minCalculations == -1 || minCalculations > thread1.calculations.size())) {
                minCalculations = thread1.calculations.size();
                thread = thread1;
            }
        }
        thread.calculations.add(calculations);
    }

    /**
     * Adds a calculation to the queues
     * @param calculation The calculation it MUST implement ICalculations
     * @return A future representing the eventual calculation {@link Future#get()} returns the object passed,
     * although after the calculations finished
     */
    public <T> Future<T> addCalculation(T calculation) {
        SimpleThread.ICalculations calculations = (SimpleThread.ICalculations) calculation;
        delegate(calculations);
        Future<T> future = new FutureImpl<T>(calculations, calculation);
        futures.put(calculations, future);
        return future;
    }

    /**
     * Adds a calculation to the queues by wrapping an object to implement {@link SimpleThread.ICalculations}
     * <b>This method is discouraged! It is meant for objects where it isn't possible to implement {@link SimpleThread.ICalculations}</b>
     * @param calculation The object representing the calculations
     * @param methodToCalculate The method to call for calculations
     * @param params The parameters for the method
     * @return A future representing the eventual calculation {@link Future#get()} returns the object passed,
     * although after the calculations finished
     */
    public <T> Future<T> addCalculation(T calculation, String methodToCalculate, Object... params) {
        SimpleThread.ICalculations calculations = new ICalculationsWrapper(calculation, methodToCalculate, params);
        delegate(calculations);
        Future<T> future = new FutureImpl<T>(calculations, calculation);
        futures.put(calculations, future);
        return future;
    }

    /**
     * Adds a calculation to the queues by wrapping a class to implement {@link SimpleThread.ICalculations}, used for static methods
     * when the class can't be instantiated
     * <b>This method is discouraged! It is meant for objects where it isn't possible to implement {@link SimpleThread.ICalculations}</b>
     * @param calculationClass The class representing the calculations
     * @param methodToCalculate The method to call for calculations
     * @param params The parameters for the method
     * @return A future representing the eventual calculation {@link Future#get()} returns null
     */
    public Future<SimpleThread.ICalculations> addCalculation(Class calculationClass, String methodToCalculate, Object... params) {
        SimpleThread.ICalculations calculations = new ICalculationsWrapper(calculationClass, methodToCalculate, params);
        delegate(calculations);
        Future<SimpleThread.ICalculations> future = new FutureImpl<SimpleThread.ICalculations>(calculations, null);
        futures.put(calculations, future);
        return future;
    }

    /**
     * Stops further calculations from occurring
     * <b>You can no longer add calculations after this!</b>
     */
    public void kill() {
        for (CalculationThread thread : threads)
            thread.disable(true);
        isKill = true;
    }

    /**
     * Use this to check if you could add calculations
     * @return True if this is dead
     */
    public boolean isDead() {
        return isKill;
    }

    private class CalculationThread extends SimpleRunnable {

        private int id;
        public ConcurrentLinkedDeque<SimpleThread.ICalculations> calculations = new ConcurrentLinkedDeque<SimpleThread.ICalculations>();

        public CalculationThread() {
            id = calculationNumber++;
            this.start();
        }

        @Override
        public void run() {
            if (!calculations.isEmpty()) {
                SimpleThread.ICalculations calculation = calculations.pop();
                calculation.doCalculation();
                ((FutureImpl)futures.get(calculation)).setDone();
            }
        }

        @Override
        public String getName() {
            return "Calculation Thread #"+id;
        }
    }

    private class ICalculationsWrapper implements SimpleThread.ICalculations {

        private Object object;
        private String methodName;
        private Object[] params;

        public ICalculationsWrapper(Object object, String methodName, Object[] params) {
            this.object = object;
            this.methodName = methodName;
            this.params = params;
        }

        /**
         * Attempts to find either a declared or normal method (in that order)
         * @param methodName The method to get
         * @param clazz The class to search
         * @return The method, or null if it wasn't found
         */
        public Method getDeclaredOrNormalMethod(String methodName, Class clazz) {
            for (Method m : clazz.getDeclaredMethods())
                if (m.getName().equals(methodName))
                    return m;
            for (Method m1 : clazz.getMethods())
                if (m1.getName().equals(methodName))
                    return m1;
            return null;
        }

        @Override
        public void doCalculation() {
            try {
                Method m;
                if (object instanceof Class)
                    m = getDeclaredOrNormalMethod(methodName, (Class) object);
                else
                    m = getDeclaredOrNormalMethod(methodName, object.getClass());
                m.invoke(object instanceof Class ? null : object, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class FutureImpl<V> implements Future<V> {

        private volatile SimpleThread.ICalculations calculations;
        private Object object;

        private boolean isCancelled = false;
        public volatile boolean isDone = false;

        public FutureImpl(SimpleThread.ICalculations calculations, Object object) {
            this.calculations = calculations;
            this.object = object;
        }

        public void setDone() {
            isDone = true;
            futures.remove(calculations);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            isCancelled = true;
            removeCalculation(calculations);
            return true;
        }

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public boolean isDone() {
            return isDone;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            while (!isDone) {}
            if (object != null)
                return (V) object;
            return null;
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            timeout = unit.toMillis(timeout);
            while (!isDone && timeout > 0) {
                timeout--;
                this.wait(1);
            }
            if (object != null)
                return (V) object;
            return null;
        }
    }
}