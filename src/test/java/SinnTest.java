import org.example.Sinn;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import static java.lang.Math.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SinnTest {

    private static final double DEFAULT_DELTA = 1e-12;

    @Test
    public void testZero() {
        assertEquals(0.0, Sinn.sin(0.0), DEFAULT_DELTA);
    }

    @Test
    public void testPi() {
        assertEquals(0.0, Sinn.sin(PI), DEFAULT_DELTA);
    }

    @Test
    public void testHalfPi() {
        assertEquals(sin(PI / 2), Sinn.sin(PI / 2), DEFAULT_DELTA);
    }

    @Test
    public void testNegative() {
        double[] values = {0.1, 0.5, 1.0, 2.0, 3.0};
        for (double x : values) {
            assertEquals(Sinn.sin(-x), -Sinn.sin(x), DEFAULT_DELTA);
        }
    }

    @Test
    public void testLarge() {
        double x = 100000000.0;
        assertEquals(Sinn.sin(x), sin(x), 1e-8);
    }

    @Test
    public void testNegativeLarge() {
        double x = -2000000.0;
        assertEquals(Sinn.sin(x), sin(x), 1e-8);
    }

    @Test
    public void testVerySmall() {
        double x = 1e-10;
        assertEquals(x, Sinn.sin(x), 1e-20);
    }

    @Test
    public void testNaN() {
        assertTrue(Double.isNaN(Sinn.sin(Double.NaN)));
    }

    @Test
    public void testInfinity() {
        assertTrue(Double.isNaN(Sinn.sin(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(Sinn.sin(Double.NEGATIVE_INFINITY)));
    }

    @Test
    public void testPeriodicity() {
        double x = PI / 6;
        double twoPi = 2 * PI;
        assertEquals(Sinn.sin(x), Sinn.sin(x + twoPi), DEFAULT_DELTA);
        assertEquals(Sinn.sin(x), Sinn.sin(x - twoPi), DEFAULT_DELTA);
    }

    @Test
    public void testSymmetry() {
        double[] values = {0.1, 0.5, 1.0, 2.0, 3.0};
        for (double x : values) {
            assertEquals(Sinn.sin(x), -Sinn.sin(-x), DEFAULT_DELTA);
        }
    }

    @Test
    public void testKnownValues() {
        double[] angles = {
                0, PI / 6, PI / 4, PI / 3, PI / 2,
                2 * PI / 3, 3 * PI / 4, 5 * PI / 6, PI
        };
        double[] expected = {
                0, 0.5, sqrt(2) / 2, sqrt(3) / 2, 1,
                sqrt(3) / 2, sqrt(2) / 2, 0.5, 0
        };
        for (int i = 0; i < angles.length; i++) {
            assertEquals(expected[i], Sinn.sin(angles[i]), DEFAULT_DELTA);
        }
    }
}

