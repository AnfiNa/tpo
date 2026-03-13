import org.example.ClosedHashTable;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClosedHashTableTest {

    private static final class TestKey {
        private final String value;
        private final int hash;

        private TestKey(String value, int hash) {
            this.value = value;
            this.hash = hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof TestKey other)) return false;
            return value.equals(other.value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Test
    void constructorShouldCreateEmptyTable() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        assertEquals(0, table.size());
        assertEquals(5, table.capacity());
        assertTrue(table.isEmpty());
    }

    @Test
    void constructorShouldThrowForNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class,
                () -> new ClosedHashTable<>(0, ClosedHashTable.Mode.LINEAR_PROBING));
    }

    @Test
    void constructorShouldThrowForNullMode() {
        assertThrows(NullPointerException.class,
                () -> new ClosedHashTable<>(5, null));
    }

    @Test
    void putAndGetShouldWorkForSingleElement() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");

        assertEquals("A", table.get("A"));
        assertTrue(table.contains("A"));
        assertEquals(1, table.size());
        assertEquals(1, table.occurrences("A"));
        assertFalse(table.isEmpty());
    }

    @Test
    void getShouldReturnNullForMissingKey() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        assertNull(table.get("missing"));
        assertFalse(table.contains("missing"));
        assertEquals(0, table.occurrences("missing"));
    }

    @Test
    void putShouldIncreaseOccurrencesForDuplicateKey() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");
        table.put("A");
        table.put("A");

        assertEquals("A", table.get("A"));
        assertEquals(1, table.size());
        assertEquals(3, table.occurrences("A"));
    }

    @Test
    void removeShouldDecreaseOccurrencesWhenKeyWasInsertedSeveralTimes() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");
        table.put("A");

        assertTrue(table.remove("A"));

        assertEquals(1, table.size());
        assertEquals(1, table.occurrences("A"));
        assertEquals("A", table.get("A"));
    }

    @Test
    void removeShouldDeleteCellWhenLastOccurrenceIsRemoved() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");

        assertTrue(table.remove("A"));

        assertEquals(0, table.size());
        assertEquals(0, table.occurrences("A"));
        assertNull(table.get("A"));
        assertFalse(table.contains("A"));
        assertTrue(table.isEmpty());
    }

    @Test
    void removeShouldReturnFalseForMissingKey() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        assertFalse(table.remove("A"));
        assertEquals(0, table.size());
    }

    @Test
    void clearShouldRemoveAllElements() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");
        table.put("B");
        table.put("B");

        table.clear();

        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
        assertNull(table.get("A"));
        assertNull(table.get("B"));
        assertEquals(0, table.occurrences("A"));
        assertEquals(0, table.occurrences("B"));
    }

    @Test
    void putShouldResolveCollisionWithLinearProbing() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        TestKey a = new TestKey("A", 1);
        TestKey b = new TestKey("B", 1);

        table.put(a);
        table.put(b);

        assertEquals(a, table.get(new TestKey("A", 1)));
        assertEquals(b, table.get(new TestKey("B", 1)));
        assertEquals(2, table.size());
    }

    @Test
    void putShouldReuseDeletedSlot() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        TestKey a = new TestKey("A", 1);
        TestKey b = new TestKey("B", 1);
        TestKey c = new TestKey("C", 1);

        table.put(a);
        table.put(b);
        table.remove(a);
        table.put(c);

        assertNull(table.get(new TestKey("A", 1)));
        assertEquals(b, table.get(new TestKey("B", 1)));
        assertEquals(c, table.get(new TestKey("C", 1)));
        assertEquals(2, table.size());
    }

    @Test
    void putShouldThrowWhenTableIsFull() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(2, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(new TestKey("A", 0));
        table.put(new TestKey("B", 1));

        assertThrows(UnsupportedOperationException.class,
                () -> table.put(new TestKey("C", 0)));
    }

    @Test
    void methodsShouldThrowForNullKey() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        assertThrows(IllegalArgumentException.class, () -> table.put(null));
        assertThrows(IllegalArgumentException.class, () -> table.get(null));
        assertThrows(IllegalArgumentException.class, () -> table.remove(null));
        assertThrows(IllegalArgumentException.class, () -> table.tracePut(null));
        assertThrows(IllegalArgumentException.class, () -> table.traceGet(null));
        assertThrows(IllegalArgumentException.class, () -> table.traceRemove(null));
    }

    @Test
    void tracePutShouldReturnTraceForNewElement() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> result = table.tracePut("A");

        assertTrue(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.PutStep.P1,
                        ClosedHashTable.PutStep.P2,
                        ClosedHashTable.PutStep.P3,
                        ClosedHashTable.PutStep.P6
                ),
                result.trace()
        );
    }

    @Test
    void tracePutShouldReturnTraceForDuplicateElement() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> result = table.tracePut("A");

        assertTrue(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.PutStep.P1,
                        ClosedHashTable.PutStep.P2,
                        ClosedHashTable.PutStep.P7,
                        ClosedHashTable.PutStep.P8
                ),
                result.trace()
        );
        assertEquals(1, table.size());
        assertEquals(2, table.occurrences("A"));
    }

    @Test
    void tracePutShouldReturnTraceForCollisionAndInsert() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(new TestKey("A", 1));

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> result =
                table.tracePut(new TestKey("B", 1));

        assertTrue(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.PutStep.P1,
                        ClosedHashTable.PutStep.P2,
                        ClosedHashTable.PutStep.P4,
                        ClosedHashTable.PutStep.P5,
                        ClosedHashTable.PutStep.P3,
                        ClosedHashTable.PutStep.P6
                ),
                result.trace()
        );
    }

    @Test
    void tracePutShouldReturnP9WhenTableIsFull() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(2, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(new TestKey("A", 0));
        table.put(new TestKey("B", 1));

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> result =
                table.tracePut(new TestKey("C", 0));

        assertFalse(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.PutStep.P1,
                        ClosedHashTable.PutStep.P2,
                        ClosedHashTable.PutStep.P4,
                        ClosedHashTable.PutStep.P5,
                        ClosedHashTable.PutStep.P4,
                        ClosedHashTable.PutStep.P5,
                        ClosedHashTable.PutStep.P9
                ),
                result.trace()
        );
    }

    @Test
    void traceGetShouldReturnTraceWhenElementIsFoundImmediately() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");

        ClosedHashTable.TraceResult<String, ClosedHashTable.GetStep> result = table.traceGet("A");

        assertEquals("A", result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.GetStep.G1,
                        ClosedHashTable.GetStep.G2,
                        ClosedHashTable.GetStep.G3
                ),
                result.trace()
        );
    }

    @Test
    void traceGetShouldReturnTraceWhenElementIsMissingAfterCollision() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(new TestKey("A", 1));

        ClosedHashTable.TraceResult<TestKey, ClosedHashTable.GetStep> result =
                table.traceGet(new TestKey("B", 1));

        assertNull(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.GetStep.G1,
                        ClosedHashTable.GetStep.G2,
                        ClosedHashTable.GetStep.G4,
                        ClosedHashTable.GetStep.G5
                ),
                result.trace()
        );
    }

    @Test
    void traceRemoveShouldReturnR3WhenOccurrenceCountIsGreaterThanOne() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");
        table.put("A");

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.RemoveStep> result =
                table.traceRemove("A");

        assertTrue(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.RemoveStep.R1,
                        ClosedHashTable.RemoveStep.R2,
                        ClosedHashTable.RemoveStep.R3
                ),
                result.trace()
        );
        assertEquals(1, table.occurrences("A"));
        assertEquals(1, table.size());
    }

    @Test
    void traceRemoveShouldReturnR4WhenLastOccurrenceIsRemoved() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("A");

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.RemoveStep> result =
                table.traceRemove("A");

        assertTrue(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.RemoveStep.R1,
                        ClosedHashTable.RemoveStep.R2,
                        ClosedHashTable.RemoveStep.R4
                ),
                result.trace()
        );
        assertNull(table.get("A"));
        assertEquals(0, table.size());
    }

    @Test
    void traceRemoveShouldReturnR5AndR6WhenElementIsMissing() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(new TestKey("A", 1));

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.RemoveStep> result =
                table.traceRemove(new TestKey("B", 1));

        assertFalse(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.RemoveStep.R1,
                        ClosedHashTable.RemoveStep.R5,
                        ClosedHashTable.RemoveStep.R6
                ),
                result.trace()
        );
    }

    @Test
    void quadraticProbingShouldStoreAndFindElements() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(7, ClosedHashTable.Mode.QUADRATIC_PROBING);

        TestKey a = new TestKey("A", 1);
        TestKey b = new TestKey("B", 1);

        table.put(a);
        table.put(b);

        assertEquals(a, table.get(new TestKey("A", 1)));
        assertEquals(b, table.get(new TestKey("B", 1)));
    }

    @Test
    void doubleHashingShouldStoreAndFindElements() {
        ClosedHashTable<TestKey> table =
                new ClosedHashTable<>(7, ClosedHashTable.Mode.DOUBLE_HASHING);

        TestKey a = new TestKey("A", 1);
        TestKey b = new TestKey("B", 1);

        table.put(a);
        table.put(b);

        assertEquals(a, table.get(new TestKey("A", 1)));
        assertEquals(b, table.get(new TestKey("B", 1)));
    }
}