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

    public TrackingError createProject(Project project) throws Exception {
        log.info("Start of validateProject");

        Set<String> priorities = dao.getPriorityEnum();

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

        // check for job type
        if (StringUtils.isNotEmpty(project.getJobType()) &&
                StringUtils.isNotEmpty(project.getCostCenter())) {
            errorMessages.addAll(isValidJobType(project.getJobType(), project.getCostCenter()));
        }

        // checking for valid priority
        if (StringUtils.isNotEmpty(project.getPriority()) && !priorities.contains(project.getPriority())) {
            errorMessages.add(project.getPriority() + " is not a valid priority");
        }

        // check for valid part count
        if (project.getPartCount() != null && project.getPartCount() < 1) {
            errorMessages.add("Part Count can not be less than 1");
        }

        if (!errorMessages.isEmpty()) {
            error = new TrackingError();
            error.setErrorMessages(errorMessages);
            error.setStatus(Response.Status.NOT_ACCEPTABLE);
        }
        return error;
    }

    private List<String> isValidJobType(String jobType, String costCenter) throws Exception {

        List<String> errorMessages = new ArrayList<>();
        List<JobType> jobTypes = dao.getJobTypeEnum();
        List<String> costCenters = dao.getCostCentersEnum();

        JobType myType = null;

        for (JobType type : jobTypes) {
            if (jobType == type.getJobType()) {
                myType = type;
                break;
            }
        }
        if (myType == null) {
            errorMessages.add("Invalid Job Type");
        }

        if (!costCenters.contains(myType.getCostCenter())) {
            errorMessages.add("Invalid Cost Center");
        }

        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        if (!myType.getCostCenter().equals(costCenter)) {
            errorMessages.add("Job type " + jobType + " does not belong to the cost center " + costCenter);
        }
        return errorMessages;
    }
}
