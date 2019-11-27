package org.icde.juriedu.rest;


import org.icde.juriedu.model.DictionaryTerm;
import org.icde.juriedu.model.IndexType;
import org.icde.juriedu.model.Question;
import org.icde.juriedu.service.SearchService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("dictionary")
public class DictionaryRestEndpoint {

    private final SearchService esService;

    @Inject
    public DictionaryRestEndpoint(SearchService esService) {
        this.esService = esService;
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Question> search(@QueryParam("search") String searchKey,
                                 @QueryParam("size") @DefaultValue("50") int size) {
        return esService.search(searchKey, IndexType.dictionary, size);
    }

    @GET
    @Path("/term/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("key") String key) {
        return esService.getDictionaryEntry(key)
                .map(entry -> Response.ok(entry).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Path("/index")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(DictionaryTerm term) {
        try {
            esService.save(term);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
