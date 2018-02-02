package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.User;
import com.line2linecoatings.api.tracking.services.UserService;
import com.line2linecoatings.api.tracking.utils.TrackingError;
import com.line2linecoatings.api.tracking.utils.TrackingValidationHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserResource extends BasicResource {
    public static final Log log = LogFactory.getLog(UserResource.class);
    public static UserService userService;
    public static TrackingValidationHelper validationHelper;

    public UserResource() {
        userService = new UserService();
        validationHelper = new TrackingValidationHelper();
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User user, @Context HttpHeaders headers) throws Exception {
        log.info("Start of createUser() in UserResource");
        User createdUser = null;

        TrackingError error = validationHelper.validateUser(user);

        if (error != null) {
            log.error(headers);
            return getResponse(Response.Status.NOT_ACCEPTABLE, error);
        }

        try {
            createdUser = userService.createUser(user);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.CREATED, createdUser);
    }

    @POST
    @Path(("/login"))
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user, @Context HttpHeaders headers) throws Exception {
        log.info("Start of login() in UserResource");

        TrackingError error = validationHelper.validateLogin(user);
        User loggedinUser =  null;
        try {
            loggedinUser = userService.login(user);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        if (loggedinUser == null) {
            return getResponse(Response.Status.UNAUTHORIZED);
        }

        return getResponse(Response.Status.OK, loggedinUser);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") int id, @Context HttpHeaders headers) throws Exception {
        log.info("Start of getUser() with id " + id);
        User user = null;

        try {
            user = userService.getUser(id);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, user);
    }
}
