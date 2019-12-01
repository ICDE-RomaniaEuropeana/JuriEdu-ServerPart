package org.icde.juriedu.rest;


import org.icde.juriedu.model.IndexType;
import org.icde.juriedu.model.Question;
import org.icde.juriedu.model.autocompete.AutocompleteResponse;
import org.icde.juriedu.service.SearchService;
import org.icde.juriedu.util.AutocompleteMapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

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
        return esService.search(searchKey.toLowerCase(), IndexType.question, size);
    }


    @GET
    @Path("/autocomplete")
    @Produces(MediaType.APPLICATION_JSON)
    public AutocompleteResponse autocomplete(@QueryParam("search") String searchKey,
                                             @QueryParam("size") @DefaultValue("50") int size) {
        List<Question> questions = esService.autocomplete(searchKey.toLowerCase(), IndexType.question, size);
        return AutocompleteMapper.mapToAutocomplete(questions);
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
