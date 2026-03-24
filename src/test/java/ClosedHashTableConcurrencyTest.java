import org.example.ClosedHashTable;

import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ClosedHashTableConcurrencyTest {

    @RepeatedTest(100)
    void shouldRemainConsistentUnderHighConcurrency() throws Exception {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(1024, ClosedHashTable.Mode.LINEAR_PROBING);

        int threads = 10;
        int operationsPerThread = 2000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            final int threadId = t;

            futures.add(executor.submit(() -> {
                startLatch.await();

                for (int i = 0; i < operationsPerThread; i++) {
                    int key = (threadId * 10000 + i) % 300;

                    if (i % 3 == 0) {
                        table.put(key);
                    } else if (i % 3 == 1) {
                        table.get(key);
                    } else {
                        table.remove(key);
                    }
                }

                return null;
            }));
        }

        startLatch.countDown();

        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        Object[] data = table.getData();
        int[] counts = table.getCounts();

        assertEquals(data.length, counts.length);
        assertTrue(table.size() >= 0);

        int occupiedSlots = 0;

        for (int i = 0; i < data.length; i++) {
            Object slot = data[i];
            int count = counts[i];

            if (slot == null) {
                assertEquals(0, count, "Для null-ячейки count должен быть 0");
            } else {
                if (count > 0) {
                    occupiedSlots++;
                }
                assertTrue(count >= 0, "Счётчик не должен быть отрицательным");
            }
        }

        assertTrue(table.size() <= occupiedSlots,
                "size не должен превышать число реально занятых ячеек");
    }
}
