package org.example.domain.model;

import org.example.domain.enums.Habitat;
import org.example.domain.interfaces.Movable;

public class SpermWhale extends Creature implements Movable {
    private double speed;
    private Habitat habitat;

    public SpermWhale(String name, double speed) {
        super(name);
        this.speed = speed;
        this.habitat = Habitat.OCEAN;
    }

    public double getSpeed() {
        return speed;
    }

    public Habitat getHabitat() {
        return habitat;
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }

    @Override
    public void move(String direction, double distance) {
        if (canAct("move")) {
            System.out.println(name + " moves " + direction + " for " + distance
                    + " meters at speed " + speed + " m/s.");
        }
    }

    @Override
    public void stop() {
        if (canAct("stop")) {
            System.out.println(name + " stops moving.");
        }
    }

    @Override
    public void changeDirection(String direction) {
        if (canAct("change direction")) {
            System.out.println(name + " changes direction to: " + direction);
        }
    }

    @Override
    public void accelerate(double value) {
        if (canAct("accelerate")) {
            speed += value;
            if (speed < 0) {
                speed = 0;
            }
            System.out.println(name + " accelerates. New speed: " + speed + " m/s.");
        }
    }

    public void dive(double depth) {
        if (canAct("dive")) {
            System.out.println(name + " dives to a depth of " + depth + " meters.");
        }
    }
}