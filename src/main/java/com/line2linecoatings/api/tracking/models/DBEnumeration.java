package com.line2linecoatings.api.tracking.models;

/**
 * Created by eriksuman on 2/11/18.
 */
public abstract class DBEnumeration {
    private Integer id;
    private String name;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
