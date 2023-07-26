/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CustomDeserializer extends StdDeserializer<TestParent>
{
    protected CustomDeserializer() {
        super(TestParent.class);
    }

    @Override
    public TestParent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        TreeNode node = p.readValueAsTree();

        // Select the concrete class based on the existence of a property
        if (node.get("classAProp") != null) {
            return p.getCodec().treeToValue(node, TestChild.class);
        }

        final int interger = Integer.parseInt(node.get("age").toString());

        TestParent testParent = new TestParent();
        testParent.setName(interger+"");

        return testParent;
    }
}
