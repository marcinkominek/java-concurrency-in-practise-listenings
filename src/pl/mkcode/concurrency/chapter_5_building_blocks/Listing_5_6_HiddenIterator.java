package pl.mkcode.concurrency.chapter_5_building_blocks;

import pl.mkcode.concurrency.util.Runner;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * this code may cause {@link ConcurrentModificationException} because iteration is also invoked by the collection's
 * hashCode, equals and toString methods
 * solutions:
 *   - make addTenThings synchronized
 *   - use CopyOnWriteArraySet instead of HashSet
 */
public class Listing_5_6_HiddenIterator {

    public static void main(String... args) {
        Runner.runMultipleTimes(1_000, () -> {
            HiddenIterator hiddenIterator = new HiddenIterator();

            Runner.runSimultaneously(
                    () -> hiddenIterator.add(100),
                    () -> hiddenIterator.remove(100),
                    () -> hiddenIterator.addTenThings(),
                    () -> hiddenIterator.addTenThings(),
                    () -> hiddenIterator.add(200));
        });
    }

    private static class HiddenIterator {
        private final Set<Integer> set = new HashSet<>();

        synchronized void add(Integer i) {
            set.add(i);
        }

        synchronized void remove(Integer i) {
            set.remove(i);
        }

        void addTenThings() {
            Random r = new Random();
            for (int i = 0; i < 10; i++)
                add(r.nextInt());
            System.out.println("DEBUG: added ten elements to " + set);
        }
    }
}
