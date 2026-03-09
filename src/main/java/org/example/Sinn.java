package org.example;


public class Sinn {

    public static double sin(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return Double.NaN;
        }

        final double PI = Math.PI;
        double twoPi = 2 * PI;
        x = x % twoPi;
        if (x > PI) {
            x -= twoPi;
        } else if (x < -PI) {
            x += twoPi;
        }

        double sum = 0.0;
        double term = x;
        int n = 0;
        final double EPS = 1e-12;
        final int MAX_ITER = 200;

        while (n < MAX_ITER && Math.abs(term) > EPS) {
            sum += term;
            n++;
            term *= -x * x / ((2.0 * n) * (2.0 * n + 1.0));
        }
        return sum;
    }
}
