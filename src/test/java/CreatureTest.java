import org.example.domain.enums.LifeState;
import org.example.domain.model.Creature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CreatureTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    private static class TestCreature extends Creature {
        TestCreature(String name) {
            super(name);
        }
    }

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
    void constructorShouldCreateAliveCreature() {
        TestCreature creature = new TestCreature("Whale");

        assertEquals("Whale", creature.getName());
        assertEquals(LifeState.ALIVE, creature.getLifeState());
        assertTrue(creature.isAlive());
    }

    @Test
    void dieAndReviveShouldChangeState() {
        TestCreature creature = new TestCreature("Whale");

        creature.die();
        assertEquals(LifeState.DEAD, creature.getLifeState());
        assertFalse(creature.isAlive());
        assertEquals("Whale is now dead.", getOutput());

        clearOutput();
        creature.revive();

        assertEquals(LifeState.ALIVE, creature.getLifeState());
        assertTrue(creature.isAlive());
        assertEquals("Whale is alive again.", getOutput());
    }

    @Test
    void aliveCreatureShouldPerformAllActions() {
        TestCreature creature = new TestCreature("Whale");

        creature.think("I am falling");
        creature.remember("the ocean");
        creature.imagine("clouds");
        creature.makeDecision("swim down");

        creature.watch("the sky");
        creature.observe("the ground");
        creature.lookAround();
        creature.noticeMovement("below");

        creature.hear("a scream");
        creature.listenTo("the wind");
        creature.recognizeSound("thunder");
        creature.reactToSound("explosion");

        creature.feel("fear");
        creature.sensePain();
        creature.senseTemperature("cold");
        creature.expressEmotion("panic");

        assertEquals("""
                Whale thinks: I am falling
                Whale remembers: the ocean
                Whale imagines: clouds
                Whale decides to: swim down
                Whale watches: the sky
                Whale observes: the ground
                Whale looks around carefully.
                Whale notices movement near: below
                Whale hears: a scream
                Whale listens to: the wind
                Whale recognizes the sound: thunder
                Whale reacts to the sound: explosion
                Whale feels: fear
                Whale senses pain.
                Whale senses temperature: cold
                Whale expresses emotion: panic
                """.trim(), getOutput());
    }

    @Test
    void deadCreatureShouldNotBeAbleToAct() {
        TestCreature creature = new TestCreature("Whale");
        creature.die();
        clearOutput();

        creature.think("I am falling");

        assertEquals("Whale cannot think because it is dead.", getOutput());
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