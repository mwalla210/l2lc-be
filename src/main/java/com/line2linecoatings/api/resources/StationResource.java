package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.Station;
import com.line2linecoatings.api.tracking.services.StationService;
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

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStationById(@PathParam("id") int id, @Context HttpHeaders headers) throws Exception{
        Station station = null;

        try {
            station = stationService.getStation(id);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, station);
    }
}
