package ping;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.*;

import javax.annotation.PostConstruct;

import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.DnsSrvResolvers;
import com.spotify.dns.LookupResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.fluentd.logger.FluentLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.stereotype.Component;

import static ping.Stroke.*;

@Component
public class PingClient implements Runnable {

    private OkHttpClient client = new OkHttpClient();

    // =====================================================
    // Configuration

    @Value("${STRENGTH:2}")
    private int strength;

    @Value("${WAIT_MAX_SECONDS:3}")
    private int waitMaxSeconds;

    @Value("${PONG_URL}:http//pong/pong")
    private String url;

    // ======================================================
    private String id;

    private static FluentLogger FLUENT_LOG = FluentLogger.getLogger("ping-pong");

    @PostConstruct
    public void start() {
        id = createId();
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        log(AnsiColor.GREEN, "Url " + url);

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
                    logRequest(Stroke.HIT);
                    while (result == null) {
                        nrStrokes++;
                        // Send HTTP request. Returns: <ID> <stroke>
                        String response[] = request(url + "/" + id);
                        Stroke stroke =
                            Stroke.valueOf(response[1].toUpperCase());
                        logResponse(response[0], stroke);

                        // Evaluate stroke and decide on next action
                        result = evaluateStroke(response[0],
                                                nrStrokes, stroke);
                    }
                    logEnd(result);


                    // Send to ElasticSearch via sidecar fluentd
                    FLUENT_LOG.log("result", result.toData());

                    waitABit(waitMaxSeconds);
                    resetConnectionPool();
                } catch (IllegalArgumentException | ConnectException |
		         SocketTimeoutException exp) {
                    waitForNextTry();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private GameResult evaluateStroke(String opponentId,
                                      int nrStrokes, Stroke stroke) {
        if (stroke == MISSED) {
            // Yippie ! We won ...
            return new GameResult(id, opponentId, nrStrokes,
	                          "ping", "pong");
        } else {
            // Check whether we hit the ball ...
            Stroke myStroke = Stroke.play(strength);
            logRequest(myStroke);
            if (myStroke == MISSED) {
                // Oh shit, we loose ...
                return new GameResult(id, opponentId, nrStrokes,
		                      "pong", "ping");
            } else {
                // Next round, please ...
                return null;
            }
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

    private DnsSrvResolver createDnsSrvResolver() {
        return DnsSrvResolvers.newBuilder()
                              .cachingLookups(false)
                              .retainingDataOnFailures(true)
                              .dnsLookupTimeoutMillis(1000)
                              .build();
    }

    private void resetConnectionPool() throws IOException {
        client.connectionPool().evictAll();
    }
    // =================================================================
    // Pretty print

    private void nl() {
        System.out.println();
    }

    private void log(AnsiColor color, String txt) {
        System.out.println(formatLogMsg(color, txt));
    }

    private void logStart() {
        log(AnsiColor.YELLOW, "==== Start Game =======");
    }

    private void logEnd(GameResult result) {
        System.out.println();
        log(AnsiColor.DEFAULT, result.toString());
        log(AnsiColor.YELLOW, "==== End Game =========\n");
    }

    private void logResponse(String opponentId, Stroke stroke) {
        String logLine =
            formatId() +
            "<== " +
            AnsiOutput.toString(AnsiColor.BLUE,stroke) +
            " <== " +
            AnsiOutput.toString(AnsiColor.BRIGHT_RED,"[",opponentId,"] ");
        System.out.println(logLine);
        System.out.flush();
    }

    private void logRequest(Stroke stroke) {
        String logLine =
            formatId() +
            "==> " +
            AnsiOutput.toString(AnsiColor.BLUE,stroke);
        if (stroke == Stroke.HIT) {
            logLine += " ==> " + AnsiOutput.toString(AnsiColor.BRIGHT_RED, "pong");
        }
        System.out.println(logLine);
        System.out.flush();
    }

    private String formatLogMsg(AnsiColor color, String txt) {
        return formatId() + AnsiOutput.toString(color, txt);
    }

    private String formatId() {
        return AnsiOutput.toString(AnsiColor.RED, AnsiStyle.BOLD, "-- V3 -- ") +
               AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, "[", id, "] ");
    }

    private String getUrlViaDns() {
        String srvAddress = "_http._tcp.pong.default.svc.cluster.local";
        DnsSrvResolver dnsResolver = createDnsSrvResolver();
        List<LookupResult> services = dnsResolver.resolve(srvAddress);
        //log(AnsiColor.DEFAULT,"Lookup " + srvAddress + ": " + services);
        if (services.size() !=  0) {
            LookupResult result = services.get(0);;
            return "http://" + result.host() + ":" + result.port() + "/pong";
        } else {
            return null;
        }
    }


}
