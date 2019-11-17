package org.icde.juriedu;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.icde.juriedu.service.SearchService;
import org.icde.juriedu.util.AwsLambdaUtil;
import org.icde.juriedu.util.JsonUtil;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static org.icde.juriedu.util.AwsLambdaUtil.*;

@Named("dictionary")
public class DictionaryLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    SearchService service;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, final Context context) {
        return execute(() -> {
            Map<String, String> params = request.getPathParameters();
            String key = AwsLambdaUtil.getStringParam("key", params);
            return service.getDictionaryEntry(key)
                    .map(JsonUtil::toJson)
                    .map(AwsLambdaUtil::successfulResponse)
                    .orElse(response("Not found", 404));
        });
    }
}
