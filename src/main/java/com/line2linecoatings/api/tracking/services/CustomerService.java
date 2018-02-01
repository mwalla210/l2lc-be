package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.Customer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    public Customer updateCustomer(int id, Customer customer) throws Exception {
        log.info("Start of updateCustomer in Service with id " + id);
        if (getCustomer(id) == null) {
            log.error("Customer with id " + id + " not found");
            return null;
        }

        Customer updatedCustomer = trackingDAO.updateCustomer(id, customer);
        log.info("End of updateEmploee in Service with id " + id);
        return updatedCustomer;
    }
}
