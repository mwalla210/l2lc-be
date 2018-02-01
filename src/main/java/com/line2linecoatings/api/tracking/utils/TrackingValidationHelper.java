package com.line2linecoatings.api.tracking.utils;

import com.line2linecoatings.api.tracking.models.Customer;
import com.line2linecoatings.api.tracking.models.Employee;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class TrackingValidationHelper {
    public static final Log log = LogFactory.getLog(TrackingValidationHelper.class);

    public TrackingError validateEmployee(Employee employee) {

        log.info("Start of validateEmployee");
        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();
        if (StringUtils.isEmpty(employee.getFirstName())) {
            errorMessages.add("Invalid Employee First Name");
        }

        if (StringUtils.isEmpty(employee.getLastName())) {
            errorMessages.add("Invalid Employee Last Name");
        }

        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }
        log.info("End of validateEmployee");
        return error;
    }

    public TrackingError validateCustomer(Customer customer) {
        log.info("Start of validateCustomer");
        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();
        if (StringUtils.isEmpty(customer.getName())) {
            errorMessages.add("Invalid Customer Name");
        }

        if (customer.getShippingAddr() == null) {
            errorMessages.add("Invalid shipping address");
        }

        if (customer.getPastDue() == null) {
            errorMessages.add("Invalid past due");
        }

        if (customer.getPhoneNumber() == null) {
            errorMessages.add("Invalid phone number");
        }

        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }

        log.info("End of validateCustomer");
        return error;
    }

    public TrackingError validatePage(int limit, int offset) {
        log.info("Start of validatePage");
        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();

        if (limit < 1) {
            errorMessages.add("limit can not be less than one");
        }

        if (offset < 0) {
            errorMessages.add("offset can not be less than zero");
        }

        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }
        log.info("End of validatePage");
        return error;
    }
}
