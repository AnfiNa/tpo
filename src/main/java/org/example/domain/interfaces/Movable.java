package org.example.domain.interfaces;

public interface Movable {
    void move(String direction, double distance);
    void stop();
    void changeDirection(String direction);
    void accelerate(double value);
}
