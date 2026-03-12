package org.example.domain;

import org.example.domain.enums.Habitat;
import org.example.domain.enums.PlanetType;
import org.example.domain.model.Planet;
import org.example.domain.model.SpermWhale;

public class Main {
    public static void main(String[] args) {

        Planet planet = new Planet("Earth", PlanetType.OCEANIC);
        Planet.Wind wind = planet.new Wind("downward", 18.7);

        SpermWhale whale = new SpermWhale("The Whale", 0.0);
        whale.setHabitat(Habitat.SPACE);

        System.out.println("This was not a natural position for a whale to be.");
        System.out.println();

        planet.describePlanet();
        wind.blow();
        System.out.println();

        whale.lookAround();
        System.out.println();

        System.out.println("The unfortunate creature had only a very small amount of time");
        System.out.println("to become used to the idea that it was a whale.");
        System.out.println();

        whale.think("I seem to be a whale.");
        whale.remember("nothing at all");
        whale.makeDecision("to understand what is happening");
        System.out.println();

        whale.noticeMovement("the rapidly approaching ground");
        whale.listenTo("the wind");
        whale.recognizeSound("the terrible whistle of falling");
        whale.reactToSound("the air roaring around it");
        whale.feel("alarm");
        whale.expressEmotion("panic");
        System.out.println();

        System.out.println("But before it could fully accept that fact,");
        System.out.println("it was already very close to another realization.");
        System.out.println();

        whale.die();

        System.out.println("And so, almost immediately after discovering that it was a whale,");
        System.out.println("it had to face the fact that it was no longer one.");
    }
}
