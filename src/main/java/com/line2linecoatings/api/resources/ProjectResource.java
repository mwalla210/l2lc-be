package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.Page;
import com.line2linecoatings.api.tracking.models.Project;
import com.line2linecoatings.api.tracking.services.ProjectService;
import com.line2linecoatings.api.tracking.utils.TrackingError;
import com.line2linecoatings.api.tracking.utils.TrackingValidationHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/project")
public class ProjectResource extends BasicResource {
    public static final Log log = LogFactory.getLog(ProjectResource.class);
    public static ProjectService projectService;
    public static TrackingValidationHelper validationHelper;

    public ProjectResource() {
        this.projectService = new ProjectService();
        validationHelper = new TrackingValidationHelper();
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject(Project project, @Context HttpHeaders headers) throws Exception {
        Project createdProject = null;

        TrackingError trackingError = validationHelper.createProject(project);

        if (trackingError != null) {
            log.error(headers);
            return getResponse(Response.Status.NOT_ACCEPTABLE, trackingError);
        }

        try {
            createdProject = projectService.createProject(project);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.CREATED, createdProject);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@PathParam("id") int id, @Context HttpHeaders headers) throws Exception {
        Project project = null;

        try {
            project = projectService.getProject(id);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        if (project == null) {
            return getResponse(Response.Status.NOT_FOUND);
        } else {
            return getResponse(Response.Status.OK, project);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjects(@QueryParam("limit") Integer limit,
                                   @QueryParam("offset") int offset,
                                   @Context HttpHeaders headers) throws Exception {
        Page page = null;

        if (limit == null) {
            limit = 50;
        }

        TrackingError error = validationHelper.validatePage(limit, offset);

        if (error != null) {
            return getResponse(Response.Status.NOT_ACCEPTABLE, error);
        }

        try {
            page = projectService.getProjectPage(limit, offset);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, page);
    }
}
