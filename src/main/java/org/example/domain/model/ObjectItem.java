package org.example.domain.model;

public abstract class ObjectItem {
    protected String name;

    public ObjectItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
