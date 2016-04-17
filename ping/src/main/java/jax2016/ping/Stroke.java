package jax2016.ping;

enum Stroke {
    HIT,
    MISSED;

    // very simplistic gameplay
    static Stroke play(int strength) {
        return strength * Math.random() > 0.5 ? HIT : MISSED;
    }
}
