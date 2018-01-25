package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.Station;
import com.line2linecoatings.api.tracking.services.StationService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/station")
public class StationResource extends BasicResource {
    public static final Log log = LogFactory.getLog(StationResource.class);
    public static StationService stationService;

    public StationResource() {
        stationService = new StationService();
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStation(Station station, @Context HttpHeaders headers) throws Exception {
        log.info("Start of createStation in Resource");
        Station createdStation = null;

        try {
            createdStation = stationService.createStation(station);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.CREATED, createdStation);
    }

    @GET
    @Path("/remove/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeStation(@PathParam("id") int id, @Context HttpHeaders headers) throws Exception {
        log.info("Start of removeStation in Resource");
        boolean removed;
        try {
            removed = stationService.removeStation(id);
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
    public Response getAllStations(@Context HttpHeaders headers) throws Exception {
        log.info("Start of getAllStations in Resource");
        List<Station> allStations = null;

        try {
            allStations = stationService.getAllStations();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, allStations);
    }


}
