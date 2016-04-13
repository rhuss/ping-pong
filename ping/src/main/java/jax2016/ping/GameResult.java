package jax2016.ping;

class GameResult {

    private final String reason;
    private final int strokes;
    private final PingPong winner;
    private final Stroke stroke;

    GameResult(int strokes, PingPong winner, Stroke stroke) {
        this.strokes = strokes;
        this.winner = winner;
        this.stroke = stroke;
        this.reason = stroke.getReason(winner.theOther());
    }

    @Override
    public String toString() {
        return "GameResult{" +
               "strokes=" + strokes +
               ", winner=" + winner +
               ", stroke=" + stroke +
               ", reason='" + reason + '\'' +
               '}';
    }
}
