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
        return trackingDAO.createEmployee(employee);
    }

    public Employee getEmployee(int id) throws Exception {
        return trackingDAO.getEmployeeById(id);
    }

    public Employee updateEmployee(int id, Employee employee) throws Exception {

        if (getEmployee(id) == null) {
            log.error("Employee with id " + id + " not found");
            return null;
        } else {
            return trackingDAO.updateEmployee(id, employee);
        }
    }

    public boolean removeEmployee(int id) throws Exception {
        return trackingDAO.removeEmployee(id);
    }
}
