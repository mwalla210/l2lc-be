package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.services.EnumService;
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
public class EnumResource extends BasicResource {
    public static final Log log = LogFactory.getLog(EnumResource.class);
    public static EnumService enumService;

    public EnumResource() {
        enumService = new EnumService();
    }

    @GET
    @Path("/cost-center")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCostCenters(@Context HttpHeaders headers) throws Exception {
        List<String> costCenters;

        try {
            costCenters = enumService.getAllCostCenters();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, costCenters);
    }

    @GET
    @Path("/station")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStations(@Context HttpHeaders headers) throws Exception {
        log.info("Start of getAllStations in Resource");
        List<String> allStations = null;

        try {
            allStations = enumService.getAllStations();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, allStations);
    }

    @GET
    @Path("/jobtype")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllJobTypes(@Context HttpHeaders headers) throws Exception {
        log.info("Start of getAllJobTypes in Resource");
        List<String> allJobTypes = null;

        try {
            allJobTypes = enumService.getAllJobTypes();
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
            allProjectStatuses = enumService.getAllProjectStatuses();
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
            allPriorities = enumService.getAllProjectPriorities();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, allPriorities);
    }
}
