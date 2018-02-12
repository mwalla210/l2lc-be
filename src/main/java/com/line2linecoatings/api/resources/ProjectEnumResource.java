package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.services.ProjectEnumService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/project/enum")
public class ProjectEnumResource extends BasicResource {
    public static final Log log = LogFactory.getLog(ProjectEnumResource.class);
    public static ProjectEnumService projectEnumService;

    public ProjectEnumResource() {
        projectEnumService = new ProjectEnumService();
    }

    @GET
    @Path("/cost-center")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCostCenters(@Context HttpHeaders headers) throws Exception {
        List<String> costCenters;

        try {
            costCenters = projectEnumService.getAllCostCenters();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, costCenters);
    }

    @GET
    @Path("/jobtype")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllJobTypes(@Context HttpHeaders headers) throws Exception {
        log.info("Start of getAllJobTypes in Resource");
        List<String> allJobTypes = null;

        try {
            allJobTypes = projectEnumService.getAllJobTypes();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, allJobTypes);
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjectStatuses(@Context HttpHeaders headers) throws Exception {
        log.info("Start of getAllProjectStatuses in Resource");
        List<String> allProjectStatuses = null;

        try {
            allProjectStatuses = projectEnumService.getAllProjectStatuses();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, allProjectStatuses);
    }

    @GET
    @Path("/priority")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjectPriorities(@Context HttpHeaders headers) throws Exception {
        log.info("Start of getAllProjectStatuses in Resource");
        List<String> allPriorities = null;

        try {
            allPriorities = projectEnumService.getAllProjectPriorities();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, allPriorities);
    }
}
