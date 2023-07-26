/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lawsystem.Session;
import org.json.JSONObject;

public class EsaphPipeRequestParser
{
    private static final String ECMD = "ECMD";
    private static final String UserData = "UDAT";
    private static final String SESSION = "SID";

    public static String getCommand(JSONObject jsonObject)
    {
        return jsonObject.getString(EsaphPipeRequestParser.ECMD);
    }

    public static JSONObject getUDATA(JSONObject jsonObject)
    {
        return jsonObject.getJSONObject(EsaphPipeRequestParser.UserData);
    }

    public static Session getSession(JSONObject jsonObject) throws JsonProcessingException
    {
        if(jsonObject.has(EsaphPipeRequestParser.SESSION))
        {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonObject.getJSONObject(EsaphPipeRequestParser.SESSION).toString(), Session.class);
        }
        return null;
    }
}