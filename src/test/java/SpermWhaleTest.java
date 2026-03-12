import org.example.domain.enums.Habitat;
import org.example.domain.model.SpermWhale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpermWhaleTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() throws Exception {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void constructorShouldSetDefaultValues() {
        SpermWhale whale = new SpermWhale("Willy", 12.5);

        assertEquals("Willy", whale.getName());
        assertEquals(12.5, whale.getSpeed(), 0.0001);
        assertEquals(Habitat.OCEAN, whale.getHabitat());
        assertTrue(whale.isAlive());
    }

    @Test
    void habitatShouldBeChangeable() {
        SpermWhale whale = new SpermWhale("Willy", 10.0);

        whale.setHabitat(Habitat.SPACE);

        assertEquals(Habitat.SPACE, whale.getHabitat());
    }

    @Test
    void accelerateShouldChangeSpeed() {
        SpermWhale whale = new SpermWhale("Willy", 10.0);

        whale.accelerate(5.0);

        assertEquals(15.0, whale.getSpeed(), 0.0001);
        assertEquals("Willy accelerates. New speed: 15.0 m/s.", getOutput());
    }

    @Test
    void accelerateShouldNotMakeSpeedNegative() {
        SpermWhale whale = new SpermWhale("Willy", 4.0);

        whale.accelerate(-10.0);

        assertEquals(0.0, whale.getSpeed(), 0.0001);
        assertEquals("Willy accelerates. New speed: 0.0 m/s.", getOutput());
    }

    @Test
    void whaleShouldMoveStopChangeDirectionAndDive() {
        SpermWhale whale = new SpermWhale("Willy", 8.0);

        whale.move("forward", 100.0);
        whale.stop();
        whale.changeDirection("left");
        whale.dive(250.0);

        assertEquals("""
                Willy moves forward for 100.0 meters at speed 8.0 m/s.
                Willy stops moving.
                Willy changes direction to: left
                Willy dives to a depth of 250.0 meters.
                """.trim(), getOutput());
    }

    @Test
    void deadWhaleShouldNotBeAbleToAct() {
        SpermWhale whale = new SpermWhale("Willy", 8.0);
        whale.die();
        clearOutput();

        whale.move("forward", 100.0);

        assertEquals("Willy cannot move because it is dead.", getOutput());
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n")
                .trim();
    }

    private void clearOutput() {
        output.reset();
    }
}