package org.icde.juriedu;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.icde.juriedu.model.DictionaryEntry;
import org.icde.juriedu.model.IndexType;
import org.icde.juriedu.model.Question;
import org.icde.juriedu.service.SearchService;
import org.icde.juriedu.util.AwsLambdaUtil;
import org.icde.juriedu.util.JsonUtil;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Map;

import static org.icde.juriedu.util.AwsLambdaUtil.*;

@Named("index")
public class IndexLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    SearchService service;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, final Context context) {
        return execute(() -> {
            Map<String, String> pathParameters = request.getPathParameters();
            IndexType indexType = AwsLambdaUtil.getEnumParam("type", pathParameters, IndexType.class);
            if (indexType == IndexType.question) {
                Question question = JsonUtil.fromJsonToQuestion(request.getBody());
                service.save(question);
            } else if (indexType == IndexType.dictionary) {
                DictionaryEntry dictionaryEntry = JsonUtil.fromJson(request.getBody(), DictionaryEntry.class);
                service.save(dictionaryEntry);
            }
            return response("Question successfully saved", 200);
        });
    }
}
