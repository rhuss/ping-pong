package ping;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

class GameResult {

    private final String id;
    private final int strokes;
    private final String winner;
    private final String looser;
    private final String opponentId;

    GameResult(String id, String opponentId, int strokes,
               String winner, String looser) {
        this.id = id;
        this.opponentId = opponentId;
        this.strokes = strokes;
        this.winner = winner;
        this.looser = looser;
    }

    @Override
    public String toString() {
        return "[ " +
               AnsiOutput.toString(AnsiColor.CYAN, strokes) + " : " +
               getColoredPlayer(winner) +
               " beats " +
               getColoredPlayer(looser) +
               " ]";
    }

    public Map<String, Object> toData() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("winner", winner);
        ret.put("nr_strokes", strokes);
        ret.put("looser", looser);
        return ret;
    }

    private String getColoredPlayer(String who) {
        return AnsiOutput.toString(who.equalsIgnoreCase("ping") ?
                                       AnsiColor.BRIGHT_GREEN :
                                       AnsiColor.BRIGHT_RED, who);
    }
}
