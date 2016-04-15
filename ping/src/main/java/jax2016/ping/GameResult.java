package jax2016.ping;

class GameResult {

    private final String id;
    private final String reason;
    private final int strokes;
    private final String winner;
    private final Stroke stroke;

    GameResult(String id, int strokes, String winner, String looser, Stroke stroke) {
        this.id = id;
        this.strokes = strokes;
        this.winner = winner;
        this.stroke = stroke;
        this.reason = stroke.getDescription(looser);
    }

    @Override
    public String toString() {
        return "GameResult{" +
               "id=" + id +
               ", strokes=" + strokes +
               ", winner=" + winner +
               ", stroke=" + stroke +
               ", reason='" + reason + '\'' +
               '}';
    }
}
