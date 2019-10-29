package org.icde.juriedu.rest;

import org.icde.juriedu.model.Entry;
import org.icde.juriedu.model.EntryType;
import org.icde.juriedu.service.EsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("entry")
public class EntryRestEndpoint {

    private final EsService esService;

    @Inject
    public EntryRestEndpoint(EsService esService) {
        this.esService = esService;
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Entry> search(@QueryParam("type") EntryType entryType,
                              @QueryParam("search") String searchKey,
                              @QueryParam("size") @DefaultValue("50") int size) {
        return esService.search(searchKey, entryType, size);
    }

    @GET
    @Path("/get/dictionary/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("key") String key) {
        return esService.getDictionaryEntry(key)
                .map(entry -> Response.ok(entry).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Path("/save/{type}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(Entry entry) {
        try {
            esService.save(entry);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
