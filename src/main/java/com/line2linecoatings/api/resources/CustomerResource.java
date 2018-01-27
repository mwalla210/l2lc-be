package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.Customer;
import com.line2linecoatings.api.tracking.services.CustomerService;
import com.line2linecoatings.api.tracking.utils.TrackingError;
import com.line2linecoatings.api.tracking.utils.TrackingValidationHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/customer")
public class CustomerResource extends BasicResource {
    public static final Log log = LogFactory.getLog(CustomerResource.class);
    public static CustomerService customerService;
    public static TrackingValidationHelper trackingValidationHelper;

    public CustomerResource() {
        customerService = new CustomerService();
        trackingValidationHelper = new TrackingValidationHelper();
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customer customer, @Context HttpHeaders headers) throws Exception {
        TrackingError error = trackingValidationHelper.validateCustomer(customer);
        if (error != null) {
            log.error(headers);
            return getResponse(Response.Status.NOT_ACCEPTABLE, error);
        }

        if (customer.getBillingAddr() == null) {
            customer.setBillingAddr(customer.getShippingAddr());
        }

        try {
            customerService.createCustomer(customer);
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
        return getResponse(Response.Status.CREATED, customer);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") int customerId, @Context HttpHeaders headers) throws Exception {
        Customer customer = null;

        try {
            customer = customerService.getCustomer(customerId);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        if (customer == null) {
            return getResponse(Response.Status.NOT_FOUND);
        }

        return getResponse(Response.Status.OK, customer);
    }
}
