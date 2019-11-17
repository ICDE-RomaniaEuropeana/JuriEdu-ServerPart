package org.icde.juriedu;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.elasticsearch.index.Index;
import org.icde.juriedu.model.Question;
import org.icde.juriedu.model.IndexType;
import org.icde.juriedu.service.SearchService;
import org.icde.juriedu.util.AwsLambdaUtil;
import org.icde.juriedu.util.JsonUtil;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.icde.juriedu.util.AwsLambdaUtil.*;

@Named("search")
public class SearchLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    SearchService service;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, final Context context) {
        return execute(() -> {
            Map<String, String> queryParams = request.getQueryStringParameters();
            int size = getIntParam("size", queryParams);
            String searchKey = getStringParam("search", queryParams);
            IndexType indexType = getEnumParam("type", queryParams, IndexType.class);
            List<Question> questions = service.search(searchKey, indexType, size);
            return successfulResponse(JsonUtil.toJson(questions));
        });
    }
}
