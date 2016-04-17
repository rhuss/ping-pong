package jax2016.pong;

enum Stroke {
    HIT,
    MISSED;

    // very simplistic gameplay
    static Stroke play(int strength) {
        return strength * Math.random() > 0.5 ? HIT : MISSED;
    }
}
