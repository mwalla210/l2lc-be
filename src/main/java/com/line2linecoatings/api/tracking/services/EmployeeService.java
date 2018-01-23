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
    public void createEmployee(Employee employee) throws Exception {
        trackingDAO.createEmployee(employee);
    }

    public Employee getEmployee(int id) throws Exception{
        return trackingDAO.getEmployeeById(id);
    }
}
