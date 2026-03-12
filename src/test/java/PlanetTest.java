import org.example.domain.enums.PlanetType;
import org.example.domain.model.Planet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlanetTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void constructorShouldSetNameAndType() {
        Planet planet = new Planet("Earth", PlanetType.TERRESTRIAL);

        assertEquals("Earth", planet.getName());
        assertEquals(PlanetType.TERRESTRIAL, planet.getType());
    }

    @Test
    void describePlanetShouldPrintInfo() {
        Planet planet = new Planet("Mars", PlanetType.TERRESTRIAL);

        planet.describePlanet();

        assertEquals("""
                Planet: Mars
                Type: TERRESTRIAL
                """.trim(), getOutput());
    }

    @Test
    void windShouldBlow() {
        Planet planet = new Planet("Neptune", PlanetType.GAS_GIANT);
        Planet.Wind wind = planet.new Wind("north", 12.5);

        wind.blow();

        assertEquals(
                "Wind blows on planet Neptune. Direction: north, speed: 12.5 m/s.",
                getOutput()
        );
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n")
                .trim();
    }
}