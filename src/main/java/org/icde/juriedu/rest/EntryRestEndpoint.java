package org.icde.juriedu.rest;


import org.icde.juriedu.model.IndexType;
import org.icde.juriedu.model.Question;
import org.icde.juriedu.service.SearchService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("entry")
public class EntryRestEndpoint {

    private final SearchService esService;

    @Inject
    public EntryRestEndpoint(SearchService esService) {
        this.esService = esService;
    }

    @GET
    @Path("/question/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Question> search(@QueryParam("type") IndexType entryType,
                                 @QueryParam("search") String searchKey,
                                 @QueryParam("size") @DefaultValue("50") int size) {
        return esService.search(searchKey, entryType, size);
    }


    @GET
    @Path("/question/autocomplete")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Question> searchQuestion(@QueryParam("type") IndexType entryType,
                                 @QueryParam("search") String searchKey,
                                 @QueryParam("size") @DefaultValue("50") int size) {
        return esService.searchQuestion(searchKey, entryType, size);
    }

    @GET
    @Path("/dictionary/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("key") String key) {
        return esService.getDictionaryEntry(key)
                .map(entry -> Response.ok(entry).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Path("/index/{type}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(List<Question> entries) {
        try {
            esService.save(entries);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
