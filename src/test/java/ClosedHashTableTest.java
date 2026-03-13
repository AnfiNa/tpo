import org.example.ClosedHashTable;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClosedHashTableTest {

    @Test
    public void putAndGetLinear() {
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
    public void putAndGetQuadratic() {
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
    public void putAndGetDoubleHashing() {
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
    public void duplicateKeysIncreaseOccurrences() {
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
    public void removeExistingKey() {
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
    public void removeOneOccurrenceOfDuplicate() {
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
    public void removeShouldNotBreakSearchChain() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(6);
        table.put(11);

        assertTrue(table.remove(6));
        assertEquals(Integer.valueOf(11), table.get(11));
    }

    @Test
    public void getMissingReturnsNull() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put("abc");
        table.put("def");

        assertNull(table.get("zzz"));
    }

    @Test
    public void containsWorksCorrectly() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.QUADRATIC_PROBING);

        table.put("hello");

        assertTrue(table.contains("hello"));
        assertFalse(table.contains("world"));
    }

    @Test
    public void clearEmptiesTable() {
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
    public void putThrowsWhenTableIsFull() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(3, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(2);
        table.put(3);

        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                new org.junit.function.ThrowingRunnable() {
                    @Override
                    public void run() {
                        table.put(4);
                    }
                }
        );

        assertTrue(ex.getMessage().contains("Hash table is full"));
    }

    @Test
    public void nullKeyIsForbidden() {
        ClosedHashTable<String> table =
                new ClosedHashTable<>(29, ClosedHashTable.Mode.LINEAR_PROBING);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                new org.junit.function.ThrowingRunnable() {
                    @Override
                    public void run() {
                        table.put(null);
                    }
                }
        );

        assertTrue(ex.getMessage().contains("key must not be null"));
    }

    @Test
    public void tracePutWithoutCollisions() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        ClosedHashTable.TraceResult<Boolean> result = table.tracePut(1);

        assertTrue(result.getValue());
        assertEquals("P1 -> P2 -> P3 -> P6", result.getTrace());
    }

    @Test
    public void tracePutThroughDeletedSlot() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        // 1, 6, 11 коллидируют при capacity = 5
        table.put(1);
        table.put(6);
        table.put(11);

        // удаляем элемент из середины цепочки
        assertTrue(table.remove(6));

        ClosedHashTable.TraceResult<Boolean> result = table.tracePut(21);

        assertTrue(result.getValue());
        assertEquals("P1 -> P2 -> P4 -> P5 -> P4 -> P5 -> P4 -> P5 -> P3 -> P6", result.getTrace());
    }

    @Test
    public void tracePutTableIsFull() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(3, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(2);
        table.put(3);

        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                new org.junit.function.ThrowingRunnable() {
                    @Override
                    public void run() {
                        table.tracePut(4);
                    }
                }
        );

        // при полной таблице нет ни null, ни DELETED
        assertTrue(ex.getMessage().contains("Hash table is full"));
    }

    @Test
    public void tracePutForDuplicateKey() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        ClosedHashTable.TraceResult<Boolean> first = table.tracePut(1);
        ClosedHashTable.TraceResult<Boolean> second = table.tracePut(1);

        assertEquals("P1 -> P2 -> P3 -> P6", first.getTrace());
        assertEquals("P1 -> P2 -> P7 -> P8", second.getTrace());
    }

    @Test
    public void traceGetSuccessfulWithoutCollisions() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);

        ClosedHashTable.TraceResult<Integer> result = table.traceGet(1);

        assertEquals(Integer.valueOf(1), result.getValue());
        assertEquals("G1 -> G2 -> G3", result.getTrace());
    }

    @Test
    public void traceGetMissingStopsOnNull() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);

        ClosedHashTable.TraceResult<Integer> result = table.traceGet(2);

        assertNull(result.getValue());
        assertEquals("G1 -> G2 -> G5", result.getTrace());
    }

    @Test
    public void traceGetThroughDeletedAndCollisions() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(6);
        table.put(11);
        assertTrue(table.remove(1)); // помечаем первую ячейку как DELETED

        ClosedHashTable.TraceResult<Integer> result = table.traceGet(11);

        assertEquals(Integer.valueOf(11), result.getValue());
        assertEquals("G1 -> G2 -> G4 -> G4 -> G3", result.getTrace());
    }

    @Test
    public void traceRemoveSingleOccurrence() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);

        ClosedHashTable.TraceResult<Boolean> result = table.traceRemove(1);

        assertTrue(result.getValue());
        assertEquals("R1 -> R2 -> R4", result.getTrace());
    }

    @Test
    public void traceRemoveFromDuplicatesDecrementsCount() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(1);
        table.put(1);

        ClosedHashTable.TraceResult<Boolean> result = table.traceRemove(1);

        assertTrue(result.getValue());
        assertEquals("R1 -> R2 -> R3", result.getTrace());
    }

    @Test
    public void traceRemoveMissingOnEmptyTable() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        ClosedHashTable.TraceResult<Boolean> result = table.traceRemove(10);

        assertFalse(result.getValue());
        assertEquals("R1 -> R6", result.getTrace());
    }

    @Test
    public void traceRemoveMissingAfterCollisions() {
        ClosedHashTable<Integer> table =
                new ClosedHashTable<>(5, ClosedHashTable.Mode.LINEAR_PROBING);

        table.put(1);
        table.put(6);
        table.put(11);

        ClosedHashTable.TraceResult<Boolean> result = table.traceRemove(21);

        assertFalse(result.getValue());
        assertEquals("R1 -> R5 -> R5 -> R5 -> R6", result.getTrace());
    }
}