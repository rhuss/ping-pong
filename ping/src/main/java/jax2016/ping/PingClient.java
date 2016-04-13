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
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;

import static jax2016.ping.PingPong.*;
import static jax2016.ping.Stroke.*;
import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Component
public class PingClient implements Runnable {

    private OkHttpClient client = new OkHttpClient();

    // ==================================================================================
    // Configuration

    @Value("${host}")
    private String host;

    @Value("${port}")
    private String port;

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

                    log(AnsiColor.RED," ==== Start Game ==============");
                    int nrStrokes = 0;
                    GameResult result = null;
                    while (result == null) {
                        nrStrokes++;
                        Stroke stroke = request(getBaseUrl() + "/ping/" + id);
                        result = checkResult(nrStrokes, PONG, stroke);
                        if (result == null) {
                            result = checkResult(nrStrokes, PING, Stroke.play(5));
                        }

                        System.out.print(".");
                    }
                    System.out.println();
                    log(AnsiColor.GREEN, " Result " + result);
                    log(AnsiColor.RED, " ==== End Game ================");

                    Thread.sleep((long) (Math.random() * 1000 * 5));
                } catch (ConnectException exp) {
                    log(AnsiColor.YELLOW, "No connection. Waiting 5 seconds ...");
                    Thread.sleep(5000);
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void log(AnsiColor color, String txt) {
        System.out.println(AnsiOutput.toString(AnsiColor.BLUE,"[",id,"] ",color, txt));
    }

    private GameResult checkResult(int nrStrokes, PingPong who, Stroke stroke) {
        if (stroke == MISSED) {
            return new GameResult(nrStrokes, who.theOther(), stroke);
        } else if (stroke == OUT) {
            return new GameResult(nrStrokes, who, stroke);
        } else {
            return null;
        }
    }

    private String createId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0,8);
    }

    private Stroke request(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();

        Response response = client.newCall(request).execute();
        return Stroke.valueOf(response.body().string().toUpperCase());
    }

}
