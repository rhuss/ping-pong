package jax2016.ping;

enum Stroke {
    HIT ,
    MISSED {
        String getDescription(String looser) {
            return looser + " missed the ball";
        }
    },
    OUT {
        String getDescription(String looser) {
            return looser + " hit the ball out";
        }
    };

    static Stroke play(int strength) {
        if (strength * Math.random() > 0.5) {
            return HIT;
        };
        return strength * Math.random() > 0.5 ? OUT : MISSED;
    }

    String getDescription(String looser) { return null; };
}
