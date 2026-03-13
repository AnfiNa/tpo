package org.example;

import java.util.Arrays;
import java.util.Objects;

public class ClosedHashTable<T> {

    private static final Object DELETED = new Object();

    private final Object[] data;
    private final int[] counts;
    private final Mode mode;

    private int size;

    public enum Mode {
        LINEAR_PROBING,
        QUADRATIC_PROBING,
        DOUBLE_HASHING
    }

    public static class TraceResult<E> {
        private final E value;
        private final String trace;

        public TraceResult(E value, String trace) {
            this.value = value;
            this.trace = trace;
        }

        public E getValue() {
            return value;
        }

        public String getTrace() {
            return trace;
        }
    }

    public TraceResult<T> traceGet(T key) {
        validateKey(key);

        StringBuilder trace = new StringBuilder("G1");

        int hash = primaryHash(key);
        trace.append(" -> G2");

        for (int step = 0; step < data.length; step++) {
            int index = probeIndex(hash, key, step);
            Object current = data[index];

            if (current == null) {
                trace.append(" -> G5");
                return new TraceResult<>(null, trace.toString());
            }

            if (current != DELETED && current.equals(key)) {
                trace.append(" -> G3");
                @SuppressWarnings("unchecked")
                T value = (T) current;
                return new TraceResult<>(value, trace.toString());
            }

            trace.append(" -> G4");
        }

        trace.append(" -> G5");
        return new TraceResult<>(null, trace.toString());
    }

    public TraceResult<Boolean> traceRemove(T key) {
        validateKey(key);

        StringBuilder trace = new StringBuilder("R1");

        int hash = primaryHash(key);

        for (int step = 0; step < data.length; step++) {
            int index = probeIndex(hash, key, step);
            Object current = data[index];

            if (current == null) {
                trace.append(" -> R6");
                return new TraceResult<>(false, trace.toString());
            }

            if (current != DELETED && current.equals(key)) {
                trace.append(" -> R2");

                if (counts[index] > 1) {
                    counts[index]--;
                    trace.append(" -> R3");
                } else {
                    data[index] = DELETED;
                    counts[index] = 0;
                    size--;
                    trace.append(" -> R4");
                }

                return new TraceResult<>(true, trace.toString());
            }

            trace.append(" -> R5");
        }

        trace.append(" -> R6");
        return new TraceResult<>(false, trace.toString());
    }

    public TraceResult<Boolean> tracePut(T key) {
        validateKey(key);

        StringBuilder trace = new StringBuilder("P1");

        int firstDeleted = -1;
        int hash = primaryHash(key);
        trace.append(" -> P2");

        for (int step = 0; step < data.length; step++) {
            int index = probeIndex(hash, key, step);
            Object current = data[index];

            if (current == null) {
                trace.append(" -> P3");
                int target = firstDeleted != -1 ? firstDeleted : index;

                data[target] = key;
                counts[target] = 1;
                size++;

                trace.append(" -> P6");
                return new TraceResult<>(true, trace.toString());
            }

            if (current == DELETED) {
                if (firstDeleted == -1) {
                    firstDeleted = index;
                }
                trace.append(" -> P4 -> P5");
                continue;
            }

            if (current.equals(key)) {
                trace.append(" -> P7");
                counts[index]++;
                trace.append(" -> P8");
                return new TraceResult<>(true, trace.toString());
            }

            trace.append(" -> P4 -> P5");
        }

        if (firstDeleted != -1) {
            data[firstDeleted] = key;
            counts[firstDeleted] = 1;
            size++;
            trace.append(" -> P6");
            return new TraceResult<>(true, trace.toString());
        }

        trace.append(" -> P9");
        throw new UnsupportedOperationException("Hash table is full");
    }


    public ClosedHashTable(int capacity, Mode mode) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.data = new Object[capacity];
        this.counts = new int[capacity];
        this.mode = Objects.requireNonNull(mode, "mode must not be null");
        this.size = 0;
    }

    public void put(T key) {
        validateKey(key);

        int index = findSlotForInsert(key);
        if (index == -1) {
            throw new UnsupportedOperationException("Hash table is full");
        }

        if (isStoredKey(index, key)) {
            counts[index]++;
            return;
        }

        data[index] = key;
        counts[index] = 1;
        size++;
    }

    public T get(T key) {
        validateKey(key);

        int index = findSlotForSearch(key);
        if (index == -1) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T value = (T) data[index];
        return value;
    }

    public boolean remove(T key) {
        validateKey(key);

        int index = findSlotForSearch(key);
        if (index == -1) {
            return false;
        }

        if (counts[index] > 1) {
            counts[index]--;
        } else {
            data[index] = DELETED;
            counts[index] = 0;
            size--;
        }

        return true;
    }

    public boolean contains(T key) {
        return get(key) != null;
    }

    public int occurrences(T key) {
        int index = findSlotForSearch(key);
        if (index == -1) {
            return 0;
        }
        return counts[index];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Arrays.fill(data, null);
        Arrays.fill(counts, 0);
        size = 0;
    }

    public Object[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public int[] getCounts() {
        return Arrays.copyOf(counts, counts.length);
    }

    private int findSlotForInsert(T key) {
        int firstDeleted = -1;
        int hash = primaryHash(key);

        for (int step = 0; step < data.length; step++) {
            int index = probeIndex(hash, key, step);
            Object current = data[index];

            if (current == null) {
                return firstDeleted != -1 ? firstDeleted : index;
            }

            if (current == DELETED) {
                if (firstDeleted == -1) {
                    firstDeleted = index;
                }
                continue;
            }

            if (current.equals(key)) {
                return index;
            }
        }

        return firstDeleted;
    }

    private int findSlotForSearch(T key) {
        int hash = primaryHash(key);

        for (int step = 0; step < data.length; step++) {
            int index = probeIndex(hash, key, step);
            Object current = data[index];

            if (current == null) {
                return -1;
            }

            if (current != DELETED && current.equals(key)) {
                return index;
            }
        }

        return -1;
    }

    private int probeIndex(int hash, T key, int step) {
        int capacity = data.length;

        switch (mode) {
            case LINEAR_PROBING:
                return Math.floorMod(hash + step, capacity);
            case QUADRATIC_PROBING:
                return Math.floorMod(hash + step * step, capacity);
            case DOUBLE_HASHING:
                return Math.floorMod(hash + step * secondaryHash(key), capacity);
            default:
                throw new IllegalStateException("unknown probing mode");
        }
    }

    private int primaryHash(T key) {
        return Math.floorMod(key.hashCode(), data.length);
    }

    private int secondaryHash(T key) {
        return 1 + Math.floorMod(key.hashCode(), data.length - 1);
    }

    private boolean isStoredKey(int index, T key) {
        Object current = data[index];
        return current != null && current != DELETED && current.equals(key);
    }

    private void validateKey(T key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
    }
}