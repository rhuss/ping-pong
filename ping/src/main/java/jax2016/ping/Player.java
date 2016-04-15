package jax2016.ping;

class Player {

    static final String PING = "PING";

    private String player;
    private String opponent;

    Player(String player, String opponent) {
        this.player = player.toUpperCase();
        this.opponent = opponent.toUpperCase();
    }

    String getPlayer() {
        return player;
    }

    String getOpponent() {
        return opponent;
    }
}
