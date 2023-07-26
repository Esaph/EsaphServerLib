/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import data.PipeData;

import java.io.Serializable;

@JsonDeserialize(using = CustomDeserializer.class)
public class TestParent extends PipeData<TestParent> implements Serializable
{
    private String name;
    private int age;

    public TestParent(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public TestParent()
    {

    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
