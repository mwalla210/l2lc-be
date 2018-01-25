package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.Employee;
import com.line2linecoatings.api.tracking.services.EmployeeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/employee")
public class EmployeeResource
{
    public static final Log log = LogFactory.getLog(EmployeeResource.class);
    public static EmployeeService employeeService;

    public EmployeeResource() {
        employeeService = new EmployeeService();
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEmployee(Employee employee, @Context HttpHeaders headers) throws Exception {
        Employee createdEmployee = null;
        try {
            createdEmployee = employeeService.createEmployee(employee);
        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }

        return Response.ok(createdEmployee).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployeeById(@PathParam("id") int employeeId, @Context HttpHeaders headers) throws Exception{
        Employee employee = null;
        try {
            employee = employeeService.getEmployee(employeeId);
        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }

        return Response.ok(employee).build();
    }
}
