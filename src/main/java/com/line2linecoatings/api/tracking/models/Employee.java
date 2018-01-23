package com.line2linecoatings.api.tracking.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee
{
    private int id;
    private String firstName;
    private String lastName;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }
}
