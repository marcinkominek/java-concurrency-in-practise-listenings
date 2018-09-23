package pl.mkcode.concurrency.chapter_5_building_blocks;

import pl.mkcode.concurrency.util.Runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * added synchronized blocks to code from listing 5.1
 */
public class Listing_5_2_AddedSynchronizedBlocks {

    private static Object getLast(List<Integer> list) {
        synchronized (list) {
            int lastIndex = list.size() - 1;
            if (lastIndex >= 0) {
                return list.get(lastIndex);
            }
            return 0;
        }
    }

    private static void deleteLast(List<Integer> list) {
        synchronized (list) {
            int lastIndex = list.size() - 1;
            list.remove(lastIndex);
        }
    }

    public static void main(String... args) {
        Runner.runMultipleTimes(10_000, () -> {
            var list = Collections.synchronizedList(new ArrayList<Integer>());
            list.add(1);

            Runnable getLastTask = () -> Listing_5_2_AddedSynchronizedBlocks.getLast(list);
            Runnable deleteLastTask = () -> Listing_5_2_AddedSynchronizedBlocks.deleteLast(list);

            Runner.runSimultaneously(getLastTask, deleteLastTask);
        });
    }


}
