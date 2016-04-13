package jax2016.pong;

enum Stroke {
    HIT,
    MISSED,
    OUT;

    static Stroke play(int strength) {
        if (strength * Math.random() > 0.5) {
            return HIT;
        };
        return Math.random() > 0.5 ? MISSED : OUT;
    }
}
