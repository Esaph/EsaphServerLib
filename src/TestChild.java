/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class TestChild extends TestParent
{
    private String classAProp;

    public TestChild(String name, String childData) {
        super(name, 12);
        this.classAProp = childData;
    }

    public TestChild() {
        super("",0);
    }

    public String getClassAProp() {
        return classAProp;
    }

    public void setClassAProp(String classAProp) {
        this.classAProp = classAProp;
    }
}
