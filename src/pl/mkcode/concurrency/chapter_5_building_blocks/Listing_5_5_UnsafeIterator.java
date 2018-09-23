package pl.mkcode.concurrency.chapter_5_building_blocks;

import pl.mkcode.concurrency.util.Runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * simultaneous iteration over list and modification; it can cause ConcurrentModificationException
 * solutions:
 * - use synchronized(list) block; see: {@link Listing_5_5_UnsafeIterator#iterationWithSynchronizedBlock(List)}
 * - iterate over copy of list; see: {@link Listing_5_5_UnsafeIterator#iterationOverCopiedList(ArrayList)}
 */
public class Listing_5_5_UnsafeIterator {

    private static void deleteLast(List<Integer> list) {
        synchronized (list) {
            int lastIndex = list.size() - 1;
            list.remove(lastIndex);
        }
    }

    private static void iteration(List<Integer> list) {
        for (Integer item : list) {
            doSomething(item);
        }
    }

    public static void iterationWithSynchronizedBlock(List<Integer> list) {
        synchronized (list) {
            for (Integer item : list) {
                doSomething(item);
            }
        }
    }

    private static void iterationOverCopiedList(ArrayList<Integer> list) {
        ArrayList<Integer> copy = (ArrayList<Integer>) list.clone();
        for (Integer item : copy) {
            doSomething(item);
        }
    }

    private static void doSomething(Integer item) {
        System.out.println(item);
    }

    // code below can throw ConcurrentModificationException
    public static void main(String... args) {
        var list = Collections.synchronizedList(new ArrayList<Integer>());
        list.add(1);

        Runnable iterationTask = () -> Listing_5_5_UnsafeIterator.iteration(list);
        Runnable deleteLastTask = () -> Listing_5_5_UnsafeIterator.deleteLast(list);

        Runner.runSimultaneously(iterationTask, deleteLastTask);
    }


}
