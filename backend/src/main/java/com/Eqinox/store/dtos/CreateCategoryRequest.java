package com.Eqinox.store.dtos;

public class CreateCategoryRequest {

    private String name;
    private String type;

    public CreateCategoryRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
