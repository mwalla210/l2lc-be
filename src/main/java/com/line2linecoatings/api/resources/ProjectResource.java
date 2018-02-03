package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.Project;
import com.line2linecoatings.api.tracking.services.ProjectService;
import com.line2linecoatings.api.tracking.utils.TrackingError;
import com.line2linecoatings.api.tracking.utils.TrackingValidationHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

//    @POST
//    @Path("/create")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createProject(Project project, @Context HttpHeaders headers) throws Exception {
//        Project createdProject = null;
//
//        TrackingError trackingError = validationHelper.
//    }
}
