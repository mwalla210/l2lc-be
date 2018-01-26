package com.line2linecoatings.api.tracking.models;

/**
 * Created by eriksuman on 1/25/18.
 */
public class Customer {
    public int id;
    public String name;
    public String email;
    public String website;
    public Address shippingAddr;
    public Address billingAddr;
    public Boolean isPastDue;
    public String phoneNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Address getShippingAddr() {
        return shippingAddr;
    }

    public void setShippingAddr(Address shippingAddr) {
        this.shippingAddr = shippingAddr;
    }

    public Address getBillingAddr() {
        return billingAddr;
    }

    public void setBillingAddr(Address billingAddr) {
        this.billingAddr = billingAddr;
    }

    public Boolean getPastDue() {
        return isPastDue;
    }

    public void setPastDue(Boolean pastDue) {
        isPastDue = pastDue;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
