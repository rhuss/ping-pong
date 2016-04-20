package jax2016.pong;

import java.util.UUID;

import javax.ws.rs.*;

import org.jboss.logging.Logger;

import static io.undertow.websockets.core.WebSocketFrameType.PONG;

@Path("/pong/{id}")
public class PongEndpoint {

    Logger log = Logger.getLogger("pong");

    private static String pongId = 
                     UUID.randomUUID().toString().substring(0, 8);

    // =============================================================
    // Configuration

    private int getStrength() {
        String strength = System.getenv("STRENGTH");
        return strength == null ? 2 : Integer.parseInt(strength);
    }

    // =============================================================

    @GET
    @Produces("text/plain")
    public String pong(@PathParam("id") String id) {
        Stroke stroke = Stroke.play(getStrength());
        return pongId + " " + stroke.toString();
    }
}
