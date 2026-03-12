package org.example.domain.model;


import org.example.domain.enums.LifeState;
import org.example.domain.interfaces.Feelable;
import org.example.domain.interfaces.Hearable;
import org.example.domain.interfaces.Thinkable;
import org.example.domain.interfaces.Watchable;

public abstract class Creature implements Thinkable, Watchable, Hearable, Feelable {
    protected String name;
    protected LifeState lifeState;

    public Creature(String name) {
        this.name = name;
        this.lifeState = LifeState.ALIVE;
    }

    public String getName() {
        return name;
    }

    public LifeState getLifeState() {
        return lifeState;
    }

    public boolean isAlive() {
        return lifeState == LifeState.ALIVE;
    }

    public void die() {
        lifeState = LifeState.DEAD;
        System.out.println(name + " is now dead.");
    }

    public void revive() {
        lifeState = LifeState.ALIVE;
        System.out.println(name + " is alive again.");
    }

    protected boolean canAct(String action) {
        if (!isAlive()) {
            System.out.println(name + " cannot " + action + " because it is dead.");
            return false;
        }
        return true;
    }

    @Override
    public void think(String thought) {
        if (canAct("think")) {
            System.out.println(name + " thinks: " + thought);
        }
    }

    @Override
    public void remember(String memory) {
        if (canAct("remember")) {
            System.out.println(name + " remembers: " + memory);
        }
    }

    @Override
    public void imagine(String image) {
        if (canAct("imagine")) {
            System.out.println(name + " imagines: " + image);
        }
    }

    @Override
    public void makeDecision(String option) {
        if (canAct("make decisions")) {
            System.out.println(name + " decides to: " + option);
        }
    }

    @Override
    public void watch(String target) {
        if (canAct("watch")) {
            System.out.println(name + " watches: " + target);
        }
    }

    @Override
    public void observe(String target) {
        if (canAct("observe")) {
            System.out.println(name + " observes: " + target);
        }
    }

    @Override
    public void lookAround() {
        if (canAct("look around")) {
            System.out.println(name + " looks around carefully.");
        }
    }

    @Override
    public void noticeMovement(String target) {
        if (canAct("notice movement")) {
            System.out.println(name + " notices movement near: " + target);
        }
    }

    @Override
    public void hear(String sound) {
        if (canAct("hear")) {
            System.out.println(name + " hears: " + sound);
        }
    }

    @Override
    public void listenTo(String source) {
        if (canAct("listen")) {
            System.out.println(name + " listens to: " + source);
        }
    }

    @Override
    public void recognizeSound(String sound) {
        if (canAct("recognize sounds")) {
            System.out.println(name + " recognizes the sound: " + sound);
        }
    }

    @Override
    public void reactToSound(String sound) {
        if (canAct("react to sounds")) {
            System.out.println(name + " reacts to the sound: " + sound);
        }
    }

    @Override
    public void feel(String feeling) {
        if (canAct("feel")) {
            System.out.println(name + " feels: " + feeling);
        }
    }

    @Override
    public void sensePain() {
        if (canAct("sense pain")) {
            System.out.println(name + " senses pain.");
        }
    }

    @Override
    public void senseTemperature(String level) {
        if (canAct("sense temperature")) {
            System.out.println(name + " senses temperature: " + level);
        }
    }

    @Override
    public void expressEmotion(String emotion) {
        if (canAct("express emotions")) {
            System.out.println(name + " expresses emotion: " + emotion);
        }
    }
}