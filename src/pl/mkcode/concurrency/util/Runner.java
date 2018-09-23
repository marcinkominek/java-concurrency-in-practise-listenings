package pl.mkcode.concurrency.util;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Runner {

    public static void runMultipleTimes(int times, Runnable runnable) {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<?>> results = IntStream.range(0, times)
                .mapToObj(i -> executor.submit(runnable))
                .collect(Collectors.toList());
        try {
            for (Future<?> result : results) {
                result.get();
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw launderThrowable(cause);
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }

    public static void runSimultaneously(Runnable... runnables) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Stream.of(runnables)
                .map(runnable -> new SimultaneousTask(runnable, countDownLatch))
                .forEach(Thread::start);

        countDownLatch.countDown();
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    /** If the Throwable is an Error, throw it; if it is a
     * RuntimeException return it, otherwise throw IllegalStateException
     */
    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }

    private static class SimultaneousTask extends Thread {

        private final Runnable innerRunnable;
        private final CountDownLatch countDownLatch;

        SimultaneousTask(Runnable innerRunnable, CountDownLatch countDownLatch) {
            this.innerRunnable = innerRunnable;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
                Thread.currentThread().interrupt();
            }

            innerRunnable.run();
        }
    }
}
