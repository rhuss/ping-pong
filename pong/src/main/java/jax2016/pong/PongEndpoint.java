package jax2016.pong;

import javax.ws.rs.*;

import org.jboss.logging.Logger;

@Path("/pong/{id}")
public class PongEndpoint {

    Logger log = Logger.getLogger("pong");

    // ==================================================================================
    // Configuration

    private int getStrength() {
        String strength = System.getenv("PONG_STRENGTH");
        return strength == null ? 2 : Integer.parseInt(strength);
    }

    // ====================================================================================

    @GET
    @Produces("text/plain")
    public String pong(@PathParam("id") String id) {
        Stroke stroke = Stroke.play(getStrength());
        log.info("PONG - " + id + "< " + stroke);
        return stroke.toString();
    }
}
