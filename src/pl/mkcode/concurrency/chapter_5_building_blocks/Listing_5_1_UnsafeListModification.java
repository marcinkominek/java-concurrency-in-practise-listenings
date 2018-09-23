package pl.mkcode.concurrency.chapter_5_building_blocks;

import pl.mkcode.concurrency.util.Runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * running {@link #getLast(List)} and {@link #deleteLast(List)} simultaneously may cause {@link ArrayIndexOutOfBoundsException}
 * solution:
 *  - add synchronized block; see: {@link Listing_5_2_AddedSynchronizedBlocks}
 *  - use CopyOnWriteArrayList
 */
public class Listing_5_1_UnsafeListModification {

    private static Object getLast(List<Integer> list) {
        int lastIndex = list.size() - 1;
        if (lastIndex >= 0) {
            return list.get(lastIndex);
        }
        return 0;
    }

    private static void deleteLast(List<Integer> list) {
        int lastIndex = list.size() - 1;
        list.remove(lastIndex);
    }

    //example of code that can throw ArrayIndexOutOfBoundsException
    public static void main(String... args) {
        Runner.runMultipleTimes(10_000, () -> {
            var list = Collections.synchronizedList(new ArrayList<Integer>());
            list.add(1);

            Runnable getLastTask = () -> getLast(list);
            Runnable deleteLastTask = () -> deleteLast(list);

            Runner.runSimultaneously(getLastTask, deleteLastTask);
        });
    }

}
