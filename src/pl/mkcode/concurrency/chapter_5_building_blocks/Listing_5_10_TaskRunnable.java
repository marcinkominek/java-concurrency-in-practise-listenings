package pl.mkcode.concurrency.chapter_5_building_blocks;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * when code call method that throws InterruptedException:
 * - call Thread.currentThread().interrupt() to restore interrupted status
 * OR
 * - propagate exception
 */
public class Listing_5_10_TaskRunnable {

    public static void main(String... args) {
        BlockingQueue<Task> queue = new ArrayBlockingQueue<>(3);
        queue.add(() -> System.out.println("inside task"));

        new Thread(new TaskRunnable(queue)).start();
    }

    private static class TaskRunnable implements Runnable {
        private BlockingQueue<Task> queue;

        TaskRunnable(BlockingQueue<Task> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                processTask(queue.take());
            } catch (InterruptedException e) {
                // restore interrupted status
                Thread.currentThread().interrupt();
            }
        }

        private void processTask(Task task) {
            task.doSomething();
        }
    }

    @FunctionalInterface
    private interface Task {
        void doSomething();
    }
}
