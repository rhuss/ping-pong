package jax2016.ping;

import java.io.IOException;
import java.net.ConnectException;
import java.util.UUID;

import javax.annotation.PostConstruct;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;

import static jax2016.ping.PingPong.*;
import static jax2016.ping.Stroke.*;

@Component
public class PingClient implements Runnable {

    private OkHttpClient client = new OkHttpClient();

    // ==================================================================================
    // Configuration

    @Value("${host}")
    private String host;

    @Value("${port}")
    private String port;

    @Value("${strength}")
    private int strength;

    private String getBaseUrl() {
        return "http://" + host + ":" + port;
    }

    // ====================================================================================

    private String id;

    @PostConstruct
    public void start() {
        id = createId();
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
                        Stroke stroke = request(getBaseUrl() + "/ping/" + id);

                        // Evaluate stroke and decide on next action
                        result = evaluateStroke(nrStrokes, stroke);
                        logDot();
                    }
                    logEnd(result);

                    waitABit();
                } catch (ConnectException exp) {
                    waitForNextTry();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private GameResult evaluateStroke(int nrStrokes, Stroke stroke) {
        GameResult result = checkResult(nrStrokes, PONG, stroke);
        if (result == null) {
            result = checkResult(nrStrokes, PING, Stroke.play(strength));
        }
        return result;
    }

    private GameResult checkResult(int nrStrokes, PingPong who, Stroke stroke) {
        if (stroke == MISSED) {
            return new GameResult(id, nrStrokes, who.theOther(), stroke);
        } else if (stroke == OUT) {
            return new GameResult(id, nrStrokes, who, stroke);
        } else {
            return null;
        }
    }

    private Stroke request(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();

        Response response = client.newCall(request).execute();
        return Stroke.valueOf(response.body().string().toUpperCase());
    }

    private void waitForNextTry() throws InterruptedException {
        nl();
        log(AnsiColor.YELLOW, "No connection. Waiting 5 seconds ...");
        Thread.sleep(5000);
    }

    private void waitABit() throws InterruptedException {
        Thread.sleep((long) (Math.random() * 1000 * 5));
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

    private void logPref() {
        System.out.print(AnsiOutput.toString(AnsiColor.BLUE, "[", id, "] "));
        System.out.flush();
    }

    private void logStart() {
        log(AnsiColor.RED, "==== Start Game ==============");
        logPref();
    }

    private void logEnd(GameResult result) {
        System.out.println();
        log(AnsiColor.GREEN, result.toString());
        log(AnsiColor.RED, "==== End Game ================");
    }

    private void logDot() {
        System.out.print(AnsiOutput.toString(AnsiColor.CYAN, "."));
        System.out.flush();
    }

    private String formatLogMsg(AnsiColor color, String txt) {
        return AnsiOutput.toString(AnsiColor.BLUE, "[", id, "] ", color, txt);
    }



}
