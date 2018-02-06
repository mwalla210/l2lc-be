package com.line2linecoatings.api.tracking.utils;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrackingValidationHelper {
    public static final Log log = LogFactory.getLog(TrackingValidationHelper.class);
    public static TrackingDAOImpl dao;

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

    public TrackingError validatePage(Integer limit, Integer offset) {
        log.info("Start of validatePage");
        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();

        if (limit < 1) {
            errorMessages.add("limit can not be less than one");
        }

        if (offset == null || offset < 0) {
            errorMessages.add("offset can not be less than zero or null");
        }

        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }

        log.info("End of validatePage");
        return error;
    }

    public TrackingError validateUser(User user) throws Exception{
        log.info("Start of validateUser");

        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();
        List<String> stations = dao.getAllStations();

        if (StringUtils.isEmpty(user.getUsername())) {
            errorMessages.add("username can not be empty");
        }

        if (StringUtils.isEmpty(user.getPassword())) {
            errorMessages.add("password can not be empty");
        }

        if (StringUtils.isNotEmpty(user.getStation()) && !stations.contains(user.getStation())) {
            errorMessages.add("Invalid station associated with user");
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

    public TrackingError validateCreatedProject(Project project) throws Exception {
        log.info("Start of validateProject");

        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();

        // checking for required fields
        if (StringUtils.isEmpty(project.getJobType())) {
            errorMessages.add("Project must have a job type");
        }

        if (StringUtils.isEmpty(project.getCostCenter())) {
            errorMessages.add("Project must have a cost center");
        }

        if (StringUtils.isEmpty(project.getTitle())) {
            errorMessages.add("Project must have a title");
        }

        errorMessages.addAll(validateProject(project));

        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }
        return error;
    }

    public TrackingError validateUpdatedProject(Project project) throws Exception {

        TrackingError error = null;
        List<String> errorMessages = new ArrayList<>();

        if (isProjectEmpty(project)) {
            errorMessages.add("Invalid Project Update Object");
        }
        errorMessages.addAll(validateProject(project));

        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }
        return error;

    }

    private boolean isProjectEmpty(Project project) {
        return project.getTitle() == null && project.getJobType() == null && project.getCostCenter() == null &&
                project.getCustomerId() == null && project.getTitle() == null && project.getDescription() == null &&
                project.getPriority() == null && project.getPartCount() == null && project.getRefName() == null;
    }

    private List<String> validateProject(Project project) throws Exception {
        List<String> errorMessages = new ArrayList<>();
        Set<String> priorities = dao.getPriorityEnum();

        // check for valid part count
        if (project.getPartCount() != null && project.getPartCount() < 1) {
            errorMessages.add("Part Count can not be less than 1");
        }

        // check for job type
        if (StringUtils.isNotEmpty(project.getJobType()) && !isValidJobType(project.getJobType())) {
            errorMessages.add(project.getJobType() + " is not a valid Job Type");
        }

        // checking for cost center
        if (StringUtils.isNotEmpty(project.getCostCenter()) && !isValidCostCenter(project.getCostCenter())) {
            errorMessages.add(project.getCostCenter() + " is not a valid cost center");
        }

        // checking for valid priority
        if (StringUtils.isNotEmpty(project.getPriority()) && !priorities.contains(project.getPriority())) {
            errorMessages.add(project.getPriority() + " is not a valid priority");
        }

        // checking for valid Customer
        if (project.getCustomerId() != null && !doesCustomerExist(project.getCustomerId())) {
            errorMessages.add(project.getCustomerId() + " is not a valid customer id");
        }

        return errorMessages;
    }

    private boolean doesCustomerExist(int customerId) throws Exception {
        return !(dao.getCustomerById(customerId)==null);
    }

    private boolean isValidJobType(String jobType) throws Exception {
        return dao.getJobTypeEnum().contains(jobType);
    }

    private boolean isValidCostCenter(String costCenter) throws Exception {
        return dao.getCostCentersEnum().contains(costCenter);
    }
}
