package jax2016;

enum Stroke {
    HIT,
    MISSED,
    OUT;

    // very simplistic gameplay
    static Stroke play(int strength) {
        if (strength * Math.random() > 0.5) {
            return HIT;
        };
        return strength * Math.random() > 0.5 ? OUT : MISSED;
    }
}
