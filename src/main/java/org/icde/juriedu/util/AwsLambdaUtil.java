package org.icde.juriedu.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AwsLambdaUtil {

    private static final Map<String, String> corsHeaders = new HashMap<>();

    static {
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    public static String getStringParam(String paramName, Map<String, String> params) {
        String param = params.get(paramName);
        if (param == null) {
            throw new IllegalArgumentException("Missing parameter " + paramName);
        }
        return param;
    }

    public static int getIntParam(String paramName, Map<String, String> params) {
        String stringParam = getStringParam(paramName, params);
        try {
            return Integer.parseInt(stringParam);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T extends Enum<T>> T getEnumParam(String paramName, Map<String, String> params, Class<T> enumClass) {
        return Enum.valueOf(enumClass, getStringParam(paramName, params));
    }

    public static APIGatewayProxyResponseEvent response(String body, int statusCode) {
        return new APIGatewayProxyResponseEvent().withBody(body).withStatusCode(statusCode).withHeaders(corsHeaders);
    }

    public static APIGatewayProxyResponseEvent successfulResponse(String body) {
        return response(body, 200);
    }

    public static APIGatewayProxyResponseEvent serverErrorResponse(Exception e) {
        return response("Unexpected server error; details:\n" + e.getMessage(), 500).withHeaders(corsHeaders);
    }

    public static APIGatewayProxyResponseEvent validationResponse(IllegalArgumentException e) {
        return response("Bad input; details: \n" + e.getMessage(), 404).withHeaders(corsHeaders);
    }

    public static APIGatewayProxyResponseEvent execute(Supplier<APIGatewayProxyResponseEvent> supplier) {
        try {
            return supplier.get();
        } catch (IllegalArgumentException e){
            return validationResponse(e);
        } catch (Exception e) {
            return serverErrorResponse(e);
        }
    }
}
