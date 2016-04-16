package jax2016.ping;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.DnsSrvResolvers;
import com.spotify.dns.LookupResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;

import static jax2016.ping.Stroke.*;

@Component
public class PingClient implements Runnable {

    private OkHttpClient client = new OkHttpClient();

    private DnsSrvResolver dnsResolver;

    // ==================================================================================

    // Configuration

    @Value("${STRENGTH:2}")
    private int strength;

    @Value("${OPPONENT:pong}")
    private String opponent;


    @Value("${WAIT_MAX_SECONDS:5}")
    private int waitMaxSeconds;

    private String getUrl() {
        return "http://" + opponent + ":8080/" + opponent;
    }

    private String getUrlViaDns() {
        String srvAddress = "_http._tcp." + opponent + ".default.svc.cluster.local";
        List<LookupResult> services = dnsResolver.resolve(srvAddress);
        log(AnsiColor.DEFAULT,"Lookup " + srvAddress + ": " + services);
        if (services.size() !=  0) {
            LookupResult result = services.get(0);;
            return "http://" + result.host() + ":" + result.port() + "/" + opponent;
        } else {
            return null;
        }
    }

    // ====================================================================================

    private String id;

    @PostConstruct
    public void start() {
        id = createId();
        log(AnsiColor.GREEN, "Url via DNS: " + getUrlViaDns());
        dnsResolver = DnsSrvResolvers.newBuilder()
                                     .cachingLookups(false)
                                     .retainingDataOnFailures(true)
                                     .dnsLookupTimeoutMillis(1000)
                                     .build();

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    int nrStrokes = 0;
                    GameResult result = null;

                    logStart();
                    while (result == null) {
                        nrStrokes++;
                        // Send HTTP request to PONG
                        String response[] = request(getUrl() + "/" + id);
                        Stroke stroke = Stroke.valueOf(response[1].toUpperCase());

                        // Evaluate stroke and decide on next action
                        result = evaluateStroke(nrStrokes, stroke);
                        logRequest(response[0],stroke);
                    }
                    logEnd(result);

                    waitABit(waitMaxSeconds);
                } catch (ConnectException | SocketTimeoutException exp) {
                    waitForNextTry();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private GameResult evaluateStroke(int nrStrokes, Stroke stroke) {
        GameResult result = checkResult(nrStrokes, new Player(opponent, Player.PING), stroke);
        if (result == null) {
            result = checkResult(nrStrokes, new Player(Player.PING, opponent), Stroke.play(strength));
        }
        return result;
    }

    private GameResult checkResult(int nrStrokes, Player who, Stroke stroke) {
        if (stroke == MISSED) {
            return new GameResult(id, nrStrokes, who.getOpponent(), who.getPlayer(), stroke);
        } else if (stroke == OUT) {
            return new GameResult(id, nrStrokes, who.getPlayer(), who.getOpponent(), stroke);
        } else {
            return null;
        }
    }

    private String[] request(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();

        Response response = client.newCall(request).execute();
        return response.body().string().split("\\s+");
    }

    private void waitForNextTry() throws InterruptedException {
        nl();
        log(AnsiColor.YELLOW, "No connection. Waiting 5 seconds ...");
        Thread.sleep(5000);
    }

    private void waitABit(int secsMax) throws InterruptedException {
        Thread.sleep((long) (Math.random() * 1000 * secsMax));
    }


    private String createId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0,8);
    }

    // ================================================================================
    // Pretty print

    private void nl() {
        System.out.println();
    }

    private void log(AnsiColor color, String txt) {
        System.out.println(formatLogMsg(color, txt));
    }

    private void logStart() {
        log(AnsiColor.RED, "==== Start Game ==============");
    }

    private void logEnd(GameResult result) {
        System.out.println();
        log(AnsiColor.GREEN, result.toString());
        log(AnsiColor.RED, "==== End Game ================");
    }

    private void logRequest(String opponentId, Stroke stroke) {
        String logLine =
            formatId() +
            "<== " +
            AnsiOutput.toString(AnsiColor.YELLOW,stroke) +
            " <== " +
            AnsiOutput.toString(AnsiColor.CYAN,"[",opponentId,"] ");
        System.out.println(logLine);
    }

    private String formatLogMsg(AnsiColor color, String txt) {
        return formatId() + AnsiOutput.toString(color, txt);
    }

    private String formatId() {
        return AnsiOutput.toString(AnsiColor.RED,"-- V2 -- ", AnsiColor.BLUE, "[", id, "] ");
    }

}
