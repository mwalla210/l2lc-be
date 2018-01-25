package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.Employee;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmployeeService {
    public static final Log log = LogFactory.getLog(EmployeeService.class);
    TrackingDAOImpl trackingDAO;

    public EmployeeService() {
        trackingDAO = new TrackingDAOImpl();
    }
    public Employee createEmployee(Employee employee) throws Exception {
        log.info("Start of createEmployee in Service");
        Employee createdEmployee = trackingDAO.createEmployee(employee);
        log.info("End of createEmployee in Service");
        return createdEmployee;

    }

    public Employee getEmployee(int id) throws Exception {
        log.info("Start of getEmployee in Service with id " + id);
        Employee employee = trackingDAO.getEmployeeById(id);
        log.info("End of getEmployee in Service with id " + id);
        return employee;

    }

    public Employee updateEmployee(int id, Employee employee) throws Exception {
        log.info("Start of updateEmployee in Service with id " + id);
        Employee updatedEmployee = null;
        if (getEmployee(id) == null) {
            log.error("Employee with id " + id + " not found");
        } else {
            updatedEmployee = trackingDAO.updateEmployee(id, employee);
        }
        log.info("Start of updateEmployee in Service with id " + id);
        return updatedEmployee;

    }

    public boolean removeEmployee(int id) throws Exception {
        boolean removed;
        log.info("Start of removeEmployee in Service with id " + id);
        removed =  trackingDAO.removeEmployee(id);
        log.info("End of removeEmployee in Service with id " + id);
        return removed;

    }
}
