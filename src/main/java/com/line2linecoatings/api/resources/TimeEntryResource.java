package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.TimeEntry;
import com.line2linecoatings.api.tracking.services.TimeEntryService;
import com.line2linecoatings.api.tracking.utils.TrackingError;
import com.line2linecoatings.api.tracking.utils.TrackingValidationHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/project/time-entry")
public class TimeEntryResource extends BasicResource {
    public static final Log log = LogFactory.getLog(TimeEntryResource.class);
    public static TimeEntryService timeEntryService;
    public static TrackingValidationHelper validationHelper;
    public TimeEntryResource() {
        timeEntryService = new TimeEntryService();
        validationHelper = new TrackingValidationHelper();
    }

    @POST
    @Path("/create/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTimeEntry(@PathParam("id") int projectId, @QueryParam("employeeId") Integer employeeId,
                                    @QueryParam("station") String station,
                                    @Context HttpHeaders headers) throws Exception {
        TimeEntry timeEntry;
        TrackingError error = validationHelper.validateTimeEntry(projectId, employeeId, station);
        if (error != null) {
            log.error(headers);
            return getResponse(Response.Status.NOT_ACCEPTABLE, error);
        }

        try {
            timeEntry = timeEntryService.createTimeEntry(projectId, employeeId, station);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.CREATED, timeEntry);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimeEntiesByProjectId(@PathParam("id") int projectId,
                                             @Context HttpHeaders headers) throws Exception {
        List<TimeEntry> timeEntries;

        try {
            timeEntries = timeEntryService.getTimeEntries(projectId);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        if (timeEntries == null) {
            return getResponse(Response.Status.NOT_FOUND);
        }

        return getResponse(Response.Status.OK, timeEntries);
    }
}
