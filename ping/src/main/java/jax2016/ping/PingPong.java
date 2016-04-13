package jax2016.ping;

enum PingPong {
    PING, PONG;

    PingPong theOther() {
        return this == PING ? PONG : PING;
    }
}
