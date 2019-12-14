package org.icde.juriedu.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class HealthcheckRestEndpoint {
    @GET
    @Path("/_healthcheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() {
        return Response.ok("ok").build();
    }
}
