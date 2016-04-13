package jax2016.pong;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/ping/{id}")
public class PongEndpoint {

    @GET
    @Produces("text/plain")
    public String pong(@PathParam("id") String id) {
        Event event = Event.play(1);
        System.out.println(id + "< " + event);
        return event.toString();
    }
}
