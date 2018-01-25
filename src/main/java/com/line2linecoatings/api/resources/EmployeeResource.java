package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.Employee;
import com.line2linecoatings.api.tracking.services.EmployeeService;
import com.line2linecoatings.api.tracking.utils.TrackingError;
import com.line2linecoatings.api.tracking.utils.TrackingValidationHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/employee")
public class EmployeeResource
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
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
        }
        createdEmployee = employeeService.createEmployee(employee);
        return Response.ok(createdEmployee).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployeeById(@PathParam("id") int employeeId, @Context HttpHeaders headers) throws Exception{
        Employee employee = null;
        employee = employeeService.getEmployee(employeeId);

        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(employee).build();
        }
    }

    @POST
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmployee(@PathParam("id") int id, Employee employee, @Context HttpHeaders headers) throws Exception {
        Employee updatedEmployee = null;

        TrackingError error = trackingValidationHelper.validateEmployee(employee);
        if (error != null) {
            log.error(headers);
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
        }
        
        updatedEmployee = employeeService.updateEmployee(id, employee);

        if (updatedEmployee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(updatedEmployee).build();
        }
    }

    @GET
    @Path("/remove/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeEmployee(@PathParam("id") int id, @Context HttpHeaders headers) throws Exception {
        boolean removed;

        removed = employeeService.removeEmployee(id);
        if (removed) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
