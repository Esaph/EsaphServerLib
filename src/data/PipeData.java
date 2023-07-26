/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

public abstract class PipeData<T>
{
    public JSONObject getObjectMapJson(T t) throws JsonProcessingException, JSONException
    {
        return new JSONObject(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(t));
    }

    public T createSelfFromMap(String jsonString) throws IOException, NullPointerException
    {
        return new ObjectMapper().readValue(jsonString, (Class<T>)
                ((ParameterizedType)getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0]);
    }
}