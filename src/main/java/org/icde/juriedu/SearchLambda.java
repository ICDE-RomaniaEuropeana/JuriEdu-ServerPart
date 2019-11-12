package org.icde.juriedu;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.icde.juriedu.model.Entry;
import org.icde.juriedu.model.EntryType;
import org.icde.juriedu.service.SearchService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("search")
public class SearchLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    SearchService service;

    ObjectWriter writer = new ObjectMapper().writerFor(List.class);

    int size = 50;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, final Context context) {
        Map<String, String> query = request.getQueryStringParameters();

        Map<String, String> headers = new HashMap<String, String>();

        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Credentials","true");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        try {
            this.size = Integer.parseInt(query.get("size"));
        } catch (NumberFormatException e) {
            // not a valid number
        }
        List<Entry> result = service.search(query.get("search"), EntryType.valueOf(query.get("type")), size);

        try {
            return response.withBody(writer.writeValueAsString(result)).withStatusCode(200);
        } catch (JsonProcessingException e) {
            return response.withBody(e.getMessage()).withStatusCode(500);
        }
    }
}
