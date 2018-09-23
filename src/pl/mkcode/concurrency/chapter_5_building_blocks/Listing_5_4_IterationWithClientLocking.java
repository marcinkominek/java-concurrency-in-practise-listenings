package pl.mkcode.concurrency.chapter_5_building_blocks;

import pl.mkcode.concurrency.util.Runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * added synchronized block to code from listing 5.3
 */
public class Listing_5_4_IterationWithClientLocking {
    private static int container = 0;

    private static void deleteLast(List<Integer> list) {
        synchronized (list) {
            int lastIndex = list.size() - 1;
            list.remove(lastIndex);
        }
    }

    private static void iteration(List<Integer> list) {
        synchronized (list) {
            for (int i=0; i < list.size(); i++) {
                doSomething(list.get(i));
            }
        }
    }

    private static void doSomething(Integer item) {
        container += item;
    }

    public static void main(String... args) {
        var list = Collections.synchronizedList(new ArrayList<Integer>());
        list.add(1);

        Runnable iterationTask = () -> Listing_5_4_IterationWithClientLocking.iteration(list);
        Runnable deleteLastTask = () -> Listing_5_4_IterationWithClientLocking.deleteLast(list);

        Runner.runSimultaneously(iterationTask, deleteLastTask);
    }


}
