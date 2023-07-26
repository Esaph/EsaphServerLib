/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

import java.io.IOException;

public class ESC
{
    public static void main(String[] args) throws IOException {

        TestParent testChild = new TestParent("BLA", 51);
        JSONObject jsonObject = testChild.getObjectMapJson(testChild);


        TestParent testChildCreateFrom = new TestParent().createSelfFromMap(jsonObject.toString());
        System.out.println(testChildCreateFrom.getName());
    }
}