package org.icde.juriedu.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public class HealthcheckRestEndpoint {
    @GET
    @Path("/_healthcheck")
    @Produces(MediaType.APPLICATION_JSON)
    public String healthCheck() {
        return "ok";
    }
}
