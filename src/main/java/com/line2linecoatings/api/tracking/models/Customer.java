package com.line2linecoatings.api.tracking.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by eriksuman on 1/25/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {
    public Integer id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String website;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Address shippingAddr;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Address billingAddr;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String phoneNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean isPastDue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
