package pl.mkcode.concurrency.chapter_5_building_blocks;

import pl.mkcode.concurrency.util.Runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * simultaneous iteration over list and modification; may cause {@link ArrayIndexOutOfBoundsException}
 * solution:
 *  - add synchronized block; see: {@link Listing_5_4_IterationWithClientLocking}
 *  - use CopyOnWriteArrayList
 */
public class Listing_5_3_UnsafeIteration {

    private static void deleteLast(List<Integer> list) {
        synchronized (list) {
            int lastIndex = list.size() - 1;
            list.remove(lastIndex);
        }
    }

    private static void iteration(List<Integer> list) {
        for (int i=0; i < list.size(); i++) {
            doSomething(list.get(i));
        }
    }

    private static void doSomething(Integer item) {
        System.out.println(item);
    }

    // code below can throw ArrayIndexOutOfBoundsException
    public static void main(String... args) {
        var list = Collections.synchronizedList(new ArrayList<Integer>());
        list.add(1);

        Runnable iterationTask = () -> Listing_5_3_UnsafeIteration.iteration(list);
        Runnable deleteLastTask = () -> Listing_5_3_UnsafeIteration.deleteLast(list);

        Runner.runSimultaneously(iterationTask, deleteLastTask);
    }


}
