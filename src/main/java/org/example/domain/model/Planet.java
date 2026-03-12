package org.example.domain.model;

import org.example.domain.enums.PlanetType;

public class Planet extends ObjectItem {
    private PlanetType type;

    public Planet(String name, PlanetType type) {
        super(name);
        this.type = type;
    }

    public PlanetType getType() {
        return type;
    }


    public void describePlanet() {
        System.out.println("Planet: " + name);
        System.out.println("Type: " + type);
    }

    public class Wind {
        private String direction;
        private double speed;

        public Wind(String direction, double speed) {
            this.direction = direction;
            this.speed = speed;
        }

        public void blow() {
            System.out.println("Wind blows on planet " + name
                    + ". Direction: " + direction
                    + ", speed: " + speed + " m/s.");
        }
    }

}