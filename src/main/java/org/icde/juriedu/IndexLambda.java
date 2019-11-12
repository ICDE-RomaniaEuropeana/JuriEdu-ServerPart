package org.icde.juriedu;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.icde.juriedu.model.Entry;
import org.icde.juriedu.service.SearchService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@Named("index")
public class IndexLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    SearchService service;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, final Context context) {
        Map<String, String> params = request.getPathParameters();

        try {
            Entry entry = objectMapper.readValue(request.getBody(), Entry.class);
            service.save(entry);
            return new APIGatewayProxyResponseEvent().withBody("OK").withStatusCode(200);
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent().withBody(e.getMessage()).withStatusCode(500);
        }
    }
}
