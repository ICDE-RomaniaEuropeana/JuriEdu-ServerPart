package org.icde.juriedu.search;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.icde.juriedu.service.SearchService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Named("search")
public class SearchLambda implements RequestHandler<InputObject, OutputObject> {

    @Inject
    SearchService service;

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public OutputObject handleRequest(InputObject input, Context context) {
        return service.search(input.getSearch(), input.getType(), input.getSize()).setRequestId(context.getAwsRequestId());
    }
}
