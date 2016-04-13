package jax2016.ping;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.PostConstruct;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
public class PingClient implements Runnable {

    OkHttpClient client = new OkHttpClient();

    @PostConstruct
    public void start() {
        Thread starter = new Thread(this);
        starter.setDaemon(false);
        starter.start();
    }

    @Override
    public void run() {
        String id = createId();
        try {
            while (true) {
                String response = request("http://localhost:8080/ping/" + id);
                System.out.println(id + "> " + response);
                if (!response.toLowerCase().equals("pong")) {
                    Thread.sleep((long) (Math.random() * 1000 * 5));
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private String createId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0,8);
    }

    private String request(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
