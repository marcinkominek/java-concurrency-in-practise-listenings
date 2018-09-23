package pl.mkcode.concurrency.chapter_5_building_blocks;

import java.util.concurrent.CountDownLatch;

public class Listing_5_11_TestHarness {

    public static void main(String... args) throws InterruptedException {
        TestHarness testHarness = new TestHarness();
        testHarness.timeTasks(5, () -> {});
    }

    private static class TestHarness {

        long timeTasks(int nThreads, final Runnable task) throws InterruptedException {

            final CountDownLatch startGate = new CountDownLatch(1);
            final CountDownLatch endGate = new CountDownLatch(nThreads);
            for (int i = 0; i < nThreads; i++) {
                Thread t = new Thread(() -> {
                    try {
                        System.out.println("await");
                        startGate.await();
                        try {
                            System.out.println("run");
                            task.run();
                        } finally {
                            endGate.countDown();
                        }
                    } catch (InterruptedException ignored) { }
                });
                t.start();
            }
            long start = System.nanoTime();
            startGate.countDown();
            endGate.await();
            long end = System.nanoTime();
            return end-start;
        }
    }
}
