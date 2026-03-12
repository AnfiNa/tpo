package domain.model;

import org.example.domain.enums.Habitat;
import org.example.domain.enums.LifeState;
import domain.utils.ConsoleOutputCaptor;
import org.example.domain.model.SpermWhale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpermWhaleTest {

    private SpermWhale whale;
    private ConsoleOutputCaptor outputCaptor;

    @BeforeEach
    void setUp() {
        whale = new SpermWhale("Arthur", 12.5);
        outputCaptor = new ConsoleOutputCaptor();
    }

    @Test
    void constructorShouldInitializeNameSpeedHabitatAndAliveState() {
        assertEquals("Arthur", whale.getName());
        assertEquals(12.5, whale.getSpeed());
        assertEquals(Habitat.OCEAN, whale.getHabitat());
        assertEquals(LifeState.ALIVE, whale.getLifeState());
        assertTrue(whale.isAlive());
    }

    @Test
    void dieShouldChangeStateToDead() {
        whale.die();

        assertEquals(LifeState.DEAD, whale.getLifeState());
        assertFalse(whale.isAlive());
    }

    @Test
    void reviveShouldChangeStateBackToAlive() {
        whale.die();
        whale.revive();

        assertEquals(LifeState.ALIVE, whale.getLifeState());
        assertTrue(whale.isAlive());
    }

    @Test
    void setHabitatShouldChangeHabitat() {
        whale.setHabitat(Habitat.SPACE);

        assertEquals(Habitat.SPACE, whale.getHabitat());
    }

    @Test
    void thinkShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.think("Where am I?"));

        assertEquals("Arthur thinks: Where am I?\n", output);
    }

    @Test
    void thinkShouldPrintDeadMessageWhenDead() {
        whale.die();

        String output = outputCaptor.capture(() -> whale.think("Where am I?"));

        assertEquals("Arthur cannot think because it is dead.\n", output);
    }

    @Test
    void rememberShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.remember("the ocean"));

        assertEquals("Arthur remembers: the ocean\n", output);
    }

    @Test
    void imagineShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.imagine("a deep blue sea"));

        assertEquals("Arthur imagines: a deep blue sea\n", output);
    }

    @Test
    void makeDecisionShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.makeDecision("move forward"));

        assertEquals("Arthur decides to: move forward\n", output);
    }

    @Test
    void watchShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.watch("the sky"));

        assertEquals("Arthur watches: the sky\n", output);
    }

    @Test
    void observeShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.observe("the horizon"));

        assertEquals("Arthur observes: the horizon\n", output);
    }

    @Test
    void lookAroundShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(whale::lookAround);

        assertEquals("Arthur looks around carefully.\n", output);
    }

    @Test
    void noticeMovementShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.noticeMovement("the ground"));

        assertEquals("Arthur notices movement near: the ground\n", output);
    }

    @Test
    void hearShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.hear("wind"));

        assertEquals("Arthur hears: wind\n", output);
    }

    @Test
    void listenToShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.listenTo("the wind"));

        assertEquals("Arthur listens to: the wind\n", output);
    }

    @Test
    void recognizeSoundShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.recognizeSound("falling air"));

        assertEquals("Arthur recognizes the sound: falling air\n", output);
    }

    @Test
    void reactToSoundShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.reactToSound("thunder"));

        assertEquals("Arthur reacts to the sound: thunder\n", output);
    }

    @Test
    void feelShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.feel("fear"));

        assertEquals("Arthur feels: fear\n", output);
    }

    @Test
    void sensePainShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(whale::sensePain);

        assertEquals("Arthur senses pain.\n", output);
    }

    @Test
    void senseTemperatureShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.senseTemperature("cold"));

        assertEquals("Arthur senses temperature: cold\n", output);
    }

    @Test
    void expressEmotionShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.expressEmotion("panic"));

        assertEquals("Arthur expresses emotion: panic\n", output);
    }

    @Test
    void moveShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.move("downward", 5000));

        assertEquals("Arthur moves downward for 5000.0 meters at speed 12.5 m/s.\n", output);
    }

    @Test
    void moveShouldPrintDeadMessageWhenDead() {
        whale.die();

        String output = outputCaptor.capture(() -> whale.move("forward", 100));

        assertEquals("Arthur cannot move because it is dead.\n", output);
    }

    @Test
    void stopShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(whale::stop);

        assertEquals("Arthur stops moving.\n", output);
    }

    @Test
    void changeDirectionShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.changeDirection("upward"));

        assertEquals("Arthur changes direction to: upward\n", output);
    }

    @Test
    void accelerateShouldIncreaseSpeed() {
        whale.accelerate(2.5);

        assertEquals(15.0, whale.getSpeed());
    }

    @Test
    void accelerateShouldNotAllowNegativeSpeed() {
        whale.accelerate(-20.0);

        assertEquals(0.0, whale.getSpeed());
    }

    @Test
    void accelerateShouldPrintNewSpeed() {
        String output = outputCaptor.capture(() -> whale.accelerate(1.5));

        assertEquals("Arthur accelerates. New speed: 14.0 m/s.\n", output);
    }

    @Test
    void diveShouldPrintMessageWhenAlive() {
        String output = outputCaptor.capture(() -> whale.dive(300));

        assertEquals("Arthur dives to a depth of 300.0 meters.\n", output);
    }

    @Test
    void diveShouldPrintDeadMessageWhenDead() {
        whale.die();

        String output = outputCaptor.capture(() -> whale.dive(300));

        assertEquals("Arthur cannot dive because it is dead.\n", output);
    }

    @Test
    void allPerceptionActionsShouldBeBlockedWhenDead() {
        whale.die();

        String thinkOutput = outputCaptor.capture(() -> whale.think("test"));
        String watchOutput = outputCaptor.capture(() -> whale.watch("test"));
        String hearOutput = outputCaptor.capture(() -> whale.hear("test"));
        String feelOutput = outputCaptor.capture(() -> whale.feel("test"));

        assertEquals("Arthur cannot think because it is dead.\n", thinkOutput);
        assertEquals("Arthur cannot watch because it is dead.\n", watchOutput);
        assertEquals("Arthur cannot hear because it is dead.\n", hearOutput);
        assertEquals("Arthur cannot feel because it is dead.\n", feelOutput);
    }
}