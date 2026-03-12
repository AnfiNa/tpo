package domain.model;

import org.example.domain.enums.PlanetType;
import domain.utils.ConsoleOutputCaptor;
import org.example.domain.model.Planet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanetTest {

    private Planet planet;
    private ConsoleOutputCaptor outputCaptor;

    @BeforeEach
    void setUp() {
        planet = new Planet("Earth", PlanetType.ROCKY);
        outputCaptor = new ConsoleOutputCaptor();
    }

    @Test
    void constructorShouldInitializePlanetFields() {
        assertEquals("Earth", planet.getName());
        assertEquals(PlanetType.ROCKY, planet.getType());
    }

    @Test
    void getTypeShouldReturnPlanetType() {
        assertEquals(PlanetType.ROCKY, planet.getType());
    }

    @Test
    void describePlanetShouldPrintPlanetData() {
        String output = outputCaptor.capture(planet::describePlanet);

        String expected = ""
                + "Planet: Earth\n"
                + "Type: ROCKY\n";

        assertEquals(expected, output);
    }

    @Test
    void windShouldPrintWindData() {
        Planet.Wind wind = planet.new Wind("downward", 42.0);

        String output = outputCaptor.capture(wind::blow);

        assertEquals(
                "Wind blows on planet Earth. Direction: downward, speed: 42.0 m/s.\n",
                output
        );
    }

    @Test
    void windShouldBeBoundToConcretePlanetInstance() {
        Planet secondPlanet = new Planet("Mars", PlanetType.DESERT);

        Planet.Wind earthWind = planet.new Wind("east", 10.0);
        Planet.Wind marsWind = secondPlanet.new Wind("west", 20.0);

        String earthOutput = outputCaptor.capture(earthWind::blow);
        String marsOutput = outputCaptor.capture(marsWind::blow);

        assertTrue(earthOutput.contains("planet Earth"));
        assertTrue(marsOutput.contains("planet Mars"));
    }
}