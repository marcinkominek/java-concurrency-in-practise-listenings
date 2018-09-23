package pl.mkcode.concurrency.chapter_5_building_blocks;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Listing_5_8_producer_and_consumer {

    private static final int BOUND = 10;
    private static final int N_CONSUMERS = 1;
    private static final List<String> INDEXED = new ArrayList<>();

    public static void main(String... args) {
        startIndexing(new File[]{new File(".")});
    }

    private static void startIndexing(File[] roots) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(BOUND);
        FileFilter filter = file -> true;
        for (File root : roots)
            new Thread(new FileCrawler(queue, filter, root)).start();
        for (int i = 0; i < N_CONSUMERS; i++)
            new Thread(new Indexer(queue)).start();
    }

    private static boolean alreadyIndexed(File file) {
        return INDEXED.contains(file.getAbsolutePath());
    }

    private static void indexFile(File file) {
        String path = file.getAbsolutePath();
        System.out.println("indexed: " + path);
        INDEXED.add(path);
    }

    private static class FileCrawler implements Runnable {
        private final BlockingQueue<File> fileQueue;
        private final FileFilter fileFilter;
        private final File root;

        FileCrawler(BlockingQueue<File> fileQueue, FileFilter fileFilter, File root) {
            this.fileQueue = fileQueue;
            this.fileFilter = fileFilter;
            this.root = root;
        }

        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void crawl(File root) throws InterruptedException {
            File[] entries = root.listFiles(fileFilter);
            if (entries != null) {
                for (File entry : entries)
                    if (entry.isDirectory())
                        crawl(entry);
                    else if (!alreadyIndexed(entry))
                        fileQueue.put(entry);
            }
        }
    }

    private static class Indexer implements Runnable {
        private final BlockingQueue<File> queue;

        Indexer(BlockingQueue<File> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                while (true)
                    indexFile(queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
