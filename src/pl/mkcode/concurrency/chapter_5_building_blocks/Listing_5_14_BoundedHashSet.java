package pl.mkcode.concurrency.chapter_5_building_blocks;

import pl.mkcode.concurrency.util.Runner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Listing_5_14_BoundedHashSet {
    private final static int BOUND = 2;
    private final static long ADDING_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
    private final static long DELETING_TIMEOUT = TimeUnit.SECONDS.toMillis(3);

    public static void main(String... args) {
        BoundedHashSet<Integer> boundedHashSet = new BoundedHashSet<>(BOUND);
        Runnable addingTask = () -> {
            int index = 0;
            while (true) {
                try {
                    boundedHashSet.add(index++);
                    Thread.sleep(ADDING_TIMEOUT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Runnable deletingTask = () -> {
            int index = 0;
            while (true) {
                try {
                    boundedHashSet.remove(index++);
                    Thread.sleep(DELETING_TIMEOUT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Runner.runSimultaneously(addingTask, deletingTask);
    }

    private static class BoundedHashSet<T> {
        private final Set<T> set;
        private final Semaphore sem;

        BoundedHashSet(int bound) {
            this.set = Collections.synchronizedSet(new HashSet<>());
            sem = new Semaphore(bound);
        }

        private boolean add(T o) throws InterruptedException {
            System.out.println("adding: " + o);
            sem.acquire();
            boolean wasAdded = false;
            try {
                wasAdded = set.add(o);
                return wasAdded;
            } finally {
                if (!wasAdded)
                    sem.release();
            }
        }

        private boolean remove(Object o) {
            System.out.println("removing: " + o);
            boolean wasRemoved = set.remove(o);
            if (wasRemoved)
                sem.release();
            return wasRemoved;
        }
    }
}
