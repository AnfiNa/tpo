import org.example.ClosedHashTable;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClosedHashTableTest {

    @Test
    void putAndGetLinear() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("a");
        table.put("b");
        table.put("c");

        assertEquals("a", table.get("a"));
        assertEquals("b", table.get("b"));
        assertEquals("c", table.get("c"));
        assertEquals(3, table.size());
    }

    @Test
    void putAndGetQuadratic() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.QUADRATIC_PROBING);

        table.put("abc");
        table.put("bca");
        table.put("cab");

        assertEquals("abc", table.get("abc"));
        assertEquals("bca", table.get("bca"));
        assertEquals("cab", table.get("cab"));
    }

    @Test
    void putAndGetDoubleHashing() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.DOUBLE_HASHING);

        table.put(10);
        table.put(39);
        table.put(68);

        assertEquals(Integer.valueOf(10), table.get(10));
        assertEquals(Integer.valueOf(39), table.get(39));
        assertEquals(Integer.valueOf(68), table.get(68));
    }

    @Test
    void duplicateKeysIncreaseOccurrences() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("a");
        table.put("a");
        table.put("a");

        assertEquals("a", table.get("a"));
        assertEquals(3, table.occurrences("a"));
        assertEquals(1, table.size());
    }

    @Test
    void removeExistingKey() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("a");
        table.put("b");
        table.put("c");

        assertTrue(table.remove("b"));
        assertNull(table.get("b"));
        assertEquals(2, table.size());
    }

    @Test
    void removeOneOccurrenceOfDuplicate() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("x");
        table.put("x");
        table.put("x");

        assertTrue(table.remove("x"));
        assertEquals(2, table.occurrences("x"));
        assertEquals("x", table.get("x"));
        assertEquals(1, table.size());
    }

    @Test
    void removeShouldNotBreakSearchChain() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(6);
        table.put(11);

        assertTrue(table.remove(6));
        assertEquals(Integer.valueOf(11), table.get(11));
    }

    @Test
    void getMissingReturnsNull() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("abc");
        table.put("def");

        assertNull(table.get("zzz"));
    }

    @Test
    void containsWorksCorrectly() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.QUADRATIC_PROBING);

        table.put("hello");

        assertTrue(table.contains("hello"));
        assertFalse(table.contains("world"));
    }

    @Test
    void clearEmptiesTable() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.DOUBLE_HASHING);

        table.put("a");
        table.put("b");
        table.clear();

        assertTrue(table.isEmpty());
        assertNull(table.get("a"));
        assertNull(table.get("b"));
    }

    @Test
    void putThrowsWhenTableIsFull() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(3, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(2);
        table.put(3);

        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> table.put(4)
        );

        assertTrue(ex.getMessage().contains("Hash table is full"));
    }

    @Test
    void nullKeyIsForbidden() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.LINEAR_PROBING);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> table.put(null)
        );

        assertTrue(ex.getMessage().contains("key must not be null"));
    }

    @Test
    void tracePutWithoutCollisions() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> result = table.tracePut(1);

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
    void tracePutThroughDeletedSlot() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(6);
        table.put(11);

        assertTrue(table.remove(6));

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> result = table.tracePut(21);

        assertTrue(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.PutStep.P1,
                        ClosedHashTable.PutStep.P2,
                        ClosedHashTable.PutStep.P4,
                        ClosedHashTable.PutStep.P5,
                        ClosedHashTable.PutStep.P4,
                        ClosedHashTable.PutStep.P5,
                        ClosedHashTable.PutStep.P4,
                        ClosedHashTable.PutStep.P5,
                        ClosedHashTable.PutStep.P3,
                        ClosedHashTable.PutStep.P6
                ),
                result.trace()
        );
    }

    @Test
    void tracePutTableIsFull() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(3, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(2);
        table.put(3);

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> result = table.tracePut(4);

        assertFalse(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.PutStep.P1,
                        ClosedHashTable.PutStep.P2,
                        ClosedHashTable.PutStep.P4,
                        ClosedHashTable.PutStep.P5,
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
    void tracePutForDuplicateKey() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> first = table.tracePut(1);
        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.PutStep> second = table.tracePut(1);

        assertEquals(
                List.of(
                        ClosedHashTable.PutStep.P1,
                        ClosedHashTable.PutStep.P2,
                        ClosedHashTable.PutStep.P3,
                        ClosedHashTable.PutStep.P6
                ),
                first.trace()
        );

        assertEquals(
                List.of(
                        ClosedHashTable.PutStep.P1,
                        ClosedHashTable.PutStep.P2,
                        ClosedHashTable.PutStep.P7,
                        ClosedHashTable.PutStep.P8
                ),
                second.trace()
        );
    }

    @Test
    void traceGetSuccessfulWithoutCollisions() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);

        ClosedHashTable.TraceResult<Integer, ClosedHashTable.GetStep> result = table.traceGet(1);

        assertEquals(Integer.valueOf(1), result.value());
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
    void traceGetMissingStopsOnNull() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);

        ClosedHashTable.TraceResult<Integer, ClosedHashTable.GetStep> result = table.traceGet(2);

        assertNull(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.GetStep.G1,
                        ClosedHashTable.GetStep.G2,
                        ClosedHashTable.GetStep.G5
                ),
                result.trace()
        );
    }

    @Test
    void traceGetThroughDeletedAndCollisions() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(6);
        table.put(11);
        assertTrue(table.remove(1));

        ClosedHashTable.TraceResult<Integer, ClosedHashTable.GetStep> result = table.traceGet(11);

        assertEquals(Integer.valueOf(11), result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.GetStep.G1,
                        ClosedHashTable.GetStep.G2,
                        ClosedHashTable.GetStep.G4,
                        ClosedHashTable.GetStep.G4,
                        ClosedHashTable.GetStep.G3
                ),
                result.trace()
        );
    }

    @Test
    void traceRemoveSingleOccurrence() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.RemoveStep> result = table.traceRemove(1);

        assertTrue(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.RemoveStep.R1,
                        ClosedHashTable.RemoveStep.R2,
                        ClosedHashTable.RemoveStep.R4
                ),
                result.trace()
        );
    }

    @Test
    void traceRemoveFromDuplicatesDecrementsCount() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(1);
        table.put(1);

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.RemoveStep> result = table.traceRemove(1);

        assertTrue(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.RemoveStep.R1,
                        ClosedHashTable.RemoveStep.R2,
                        ClosedHashTable.RemoveStep.R3
                ),
                result.trace()
        );
    }

    @Test
    void traceRemoveMissingOnEmptyTable() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.RemoveStep> result = table.traceRemove(10);

        assertFalse(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.RemoveStep.R1,
                        ClosedHashTable.RemoveStep.R6
                ),
                result.trace()
        );
    }

    @Test
    void traceRemoveMissingAfterCollisions() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(6);
        table.put(11);

        ClosedHashTable.TraceResult<Boolean, ClosedHashTable.RemoveStep> result = table.traceRemove(21);

        assertFalse(result.value());
        assertEquals(
                List.of(
                        ClosedHashTable.RemoveStep.R1,
                        ClosedHashTable.RemoveStep.R5,
                        ClosedHashTable.RemoveStep.R5,
                        ClosedHashTable.RemoveStep.R5,
                        ClosedHashTable.RemoveStep.R6
                ),
                result.trace()
        );
    }
}