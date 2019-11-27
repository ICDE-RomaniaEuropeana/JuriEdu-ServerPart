package org.icde.juriedu.rest;


import org.icde.juriedu.model.IndexType;
import org.icde.juriedu.model.Question;
import org.icde.juriedu.service.SearchService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("question")
public class QuestionRestEndpoint {

    private final SearchService esService;

    @Inject
    public QuestionRestEndpoint(SearchService esService) {
        this.esService = esService;
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Question> search(@QueryParam("search") String searchKey,
                                 @QueryParam("size") @DefaultValue("50") int size) {
        return esService.search(searchKey, IndexType.question, size);
    }


    @GET
    @Path("/autocomplete")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Question> autocomplete(@QueryParam("search") String searchKey,
                                       @QueryParam("size") @DefaultValue("50") int size) {
        return esService.autocomplete(searchKey, IndexType.question, size);
    }

    @POST
    @Path("/index")
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
