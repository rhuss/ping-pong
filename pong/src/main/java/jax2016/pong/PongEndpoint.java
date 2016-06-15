package jax2016.pong;

import java.util.UUID;

import javax.ws.rs.*;

import org.jboss.logging.Logger;

@Path("/pong/{id}")
public class PongEndpoint {

    private static String pongId =
        UUID.randomUUID().toString().substring(0, 8);

    @GET
    @Produces("text/plain")
    public String pong(@PathParam("id") String id) {
        Stroke stroke = Stroke.play(getStrength());
        return pongId + " " + stroke.toString();
    }
    // =========================================================
    // Configuration

    private int getStrength() {
        String strength = System.getenv("STRENGTH");
        return strength == null ? 2 : Integer.parseInt(strength);
    }
}
