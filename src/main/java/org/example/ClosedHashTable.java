package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClosedHashTable<T> {

    private static final Object DELETED = new Object();

    private final Object[] data;
    private final int[] counts;
    private final Mode mode;

    private int size;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public enum Mode {
        LINEAR_PROBING,
        QUADRATIC_PROBING,
        DOUBLE_HASHING
    }

    public enum PutStep {
        P1, P2, P3, P4, P5, P6, P7, P8, P9
    }

    public enum GetStep {
        G1, G2, G3, G4, G5
    }

    public enum RemoveStep {
        R1, R2, R3, R4, R5, R6
    }

    public record TraceResult<E, S extends Enum<S>>(E value, List<S> trace) {
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
        lock.writeLock().lock();
        try {
            boolean inserted = putInternal(key, step -> {});
            if (!inserted) {
                throw new UnsupportedOperationException("Hash table is full");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public T get(T key) {
        lock.readLock().lock();
        try {
            return getInternal(key, step -> {});
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean remove(T key) {
        lock.writeLock().lock();
        try {
            return removeInternal(key, step -> {});
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean contains(T key) {
        lock.readLock().lock();
        try {
            return getInternal(key, step -> {}) != null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int occurrences(T key) {
        lock.readLock().lock();
        try {
            validateKey(key);
            int index = findSlotForSearch(key);
            if (index == -1) {
                return 0;
            }
            return counts[index];
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int capacity() {
        return data.length;
    }

    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return size == 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            Arrays.fill(data, null);
            Arrays.fill(counts, 0);
            size = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Object[] getData() {
        lock.readLock().lock();
        try {
            return Arrays.copyOf(data, data.length);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int[] getCounts() {
        lock.readLock().lock();
        try {
            return Arrays.copyOf(counts, counts.length);
        } finally {
            lock.readLock().unlock();
        }
    }

    public TraceResult<Boolean, PutStep> tracePut(T key) {
        lock.writeLock().lock();
        try {
            List<PutStep> trace = new ArrayList<>();
            boolean result = putInternal(key, trace::add);
            return new TraceResult<>(result, List.copyOf(trace));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public TraceResult<T, GetStep> traceGet(T key) {
        lock.readLock().lock();
        try {
            List<GetStep> trace = new ArrayList<>();
            T result = getInternal(key, trace::add);
            return new TraceResult<>(result, List.copyOf(trace));
        } finally {
            lock.readLock().unlock();
        }
    }

    public TraceResult<Boolean, RemoveStep> traceRemove(T key) {
        lock.writeLock().lock();
        try {
            List<RemoveStep> trace = new ArrayList<>();
            boolean result = removeInternal(key, trace::add);
            return new TraceResult<>(result, List.copyOf(trace));
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean putInternal(T key, Consumer<PutStep> tracer) {
        validateKey(key);

        tracer.accept(PutStep.P1);

        int firstDeleted = -1;
        int hash = primaryHash(key);

        tracer.accept(PutStep.P2);

        for (int step = 0; step < data.length; step++) {
            int index = probeIndex(hash, key, step);
            Object current = data[index];

            if (current == null) {
                tracer.accept(PutStep.P3);

                int target = (firstDeleted != -1) ? firstDeleted : index;
                data[target] = key;
                counts[target] = 1;
                size++;

                tracer.accept(PutStep.P6);
                return true;
            }

            if (current == DELETED) {
                if (firstDeleted == -1) {
                    firstDeleted = index;
                }
                tracer.accept(PutStep.P4);
                tracer.accept(PutStep.P5);
                continue;
            }

            if (current.equals(key)) {
                tracer.accept(PutStep.P7);
                counts[index]++;
                tracer.accept(PutStep.P8);
                return true;
            }

            tracer.accept(PutStep.P4);
            tracer.accept(PutStep.P5);
        }

        if (firstDeleted != -1) {
            data[firstDeleted] = key;
            counts[firstDeleted] = 1;
            size++;

            tracer.accept(PutStep.P6);
            return true;
        }

        tracer.accept(PutStep.P9);
        return false;
    }

    private T getInternal(T key, Consumer<GetStep> tracer) {
        validateKey(key);

        tracer.accept(GetStep.G1);

        int hash = primaryHash(key);
        tracer.accept(GetStep.G2);

        for (int step = 0; step < data.length; step++) {
            int index = probeIndex(hash, key, step);
            Object current = data[index];

            if (current == null) {
                tracer.accept(GetStep.G5);
                return null;
            }

            if (current != DELETED && current.equals(key)) {
                tracer.accept(GetStep.G3);

                @SuppressWarnings("unchecked")
                T value = (T) current;
                return value;
            }

            tracer.accept(GetStep.G4);
        }

        tracer.accept(GetStep.G5);
        return null;
    }

    private boolean removeInternal(T key, Consumer<RemoveStep> tracer) {
        validateKey(key);

        tracer.accept(RemoveStep.R1);

        int hash = primaryHash(key);

        for (int step = 0; step < data.length; step++) {
            int index = probeIndex(hash, key, step);
            Object current = data[index];

            if (current == null) {
                tracer.accept(RemoveStep.R6);
                return false;
            }

            if (current != DELETED && current.equals(key)) {
                tracer.accept(RemoveStep.R2);

                if (counts[index] > 1) {
                    counts[index]--;
                    tracer.accept(RemoveStep.R3);
                } else {
                    data[index] = DELETED;
                    counts[index] = 0;
                    size--;
                    tracer.accept(RemoveStep.R4);
                }

                return true;
            }

            tracer.accept(RemoveStep.R5);
        }

        tracer.accept(RemoveStep.R6);
        return false;
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

        return switch (mode) {
            case LINEAR_PROBING -> Math.floorMod(hash + step, capacity);
            case QUADRATIC_PROBING -> Math.floorMod(hash + step * step, capacity);
            case DOUBLE_HASHING -> Math.floorMod(hash + step * secondaryHash(key), capacity);
        };
    }

    private int primaryHash(T key) {
        return Math.floorMod(key.hashCode(), data.length);
    }

    private int secondaryHash(T key) {
        if (data.length == 1) {
            return 1;
        }
        return 1 + Math.floorMod(key.hashCode(), data.length - 1);
    }

    private void validateKey(T key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
    }
}