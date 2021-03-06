package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.Employee;
import com.line2linecoatings.api.tracking.models.Page;
import com.line2linecoatings.api.tracking.services.EmployeeService;
import com.line2linecoatings.api.tracking.utils.TrackingError;
import com.line2linecoatings.api.tracking.utils.TrackingValidationHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/employee")
public class EmployeeResource extends BasicResource
{
    public static final Log log = LogFactory.getLog(EmployeeResource.class);
    public static EmployeeService employeeService;
    public static TrackingValidationHelper trackingValidationHelper;
    public EmployeeResource() {
        employeeService = new EmployeeService();
        trackingValidationHelper = new TrackingValidationHelper();
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEmployee(Employee employee, @Context HttpHeaders headers) throws Exception {
        Employee createdEmployee = null;

        TrackingError error = trackingValidationHelper.validateEmployee(employee);
        if (error != null) {
            log.error(headers);
            return getResponse(Response.Status.NOT_ACCEPTABLE, error);
        }

        try {
            createdEmployee = employeeService.createEmployee(employee);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, employee);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployeeById(@PathParam("id") int employeeId, @Context HttpHeaders headers) throws Exception{
        Employee employee = null;
        employee = employeeService.getEmployee(employeeId);

        if (employee == null) {
            return getResponse(Response.Status.NOT_FOUND);
        } else {
            return getResponse(Response.Status.OK, employee);
        }
    }

    @POST
    @Path("/{id}/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmployee(@PathParam("id") int id, Employee employee, @Context HttpHeaders headers) throws Exception {
        Employee updatedEmployee = null;

        TrackingError error = trackingValidationHelper.validateEmployee(employee);
        if (error != null) {
            log.error(headers);
            return getResponse(Response.Status.NOT_ACCEPTABLE, error);
        }

        try {
            updatedEmployee = employeeService.updateEmployee(id, employee);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        if (updatedEmployee == null) {
            return getResponse(Response.Status.NOT_FOUND);
        } else {
            return getResponse(Response.Status.OK, updatedEmployee);
        }
    }

    @DELETE
    @Path("/{id}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeEmployee(@PathParam("id") int id, @Context HttpHeaders headers) throws Exception {
        boolean removed;

        try {
            removed = employeeService.removeEmployee(id);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        if (removed) {
            return getResponse(Response.Status.OK);
        } else {
            return getResponse(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmployees(@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
                                    @Context HttpHeaders headers) throws Exception{
        Page employeePage = null;

        if (limit == null) {
            limit = 50; // default limit
        }

        TrackingError error = trackingValidationHelper.validatePage(limit, offset);

        if (error != null) {
            log.error(headers);
            return getResponse(Response.Status.NOT_ACCEPTABLE, error);
        }

        employeePage = employeeService.getEmployeePage(limit, offset);
        return getResponse(Response.Status.OK, employeePage);
    }
}
