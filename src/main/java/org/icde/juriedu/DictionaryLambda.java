package org.icde.juriedu;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.icde.juriedu.model.Entry;
import org.icde.juriedu.service.SearchService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Optional;

@Named("dictionary")
public class DictionaryLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    SearchService service;

    ObjectWriter writer = new ObjectMapper().writerFor(Entry.class);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, final Context context) {
        Map<String, String> params = request.getPathParameters();
        Optional<Entry> result = service.getDictionaryEntry(params.get("key"));

        if (! result.isPresent()) {
            return new APIGatewayProxyResponseEvent().withBody("Not found").withStatusCode(404);
        }

        try {
            return new APIGatewayProxyResponseEvent().withBody(writer.writeValueAsString(result.get())).withStatusCode(200);
        } catch (JsonProcessingException e) {
            return new APIGatewayProxyResponseEvent().withBody(e.getMessage()).withStatusCode(500);
        }
    }
}
