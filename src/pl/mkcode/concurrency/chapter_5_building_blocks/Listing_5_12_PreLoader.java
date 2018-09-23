package pl.mkcode.concurrency.chapter_5_building_blocks;

import pl.mkcode.concurrency.util.Runner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Listing_5_12_PreLoader {

    private static class PreLoader {
        private final FutureTask<ProductInfo> future = new FutureTask<>(this::loadProductInfo);

        private final Thread thread = new Thread(future);

        public void start() {
            thread.start();
        }

        public ProductInfo get() throws DataLoadException, InterruptedException {
            try {
                return future.get();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof DataLoadException)
                    throw (DataLoadException) cause;
                else
                    throw launderThrowable(cause);
            }
        }


        private ProductInfo loadProductInfo() throws DataLoadException {
            // some code that can throw DataLoadException
            // ...
            return new ProductInfo();
        }

        /**
         * If the Throwable is an Error, throw it; if it is a
         * RuntimeException return it, otherwise throw IllegalStateException
         * used in {@link Runner#launderThrowable(Throwable)}
         */
        public static RuntimeException launderThrowable(Throwable t) {
            if (t instanceof RuntimeException)
                return (RuntimeException) t;
            else if (t instanceof Error)
                throw (Error) t;
            else
                throw new IllegalStateException("Not unchecked", t);
        }
    }

    private static class ProductInfo {

    }

    private static class DataLoadException extends Exception {
        public DataLoadException() {
        }
    }
}
