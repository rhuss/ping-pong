package pong;

import java.util.UUID;

import javax.ws.rs.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Path("/pong/{id}")
public class Pong2Endpoint {

    private Logger log = LoggerFactory.getLogger("pong");

    private static String pongId = "sb-" + UUID.randomUUID().toString().substring(0, 8);

    // ==================================================================================
    // Configuration

    @Value("${STRENGTH:2}")
    private int strength;

    // ====================================================================================

    @GET
    @Produces("text/plain")
    public String pong(@PathParam("id") String id) {
        Stroke stroke = Stroke.play(strength);
        return pongId + " " + stroke.toString();
    }
}
