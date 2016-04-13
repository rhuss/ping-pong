package jax2016.pong;

public enum Event {
    PONG,
    MISSED,
    OUT;

    static Event play(int strength) {
        if (strength * Math.random() > 0.5) {
            return PONG;
        };
        return Math.random() > 0.5 ? MISSED : OUT;
    }
}
