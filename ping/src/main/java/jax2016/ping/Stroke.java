package jax2016.ping;

enum Stroke {
    HIT {
        String getReason(PingPong who) {
            return who + " hit the ball";
        }
    },
    MISSED {
        String getReason(PingPong who) {
            return who + " missed the ball";
        }
    },
    OUT {
        String getReason(PingPong who) {
            return who + " hit the ball out";
        }
    };

    static Stroke play(int strength) {
        if (strength * Math.random() > 0.5) {
            return HIT;
        };
        return strength * Math.random() > 0.5 ? OUT : MISSED;
    }

    abstract String getReason(PingPong who);
}
