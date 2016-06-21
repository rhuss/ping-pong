package peng;

import java.util.UUID;

import javax.ws.rs.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Path("/peng/{id}")
public class PengEndpoint {

    private Logger log = LoggerFactory.getLogger("peng");

    private static String pengId = UUID.randomUUID().toString().substring(0, 8);

    // ==================================================================================
    // Configuration

    @Value("${STRENGTH:2}")
    private int strength;

    // ====================================================================================

    @GET
    @Produces("text/plain")
    public String peng(@PathParam("id") String id) {
        Stroke stroke = Stroke.play(strength);
        return pengId + " " + stroke.toString();
    }
}
