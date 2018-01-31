package com.line2linecoatings.api.tracking.utils;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.Customer;
import com.line2linecoatings.api.tracking.models.Employee;
import com.line2linecoatings.api.tracking.models.Station;
import com.line2linecoatings.api.tracking.models.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrackingValidationHelper {
    public static final Log log = LogFactory.getLog(TrackingValidationHelper.class);
    public static TrackingDAOImpl dao;
    public static Set<Integer> stationIds;

    public TrackingValidationHelper() {
        dao = new TrackingDAOImpl();
    }

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

    public TrackingError validateUser(User user) throws Exception{
        log.info("Start of validateUser");
        this.stationIds = getStationIds();

        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();

        if (StringUtils.isEmpty(user.getUsername())) {
            errorMessages.add("username can not be empty");
        }

        if (StringUtils.isEmpty(user.getPassword())) {
            errorMessages.add("password can not be empty");
        }

        if (user.isAdmin() == null) {
            errorMessages.add("admin must not be null");
        }

        if (user.getStationId() != null && !stationIds.contains(user.getStationId())) {
            errorMessages.add("Invalid stationId associated with user");
        }

        if (StringUtils.isNotEmpty(user.getUsername()) && dao.doesUsernameExist(user.getUsername())) {
            errorMessages.add("Username already exists.");
        }
        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }
        log.info("End of validateUser");
        return error;
    }

    public TrackingError validateLogin(User user) throws Exception {
        log.info("Start of validateLogin");

        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();

        if (StringUtils.isEmpty(user.getUsername())) {
            errorMessages.add("username can not be empty");
        }

        if (StringUtils.isEmpty(user.getPassword())) {
            errorMessages.add("password can not be empty");
        }

        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }

        log.info("End of validateLogin");
        return error;
    }

    private static Set<Integer> getStationIds() throws Exception{
        log.info("How often does this run");
        List <Station> stations = dao.getAllStations();
        Set <Integer> stationIds = new HashSet<>();

        for (Station station : stations) {
            stationIds.add(station.getId());
        }

        return stationIds;
    }
}
