package org.icde.juriedu.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.icde.juriedu.model.Entry;

import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to write object " + object + " as json", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read json \n" + json + "\n into class " + cls, e);
        }
    }

    public static Entry fromJsonToEntry(String json) {
        return fromJson(json, Entry.class);
    }
}
