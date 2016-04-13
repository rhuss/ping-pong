package jax2016.pong;

import javax.ws.rs.*;

@Path("/ping/{id}")
public class PongEndpoint {

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
        System.out.println(id + "< " + stroke);
        return stroke.toString();
    }
}
