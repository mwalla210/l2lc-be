package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.Customer;
import com.line2linecoatings.api.tracking.models.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by eriksuman on 1/25/18.
 */
public class CustomerService {
    public static final Log log = LogFactory.getLog(CustomerService.class);
    TrackingDAOImpl trackingDAO;

    public CustomerService() {
        trackingDAO = new TrackingDAOImpl();
    }

    public Customer createCustomer(Customer customer) throws Exception {
        log.info("Start of createCustomer in Service");
        Customer createdCustomer = trackingDAO.createCustomer(customer);
        log.info("End of createCustomer in Service");
        return createdCustomer;
    }

    public Customer getCustomer(int id) throws Exception {
        log.info("Start of getCustomer in Service");
        Customer customer = trackingDAO.getCustomerById(id);
        log.info("End of getCustomer in Service");
        return customer;
    }

    public Page getCustomerPage(int limit, int offset) throws Exception{
        log.info("Start of getCustomerPage in Service");
        Page customerPage = trackingDAO.getCustomerPage(limit, offset);
        log.info("End of getCustomerPage in Service");
        return customerPage;
    }
}
